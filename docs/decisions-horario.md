# decisions-horario.md — Módulo Horario

Registro técnico de decisiones y correcciones aplicadas al módulo de horarios de PetVission.

---

## 1. `HorarioController` devolvía entidades crudas sin `ApiResponse`

### Problema

El controller original tenía dos endpoints que devolvían la entidad `Horario` directamente:

```java
@GetMapping
public List<Horario> listarHorarios() {
    return horarioService.obtenerHorarios();
}

@PostMapping
public Horario crearHorario(@RequestBody Horario horario) {
    return horarioService.guardarHorario(horario);
}
```

Adicionalmente tenía `@CrossOrigin(origins = "*")` a nivel de controller.

### Por qué es un problema

- Devolver `List<Horario>` expone la entidad JPA completa, incluyendo la relación con `UsuarioVeterinario`, que a su vez tiene relación con `Usuario`. Esto genera referencias circulares al serializar.
- Sin `ApiResponse<T>` el frontend no puede asumir una estructura de respuesta uniforme.
- `@CrossOrigin(origins = "*")` a nivel de controller es una mala práctica — CORS debe configurarse globalmente en la clase de seguridad, no en cada controller individualmente.

### Solución

```java
// Antes
@GetMapping
public List<Horario> listarHorarios() {
    return horarioService.obtenerHorarios();
}

// Después
@GetMapping
public ResponseEntity<ApiResponse<List<HorarioResponseDto>>> listarHorarios() {
    return ResponseEntity.ok(
            ApiResponse.success(horarioService.obtenerHorarios())
    );
}
```

Se eliminó `@CrossOrigin` del controller.

### Efecto en el frontend

El frontend siempre recibe `response.data.data` con una lista de `HorarioResponseDto` limpia y predecible.

---

## 2. `HorarioDto` sin separación de Request y Response

### Problema

El proyecto tenía un único `HorarioDto` con campos mezclados de entrada y salida:

```java
public class HorarioDto {
    private Long id;              // campo de respuesta
    private LocalDate fecha;      // campo de ambos
    private LocalTime hora;       // campo de ambos
    private String nombreVeterinario; // campo de respuesta
    private Boolean disponible;   // campo de respuesta
}
```

No había separación entre lo que el frontend envía y lo que el backend devuelve.

### Por qué es un problema

- Un DTO de request no debería tener `id` ni `nombreVeterinario` — esos los genera el backend
- Un DTO de response no debería recibir datos del cliente directamente
- Mezclar ambos hace el código ambiguo y difícil de mantener

### Solución

Se separó en dos DTOs con responsabilidades claras:

**`HorarioRequestDto`** — lo que el frontend envía:
```java
public class HorarioRequestDto {
    @NotNull private Long idVeterinario;
    @NotNull private LocalDate fecha;
    @NotNull private LocalTime hora;
}
```

**`HorarioResponseDto`** — lo que el backend devuelve:
```java
public class HorarioResponseDto {
    private Long id;
    private LocalDate fecha;
    private LocalTime hora;
    private Boolean disponible;
    private Long idVeterinario;
    private String nombreVeterinario;
}
```

### Efecto en el frontend

El frontend sabe exactamente qué campos enviar al crear un horario y qué campos esperar en la respuesta.

---

## 3. `HorarioService` sin mapper ni DTOs

### Problema

El service original operaba directamente con la entidad:

```java
public List<Horario> obtenerHorarios() {
    return horarioRepository.findAll();
}

public Horario guardarHorario(Horario horario) {
    return horarioRepository.save(horario);
}
```

- Recibía y devolvía `Horario` (entidad JPA)
- No había conversión a DTO
- No validaba que el veterinario existiera al crear un horario

### Solución

Se creó `HorarioMapper` y se refactorizó el service para usar DTOs:

```java
public HorarioResponseDto guardarHorario(HorarioRequestDto dto) {
    UsuarioVeterinario veterinario = veterinarioRepository.findById(dto.getIdVeterinario())
            .orElseThrow(() -> new ResourceNotFoundException("Veterinario no encontrado"));

    Horario horario = Horario.builder()
            .fecha(dto.getFecha())
            .hora(dto.getHora())
            .veterinario(veterinario)
            .disponible(true)
            .build();

    return horarioMapper.toDto(horarioRepository.save(horario));
}
```

### Efecto en el frontend

El frontend puede crear horarios enviando solo `idVeterinario`, `fecha` y `hora`, y recibe un `HorarioResponseDto` limpio con toda la información necesaria.

---

## 4. Faltaban endpoints esenciales

### Problema

El controller original solo tenía `GET /api/horarios` y `POST /api/horarios`. No había forma de consultar horarios por veterinario ni de filtrar solo los disponibles.

### Solución

Se agregaron tres endpoints nuevos:

```java
// Horarios de un veterinario específico
GET /api/horarios/veterinario/{idVeterinario}

// Solo los horarios disponibles de un veterinario
GET /api/horarios/veterinario/{idVeterinario}/disponibles

// Desactivar un horario cuando se agenda una cita
PATCH /api/horarios/{id}/desactivar
```

Y en `HorarioRepository` se agregaron los métodos necesarios:

```java
List<Horario> findByVeterinario_IdUsuario(Long idVeterinario);
List<Horario> findByVeterinario_IdUsuarioAndDisponibleTrue(Long idVeterinario);
```

### Efecto en el frontend

El módulo de agendamiento puede consultar `/api/horarios/veterinario/{id}/disponibles` para mostrar solo los slots realmente disponibles de cada veterinario.

---

## 5. Desconexión entre horarios y agendamiento de citas

### Problema

Al agendar una cita en `CitaService.agendarCitaDto()`, el horario correspondiente no se marcaba como no disponible. Esto permitía que dos clientes agendaran la misma hora con el mismo veterinario.

Además, `generarHorariosDisponibles()` era un método hardcodeado que siempre devolvía los mismos tres horarios fijos (9:00, 10:00, 11:00) sin consultar la base de datos.

### Solución

Se inyectó `HorarioRepository` en `CitaService` y se conectó el agendamiento con los horarios:

```java
// Al agendar una cita, marcar el horario como no disponible
horarioRepository
        .findByVeterinario_IdUsuarioAndDisponibleTrue(dto.getIdVeterinario())
        .stream()
        .filter(h -> h.getFecha().equals(dto.getFecha()) && h.getHora().equals(dto.getHora()))
        .findFirst()
        .ifPresent(h -> {
            h.setDisponible(false);
            horarioRepository.save(h);
        });
```

### Efecto en el frontend

Una vez que se agenda una cita, el horario desaparece de la lista de disponibles. El cliente no puede agendar dos citas en el mismo slot.

---

## Lecciones aprendidas

- **Separar siempre Request y Response** — un DTO que mezcla campos de entrada y salida genera ambigüedad
- **El service nunca debe recibir ni devolver entidades JPA** — siempre usar DTOs como entrada y salida
- **Los endpoints deben diseñarse pensando en el frontend** — si el frontend necesita horarios por veterinario, debe existir ese endpoint
- **La lógica de negocio debe ser coherente** — agendar una cita debe afectar la disponibilidad del horario automáticamente
- **No hardcodear datos** — `generarHorariosDisponibles()` con horarios fijos no refleja la realidad del sistema

---

*Módulo desarrollado por integrante del Escuadrón Alpha Mango · Cohorte 24 · Java Generation Chile*