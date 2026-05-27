# Definición de Relaciones - Módulo de Citas PetVission


Definir las relaciones entre las entidades:

- Usuario
- Mascota
- Cita

utilizando Spring Boot + JPA + Hibernate.

---

# Relaciones implementadas

## Relación Usuario - Cita

Un usuario puede tener múltiples citas veterinarias.

Sin embargo:

- cada cita pertenece solamente a un usuario.

### Tipo de relación

```txt
Usuario (1) ------ (N) Cita
```

### Implementación JPA

#### En Usuario.java

```java
@OneToMany(mappedBy = "usuario")
private List<Cita> citas;
```

#### En Cita.java

```java
@ManyToOne
@JoinColumn(name = "usuario_id")
private Usuario usuario;
```

---

## Relación Mascota - Cita

Una mascota puede tener múltiples citas veterinarias.

Sin embargo:

- cada cita pertenece solamente a una mascota.

### Tipo de relación

```txt
Mascota (1) ------ (N) Cita
```

### Implementación JPA

#### En Mascota.java

```java
@OneToMany(mappedBy = "mascota")
private List<Cita> citas;
```

#### En Cita.java

```java
@ManyToOne
@JoinColumn(name = "mascota_id")
private Mascota mascota;
```

---

# Diagrama Entidad-Relación (DER)

```text
+-------------------+
|      USUARIO      |
+-------------------+
| PK id             |
| nombre            |
| correo            |
+-------------------+
          |
          | 1
          |
          | N
+-------------------+
|       CITA        |
+-------------------+
| PK id             |
| fecha             |
| motivo            |
| FK usuario_id     |
| FK mascota_id     |
+-------------------+
          |
          | N
          |
          | 1
+-------------------+
|      MASCOTA      |
+-------------------+
| PK id             |
| nombre            |
| especie           |
+-------------------+
```

---

# Foreign Keys generadas

La tabla `citas` generará automáticamente las siguientes claves foráneas:

```sql
usuario_id
mascota_id
```

relacionadas con:

```txt
usuarios(id)
mascotas(id)
```

---

# Resultado esperado

Con estas relaciones implementadas:

- un usuario podrá tener múltiples citas
- una mascota podrá tener historial de citas
- cada cita quedará asociada correctamente a:
    - un usuario
    - una mascota