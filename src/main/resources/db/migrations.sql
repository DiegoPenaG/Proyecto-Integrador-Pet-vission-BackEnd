-- ============================================================
-- PetVision — Migraciones de Notificaciones
-- Ejecutar manualmente o con Flyway/Liquibase si se integra.
-- Hibernate ddl-auto: update ya crea estas tablas en dev.
-- ============================================================

-- 1. Preferencias de notificación por usuario
CREATE TABLE IF NOT EXISTS usuario_notificacion_pref (
    id                  BIGSERIAL PRIMARY KEY,
    id_usuario          BIGINT NOT NULL UNIQUE REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    email_habilitado    BOOLEAN NOT NULL DEFAULT TRUE,
    dias_recordatorio   INTEGER NOT NULL DEFAULT 7,
    recordatorio7dias   BOOLEAN NOT NULL DEFAULT TRUE,
    recordatorio1dia    BOOLEAN NOT NULL DEFAULT TRUE,
    confirmacion_reserva BOOLEAN NOT NULL DEFAULT TRUE,
    cancelacion_reserva  BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Log de notificaciones enviadas por cita
CREATE TABLE IF NOT EXISTS cita_notificacion_log (
    id              BIGSERIAL PRIMARY KEY,
    id_cita         BIGINT NOT NULL REFERENCES reserva(id_reserva) ON DELETE CASCADE,
    canal           VARCHAR(20) NOT NULL CHECK (canal IN ('EMAIL')),
    estado          VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'ENVIADO', 'ERROR')),
    fecha_envio     TIMESTAMP,
    error_message   TEXT
);

-- 3. Token de confirmación en recordatorio (si no existe ya)
ALTER TABLE recordatorio
    ADD COLUMN IF NOT EXISTS confirmation_token VARCHAR(255) UNIQUE;

-- 4. Columna estado en reserva ya existe como ENUM en JPA.
--    Si se requiere conversión explícita a tipo PostgreSQL:
-- ALTER TABLE reserva ALTER COLUMN estado TYPE VARCHAR(20);

-- 5. Columna fecha_confirmacion en recordatorio ya existe.
--    Verificar con: \d recordatorio
