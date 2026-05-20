# DECISIONS.md — PetVission Backend

Registro técnico de decisiones, correcciones y lecciones aprendidas durante el desarrollo.
Cada módulo tiene su propio documento detallado.

---

## Índice

| Módulo | Documento | Descripción |
|---|---|---|
| Citas | [decisions-citas.md](docs/decisions-citas.md) | Estandarización de respuestas, DTOs y referencias circulares |
| Horario | [decisions-horario.md](docs/decisions-horario.md) | Refactorización del módulo de horarios y conexión con citas |

---

## Reglas generales del proyecto

Estas reglas aplican a todos los módulos y surgieron de los problemas encontrados durante el desarrollo:

1. **Nunca devolver entidades JPA desde un Controller** — siempre usar DTOs
2. **Todos los endpoints deben devolver `ApiResponse<T>`** — nunca `ResponseEntity<?>` ni entidad cruda
3. **Separar siempre Request y Response en DTOs distintos** — `XRequestDto` para entrada, `XResponseDto` para salida
4. **Los tipos de retorno del Service deben coincidir con lo que espera el Controller**
5. **No usar `@CrossOrigin` en controllers** — configurar CORS en la clase de seguridad global
6. **Los enums deben documentarse** — el frontend debe conocer los valores exactos aceptados
7. **El código comentado no va a producción** — para ver la evolución del código se usa `git history`

---

*Escuadrón Alpha Mango · Cohorte 24 · Java Generation Chile*