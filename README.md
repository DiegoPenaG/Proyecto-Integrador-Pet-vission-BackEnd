# 🐾 PetVission — Backend

API REST del sistema VetVission, desarrollada con Java 21 + Spring Boot.

---

## ⚙️ Requisitos previos

- Java 21+
- Maven 3.8+
- PostgreSQL o cuenta en NeonDB

---

## 🚀 Cómo correr el proyecto

1. Clonar el repositorio
   git clone https://github.com/DiegoPenaG/Petvission-backend

2. Configurar application.properties
   spring.datasource.url=jdbc:postgresql://TU_HOST/petvission
   spring.datasource.username=TU_USUARIO
   spring.datasource.password=TU_PASSWORD
   spring.jpa.hibernate.ddl-auto=update
   jwt.secret=TU_SECRET_KEY
   jwt.expiration=86400000

3. Ejecutar
   ./mvnw spring-boot:run

La API estará disponible en: http://localhost:8080

---

## 📁 Estructura del proyecto
```
src/main/java/com/petvission/
├── auth/
│   ├── controller/AuthController.java
│   ├── service/AuthService.java
│   └── dto/
├── usuarios/
├── mascotas/
├── citas/
├── historial/
├── productos/
├── pedidos/
├── pagos/
├── config/
│   ├── SecurityConfig.java
│   ├── JwtConfig.java
│   └── CorsConfig.java
└── shared/
    ├── exception/
    └── response/ApiResponse.java
```
---

## 📡 Endpoints — Sprint 1

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| POST | /api/auth/register | Registro de usuario | No |
| POST | /api/auth/login | Login — retorna JWT | No |
| GET | /api/usuarios/me | Perfil del usuario | JWT |
| GET | /api/mascotas | Listar mis mascotas | JWT |
| POST | /api/mascotas | Crear mascota | JWT |
| PUT | /api/mascotas/{id} | Editar mascota | JWT |
| DELETE | /api/mascotas/{id} | Eliminar mascota | JWT |

---

## 🗃️ Base de datos

PostgreSQL (NeonDB)  
Script completo: `src/main/resources/db/schema.sql`

---

## 🔗 Frontend
Repositorio: [petvission-frontend](#)
