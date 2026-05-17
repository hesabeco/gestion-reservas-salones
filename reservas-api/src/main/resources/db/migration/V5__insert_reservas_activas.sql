-- V5__insert_reservas_activas.sql
-- Reservas activas actuales para probar indicadores en tiempo real

INSERT INTO reservas (documento_cliente, nombre_cliente, fecha_inicio, fecha_fin_estimada, fecha_creacion, asistentes, estado, salon_id) VALUES
('10234567', 'Juan Gómez', '2026-05-17 08:00:00', '2026-05-17 12:00:00', '2026-05-17 07:00:00', 20, 'ACTIVA', 1),
('20345678', 'María López', '2026-05-17 09:00:00', '2026-05-17 13:00:00', '2026-05-17 08:00:00', 40, 'ACTIVA', 2),
('30456789', 'Carlos Ruiz', '2026-05-17 10:00:00', '2026-05-17 15:00:00', '2026-05-17 09:00:00', 80, 'ACTIVA', 3),
('12345678', 'Laura Pineda', '2026-05-17 08:00:00', '2026-05-17 14:00:00', '2026-05-17 07:00:00', 30, 'ACTIVA', 6),
('23456789', 'Sergio Mendoza', '2026-05-17 09:00:00', '2026-05-17 15:00:00', '2026-05-17 08:00:00', 50, 'ACTIVA', 7),
('34567890', 'Patricia Luna', '2026-05-17 10:00:00', '2026-05-17 16:00:00', '2026-05-17 09:00:00', 90, 'ACTIVA', 11),
('45678901', 'Ricardo Fuentes', '2026-05-17 08:00:00', '2026-05-17 20:00:00', '2026-05-17 07:00:00', 20, 'PENDIENTE_APROBACION', 5),
('56789012', 'Gloria Medina', '2026-05-17 09:00:00', '2026-05-17 21:00:00', '2026-05-17 08:00:00', 140, 'PENDIENTE_APROBACION', 4);