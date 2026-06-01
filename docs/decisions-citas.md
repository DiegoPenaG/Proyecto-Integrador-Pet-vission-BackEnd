# DECISIONS.md — Backend PetVission

Documento técnico que registra los problemas encontrados durante el desarrollo,
las decisiones tomadas para corregirlos y el impacto que tienen sobre la conexión con el frontend.

---

## 1. Inconsistencia en tipos de retorno del `CitaController`

### Problema

El `CitaController` tenía endpoints que devolvían distintos tipos de respuesta sin ningún estándar:

```java
// Algunos devolvían la entidad cruda
public ResponseEntity<Cita> cancelarCita(...)

// Otros devolvían ResponseEntity<?> sin tipo definido
public ResponseEntity<?> obtenerCitasVeterinario(...)

// Y solo algunos usaban el wrapper ApiResponse
public ResponseEntity<ApiResponse<CitaResponseDto>> agendarCita(...)
```

### Por qué es un problema

Cuando el frontend llama a distintos endpoints, no puede asumir que la respuesta siempre tendrá la misma estructura. Esto obliga a escribir lógica diferente para cada llamada:

```js
// Para un endpoint devuelve data.token
const token = response.data.token

// Para otro devuelve data.data.token
const token = response.data.data.token

// Para otro devuelve directamente el objeto
const token = response.data
```

Esto genera bugs difíciles de rastrear y código inconsistente en el frontend.

### Solución

Estandarizar **todos** los endpoints para que devuelvan `ApiResponse<T>`:

```java
// Antes
public ResponseEntity<Cita> cancelarCita(@PathVariable Long id) {
    return ResponseEntity.ok(citaService.cancelarCita(id));
}

// Después
public ResponseEntity<ApiResponse<CitaUsuarioDto>> cancelarCita(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success(citaService.cancelarCita(id)));
}
```

### Efecto en el frontend

Con la estandarización, el frontend siempre lee `response.data.data` para obtener los datos:

```js
const { data } = await apiClient.patch(`/citas/${idCita}/cancelar`)
return data.data // siempre es CitaUsuarioDto
```

---

## 2. Referencias circulares en la serialización JSON

### Problema

`obtenerAgendaMensualVeterinario` y `obtenerCitasVeterinario` devolvían `List<Cita>` — la entidad JPA directamente. La entidad `Cita` tiene una relación con `Usuario`, y `Usuario` tiene una colección de `Cita`, creando una referencia circular:

```
Cita → UsuarioVeterinario → Usuario → List<Cita> → UsuarioVeterinario → ...
```

Esto causaba que Spring intentara serializar infinitamente el objeto, generando un JSON malformado o un error de stack overflow.

### Síntoma en el frontend

```
SyntaxError: JSON.parse: unexpected character at line 1 column 224574
```

El navegador recibía 227KB de JSON corrupto e infinito que no podía parsear.

### Solución

Nunca devolver entidades JPA directamente desde un Controller. Siempre convertir a DTO antes de retornar:

```java
// Antes — devuelve entidad cruda con referencias circulares
public List<Cita> obtenerCitasVeterinario(Long idVeterinario) {
    return citaRepository.findByVeterinario_IdUsuarioOrderByFechaAscHoraAsc(idVeterinario);
}

// Después — devuelve DTO limpio sin referencias circulares
public List<CitaUsuarioDto> obtenerCitasVeterinario(Long idVeterinario) {
    return citaRepository
            .findByVeterinario_IdUsuarioOrderByFechaAscHoraAsc(idVeterinario)
            .stream()
            .map(cita -> CitaUsuarioDto.builder()
                    .idCita(cita.getIdCita())
                    .nombreCliente(cita.getUsuario().getNombres())
                    .nombreVeterinario(cita.getVeterinario().getUsuario().getNombres())
                    .fecha(cita.getFecha())
                    .hora(cita.getHora())
                    .estado(cita.getEstado())
                    .motivo(cita.getMotivo())
                    .build()
            )
            .toList();
}
```

### Efecto en el frontend

El frontend recibe un JSON limpio, predecible y de tamaño razonable que puede parsear sin errores.

---

## 3. `cancelarCita` y `reprogramarCita` devolvían la entidad `Cita`

### Problema

```java
public Cita cancelarCita(Long idCita) {
    // ...
    return citaRepository.save(cita); // devuelve entidad cruda
}
```

### Por qué es un problema

