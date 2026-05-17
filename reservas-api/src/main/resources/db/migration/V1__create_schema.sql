-- V1__create_schema.sql
-- Creación del esquema completo de la base de datos

-- Tabla de usuarios (gestores y admin)
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    contrasenia VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL CHECK (rol IN ('ADMIN', 'GESTOR'))
);

-- Tabla de sucursales
CREATE TABLE IF NOT EXISTS sucursales (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    direccion VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    gestor_id BIGINT NOT NULL,
    CONSTRAINT fk_sucursal_gestor FOREIGN KEY (gestor_id) REFERENCES usuarios(id)
);

-- Tabla de salones
CREATE TABLE IF NOT EXISTS salones (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    capacidad_maxima INTEGER NOT NULL,
    costo_por_hora FLOAT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    sucursal_id BIGINT NOT NULL,
    gestor_id BIGINT NOT NULL,
    CONSTRAINT fk_salon_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursales(id),
    CONSTRAINT fk_salon_gestor FOREIGN KEY (gestor_id) REFERENCES usuarios(id)
);

-- Tabla de reservas activas
CREATE TABLE IF NOT EXISTS reservas (
    id BIGSERIAL PRIMARY KEY,
    documento_cliente VARCHAR(12) NOT NULL,
    nombre_cliente VARCHAR(255) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin_estimada TIMESTAMP NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    asistentes INTEGER NOT NULL,
    estado VARCHAR(50) NOT NULL CHECK (estado IN ('PENDIENTE_APROBACION','ACTIVA','FINALIZADA','RECHAZADA','EXPIRADA')),
    motivo_rechazo VARCHAR(255),
    salon_id BIGINT NOT NULL,
    CONSTRAINT fk_reserva_salon FOREIGN KEY (salon_id) REFERENCES salones(id)
);

-- Tabla de histórico de reservas finalizadas
CREATE TABLE IF NOT EXISTS historico_reservas (
    id BIGSERIAL PRIMARY KEY,
    reserva_id BIGINT NOT NULL,
    documento_cliente VARCHAR(12) NOT NULL,
    nombre_cliente VARCHAR(255) NOT NULL,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin_estimada TIMESTAMP NOT NULL,
    fecha_fin_real TIMESTAMP NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL,
    asistentes INTEGER NOT NULL,
    total_cobrado FLOAT NOT NULL,
    salon_id BIGINT NOT NULL,
    CONSTRAINT fk_historico_salon FOREIGN KEY (salon_id) REFERENCES salones(id)
);