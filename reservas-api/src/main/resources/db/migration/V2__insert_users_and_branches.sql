-- V2__insert_users_and_branches.sql
-- Inserción de usuarios (admin y gestores) y sucursales

-- Admin del sistema (password: admin)
INSERT INTO usuarios (nombre, email, contrasenia, rol) VALUES
('Administrador', 'admin@mail.com', '$2a$12$e7yKHxU7gmS1HWO9uywIxeDHWseQjo.6wLlApU3dZCcHRC/jgWX4q', 'ADMIN');

-- Gestores de Bogotá (password: gestor)
INSERT INTO usuarios (nombre, email, contrasenia, rol) VALUES
('Carlos Pérez', 'carlos.perez@eventos.com', '$2a$12$mHlBpl5anhP9M9axmlzQMeKgYN0mZz.NxyAXlHVyPZOiAV9ox/sfi', 'GESTOR'),
('Ana Rodríguez', 'ana.rodriguez@eventos.com', '$2a$12$mHlBpl5anhP9M9axmlzQMeKgYN0mZz.NxyAXlHVyPZOiAV9ox/sfi', 'GESTOR');

-- Gestores de Medellín (password: gestor)
INSERT INTO usuarios (nombre, email, contrasenia, rol) VALUES
('Jorge Martínez', 'jorge.martinez@eventos.com', '$2a$12$mHlBpl5anhP9M9axmlzQMeKgYN0mZz.NxyAXlHVyPZOiAV9ox/sfi', 'GESTOR'),
('Laura Gómez', 'laura.gomez@eventos.com', '$2a$12$mHlBpl5anhP9M9axmlzQMeKgYN0mZz.NxyAXlHVyPZOiAV9ox/sfi', 'GESTOR');

-- Gestores de Cali (password: gestor)
INSERT INTO usuarios (nombre, email, contrasenia, rol) VALUES
('Andrés Torres', 'andres.torres@eventos.com', '$2a$12$mHlBpl5anhP9M9axmlzQMeKgYN0mZz.NxyAXlHVyPZOiAV9ox/sfi', 'GESTOR'),
('Valentina Ríos', 'valentina.rios@eventos.com', '$2a$12$mHlBpl5anhP9M9axmlzQMeKgYN0mZz.NxyAXlHVyPZOiAV9ox/sfi', 'GESTOR');

-- Gestores de Cúcuta (password: gestor)
INSERT INTO usuarios (nombre, email, contrasenia, rol) VALUES
('Miguel Hernández', 'miguel.hernandez@eventos.com', '$2a$12$mHlBpl5anhP9M9axmlzQMeKgYN0mZz.NxyAXlHVyPZOiAV9ox/sfi', 'GESTOR'),
('Daniela Castro', 'daniela.castro@eventos.com', '$2a$12$mHlBpl5anhP9M9axmlzQMeKgYN0mZz.NxyAXlHVyPZOiAV9ox/sfi', 'GESTOR');

-- Sucursales (gestor_id = gestor de sucursal)
INSERT INTO sucursales (nombre, direccion, activo, gestor_id) VALUES
('Eventos Bogotá', 'Cra 7 # 32-16, Bogotá', true, 2),
('Eventos Medellín', 'Calle 10 # 43-25, Medellín', true, 4),
('Eventos Cali', 'Av 6N # 23-45, Cali', true, 6),
('Eventos Cúcuta', 'Calle 10 # 5-32, Cúcuta', true, 8);