El frontend necesita actualizar su estado local (caché) cuando una cita es cancelada o reprogramada, sin hacer una nueva consulta al backend. Para eso necesita recibir el objeto actualizado en el formato que entiende — `CitaUsuarioDto`. Si recibe la entidad cruda, o no puede parsearla (referencias circulares) o tiene que mapearla manualmente.

### Solución

```java
public CitaUsuarioDto cancelarCita(Long idCita) {
    Cita cita = citaRepository.findById(idCita)
            .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada"));

    cita.setEstado(EstadoCita.CANCELADA);
    Cita saved = citaRepository.save(cita);

    return CitaUsuarioDto.builder()
            .idCita(saved.getIdCita())
            .nombreCliente(saved.getUsuario().getNombres())
            .nombreVeterinario(saved.getVeterinario().getUsuario().getNombres())
            .fecha(saved.getFecha())
            .hora(saved.getHora())
            .estado(saved.getEstado())
            .motivo(saved.getMotivo())
            .build();
}
```

### Efecto en el frontend

El frontend recibe el objeto actualizado directamente y puede actualizar su caché local:

```js
// En MisCitas.jsx
const handleCancelar = async (idCita) => {
    const citaActualizada = await cancelarCita(idCita)
    updateCita(citaActualizada) // actualiza caché sin llamar al backend de nuevo
}
```

---

## 4. `obtenerAgendaMensualVeterinario` devolvía `List<Cita>`

### Problema

Mismo problema que el punto 2 — la entidad `Cita` tiene relaciones bidireccionales con `UsuarioVeterinario` y `Usuario`, causando referencias circulares al serializar.

### Solución

Aplicar el mismo patrón que `obtenerCitasPorUsuario` que ya usaba `CitaUsuarioDto`:

```java
public List<CitaUsuarioDto> obtenerAgendaMensualVeterinario(Long idVeterinario) {
    return citaRepository
            .findByVeterinarioAndFechaGreaterThanEqualOrderByFechaAscHoraAsc(
                    veterinario, LocalDate.now()
            )
            .stream()
            .map(cita -> CitaUsuarioDto.builder()
                    .idCita(cita.getIdCita())
                    .nombreCliente(cita.getUsuario().getNombres())
                    .nombreVeterinario(cita.getVeterinario().getUsuario().getNombres())
                    .fecha(cita.getFecha())
                    .hora(cita.getHora())
                    .estado(cita.getEstado())
                    .motivo(cita.getMotivo())
                    .build()
            )
            .toList();
}
```

### Efecto en el frontend

El endpoint `/api/citas/agenda/veterinario/{id}` ahora puede ser usado correctamente por el módulo de agendamiento para mostrar la agenda de un veterinario específico.

---

## 5. Enum de roles — spanglish en el backend

### Problema

El enum `NombreRol` tenía los valores `CLIENTE`, `VETERINARIO` y `ADMINISTRADOR`, pero el frontend originalmente enviaba `ADMIN` en el registro. Esto causaba:

```json
{
  "success": false,
  "message": "Error interno del servidor",
  "data": null
}
```

Con el siguiente error en la consola del backend:

```
Cannot deserialize value of type `NombreRol` from String "ADMIN":
not one of the values accepted for Enum class: [CLIENTE, ADMINISTRADOR, VETERINARIO]
```

### Solución

Usar siempre el valor exacto del enum en el frontend:

```js
// Incorrecto
rol: 'ADMIN'

// Correcto
rol: 'ADMINISTRADOR'
```

Y en el frontend mapear el rol correctamente al redirigir tras el login:

```js
if (data.rol === 'ADMINISTRADOR') window.location.href = '/admin/dashboard'
```

### Lección

Los valores de un enum Java son case-sensitive y deben coincidir exactamente con lo que el frontend envía. Cualquier diferencia genera un error de deserialización en Spring.

---

## Reglas generales establecidas

A partir de estos problemas, el equipo establece las siguientes reglas para el desarrollo:

1. **Nunca devolver entidades JPA desde un Controller** — siempre usar DTOs
2. **Todos los endpoints deben devolver `ApiResponse<T>`** — nunca `ResponseEntity<?>` ni la entidad cruda
3. **Los tipos de retorno del Service deben coincidir con lo que espera el Controller** — si el Controller declara `ApiResponse<CitaUsuarioDto>`, el Service debe devolver `CitaUsuarioDto`
4. **Los enums deben documentarse** — el frontend debe conocer los valores exactos aceptados
5. **El código comentado no va a producción** — para ver la evolución del código se usa `git history`

---

*Documento generado durante la migración del frontend vanilla JS → React + Vite*
*Escuadrón Alpha Mango · Cohorte 24 · Java Generation Chile*