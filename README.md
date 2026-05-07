# 🐾 PetVission — Backend

API REST del sistema PetVission, desarrollada con Java 21 + Spring Boot.

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
│
├── PetvissionApplication.java
│
├── security/                          ← todo lo de JWT va aquí
│   ├── config/
│   │   └── SecurityConfig.java        ← rutas públicas vs protegidas
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java
│   └── service/
│       ├── JwtService.java            ← genera y valida tokens
│       └── CustomUserDetailsService.java
│
├── auth/                              ← login y registro
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── AuthRequestDto.java        ← { email, password }
│   │   ├── AuthResponseDto.java       ← { token, usuario }
│   │   └── RegisterRequestDto.java    ← { nombre, email, password, rol }
│   └── service/
│       └── AuthService.java
│
├── usuario/                           ← gestión de usuarios
│   ├── controller/
│   │   └── UsuarioController.java
│   ├── dto/
│   │   ├── UsuarioRequestDto.java
│   │   └── UsuarioResponseDto.java
│   ├── mapper/
│   │   └── UsuarioMapper.java
│   ├── model/
│   │   ├── Usuario.java               ← tabla USUARIO
│   │   ├── Rol.java                   ← tabla ROL
│   │   └── UsuarioVeterinario.java    ← tabla USUARIO_VETERINARIO
│   ├── repository/
│   │   ├── UsuarioRepository.java
│   │   ├── RolRepository.java
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
│   │   └── Cita.java
│   ├── repository/
│   │   └── CitaRepository.java
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
│   │   ├── HistorialClinico.java
│   │   └── ArchivoClinco.java
│   ├── repository/
│   │   ├── AtencionRepository.java
│   │   ├── RecetaRepository.java
│   │   ├── TratamientoRepository.java
│   │   ├── HistorialClinicoRepository.java
│   │   └── ArchivoClinicoRepository.java
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
│   │   ├── Boleta.java
│   │   ├── BoletaPedido.java
│   │   └── BoletaAtencion.java
│   ├── repository/
│   │   ├── PagoRepository.java
│   │   ├── MetodoPagoRepository.java
│   │   ├── BoletaRepository.java
│   │   ├── BoletaPedidoRepository.java
│   │   └── BoletaAtencionRepository.java
│   └── service/
│       └── PagoService.java
│
├── inventario/
│   ├── controller/
│   │   └── InventarioController.java
│   ├── dto/
│   │   ├── ProveedorRequestDto.java
│   │   ├── ProveedorResponseDto.java
│   │   ├── CompraRequestDto.java
│   │   ├── CompraResponseDto.java
│   │   └── MovimientoStockDto.java
│   ├── mapper/
│   │   └── InventarioMapper.java
│   ├── model/
│   │   ├── Proveedor.java
│   │   ├── Compra.java
│   │   ├── DetalleCompra.java
│   │   └── MovimientoStock.java
│   ├── repository/
│   │   ├── ProveedorRepository.java
│   │   ├── CompraRepository.java
│   │   ├── DetalleCompraRepository.java
│   │   └── MovimientoStockRepository.java
│   └── service/
│       └── InventarioService.java
│
└── shared/
    ├── exception/
    │   ├── GlobalExceptionHandler.java
    │   ├── ResourceNotFoundException.java
    │   └── UnauthorizedException.java
    └── response/
        └── ApiResponse.java
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
