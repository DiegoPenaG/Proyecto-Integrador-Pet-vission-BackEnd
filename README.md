# 🐾 PetVission — Backend

API REST del sistema PetVission, desarrollada con Java 21 + Spring Boot.

---

## ⚙️ Requisitos previos

- Java 21 LTS
- Maven 3.0.6
- PostgreSQL 17
- Variables de entorno configuradas

---

## 🚀 Cómo correr el proyecto

1. Clonar el repositorio
```bash
git clone https://github.com/DiegoPenaG/vetvission-backend
```

2. Copiar el archivo de variables de entorno
```bash
cp .env.example .env
```

3. Completar los valores en `.env`
```bash
DB_URL=jdbc:postgresql://localhost:5432/petvission_db
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_password
JWT_SECRET=tu_clave_secreta
```

4. Ejecutar
```bash
./mvnw spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

---

## 📁 Estructura del proyecto

```
src/main/java/com/petvission/
│
├── PetvissionApplication.java
│
├── security/
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java
│   └── service/
│       ├── JwtService.java
│       └── CustomUserDetailsService.java
│
├── auth/
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── AuthRequestDto.java
│   │   ├── AuthResponseDto.java
│   │   └── RegisterRequestDto.java
│   └── service/
│       └── AuthService.java
│
├── rol/
│   ├── model/
│   │   └── Rol.java
│   ├── repository/
│   │   └── RolRepository.java
│   └── service/
│       └── RolService.java
│
├── usuario/
│   ├── controller/
│   │   └── UsuarioController.java
│   ├── dto/
│   │   ├── UsuarioRequestDto.java
│   │   └── UsuarioResponseDto.java
│   ├── mapper/
│   │   └── UsuarioMapper.java
│   ├── model/
│   │   ├── Usuario.java
│   │   └── UsuarioVeterinario.java
│   ├── repository/
│   │   ├── UsuarioRepository.java
│   │   └── UsuarioVeterinarioRepository.java
│   └── service/
│       └── UsuarioService.java
│
├── mascota/
│   ├── controller/
│   │   └── MascotaController.java
│   ├── dto/
│   │   ├── MascotaRequestDto.java
│   │   └── MascotaResponseDto.java
│   ├── mapper/
│   │   └── MascotaMapper.java
│   ├── model/
│   │   └── Mascota.java
│   ├── repository/
│   │   └── MascotaRepository.java
│   └── service/
│       └── MascotaService.java
│
├── cita/
│   ├── controller/
│   │   └── CitaController.java
│   ├── dto/
│   │   ├── CitaRequestDto.java
│   │   └── CitaResponseDto.java
│   ├── mapper/
│   │   └── CitaMapper.java
│   ├── model/
│   │   ├── Cita.java
│   │   └── Recordatorio.java
│   ├── repository/
│   │   ├── CitaRepository.java
│   │   └── RecordatorioRepository.java
│   └── service/
│       └── CitaService.java
│
├── atencion/
│   ├── controller/
│   │   └── AtencionController.java
│   ├── dto/
│   │   ├── AtencionRequestDto.java
│   │   └── AtencionResponseDto.java
│   ├── mapper/
│   │   └── AtencionMapper.java
│   ├── model/
│   │   ├── Atencion.java
│   │   ├── Receta.java
│   │   ├── Tratamiento.java
│   │   └── HistorialClinico.java
│   ├── repository/
│   │   ├── AtencionRepository.java
│   │   ├── RecetaRepository.java
│   │   ├── TratamientoRepository.java
│   │   └── HistorialClinicoRepository.java
│   └── service/
│       └── AtencionService.java
│
├── producto/
│   ├── controller/
│   │   └── ProductoController.java
│   ├── dto/
│   │   ├── ProductoRequestDto.java
│   │   └── ProductoResponseDto.java
│   ├── mapper/
│   │   └── ProductoMapper.java
│   ├── model/
│   │   ├── Producto.java
│   │   └── Categoria.java
│   ├── repository/
│   │   ├── ProductoRepository.java
│   │   └── CategoriaRepository.java
│   └── service/
│       └── ProductoService.java
│
├── pedido/
│   ├── controller/
│   │   └── PedidoController.java
│   ├── dto/
│   │   ├── PedidoRequestDto.java
│   │   ├── PedidoResponseDto.java
│   │   ├── DetallePedidoRequestDto.java
│   │   └── DetallePedidoResponseDto.java
│   ├── mapper/
│   │   └── PedidoMapper.java
│   ├── model/
│   │   ├── Pedido.java
│   │   └── DetallePedido.java
│   ├── repository/
│   │   ├── PedidoRepository.java
│   │   └── DetallePedidoRepository.java
│   └── service/
│       └── PedidoService.java
│
├── pago/
│   ├── controller/
│   │   └── PagoController.java
│   ├── dto/
│   │   ├── PagoRequestDto.java
│   │   ├── PagoResponseDto.java
│   │   └── BoletaResponseDto.java
│   ├── mapper/
│   │   └── PagoMapper.java
│   ├── model/
│   │   ├── Pago.java
│   │   ├── MetodoPago.java
│   │   └── Boleta.java
│   ├── repository/
│   │   ├── PagoRepository.java
│   │   ├── MetodoPagoRepository.java
│   │   └── BoletaRepository.java
│   └── service/
│       └── PagoService.java
│
└── shared/
    ├── exception/
    │   ├── GlobalExceptionHandler.java
    │   ├── ResourceNotFoundException.java
    │   └── UnauthorizedException.java
    ├── health/
    │   └── HealthController.java
    └── response/
        └── ApiResponse.java
```

---

## 📡 Endpoints principales

### Auth — Público
| Método | Ruta | Descripción |
|---|---|---|
| POST | /api/auth/register | Registro de usuario |
| POST | /api/auth/login | Inicio de sesión |

### Veterinaria — Requiere JWT
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/usuarios | Listar usuarios |
| GET | /api/mascotas/usuario/{id} | Mascotas de un usuario |
| GET | /api/citas/mascota/{id} | Citas de una mascota |
| GET | /api/citas/veterinario/{id} | Agenda del veterinario |
| GET | /api/atenciones/cita/{id} | Atención de una cita |
| GET | /api/historial/mascota/{id} | Historial de una mascota |

### E-commerce — Requiere JWT
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/productos | Listar productos |
| GET | /api/pedidos/usuario/{id} | Pedidos de un usuario |
| GET | /api/pagos/usuario/{id} | Pagos de un usuario |

### Sistema
| Método | Ruta | Descripción |
|---|---|---|
| GET | /api/health | Estado del servidor y BD |

---

## 🛠️ Stack

| Tecnología | Versión |
|---|---|
| Java | 21 LTS |
| Spring Boot | Última estable |
| Spring Security | Incluida |
| PostgreSQL | 17 |
| Maven | 3.0.6 |
| JWT | io.jsonwebtoken |
| Lombok | Última estable |

---

## 🔗 Frontend
Repositorio: [vetvission-frontend](#)
