-- V3__insert_salones.sql
-- Inserción de salones por sucursal
-- Gestor de sucursal maneja salones premium
-- Gestor de salones maneja salones estándar

-- Salones Bogotá (sucursal_id=1, gestor_sucursal=2 Carlos, gestor_salones=3 Ana)
INSERT INTO salones (nombre, capacidad_maxima, costo_por_hora, activo, sucursal_id, gestor_id) VALUES
('Salón Ejecutivo', 25, 50000, true, 1, 3),
('Salón Empresarial', 50, 80000, true, 1, 3),
('Salón Conferencias', 100, 120000, true, 1, 3),
('Salón VIP', 150, 150000, true, 1, 2),
('Salón Magna', 200, 200000, true, 1, 2);

-- Salones Medellín (sucursal_id=2, gestor_sucursal=4 Jorge, gestor_salones=5 Laura)
INSERT INTO salones (nombre, capacidad_maxima, costo_por_hora, activo, sucursal_id, gestor_id) VALUES
('Salón Ejecutivo', 25, 50000, true, 2, 5),
('Salón Empresarial', 50, 80000, true, 2, 5),
('Salón Conferencias', 100, 120000, true, 2, 5),
('Salón VIP', 150, 150000, true, 2, 4);

-- Salones Cali (sucursal_id=3, gestor_sucursal=6 Andrés, gestor_salones=7 Valentina)
INSERT INTO salones (nombre, capacidad_maxima, costo_por_hora, activo, sucursal_id, gestor_id) VALUES
('Salón Ejecutivo', 25, 50000, true, 3, 7),
('Salón Empresarial', 50, 80000, true, 3, 7),
('Salón Conferencias', 100, 120000, true, 3, 7),
('Salón VIP', 150, 150000, true, 3, 6);

-- Salones Cúcuta (sucursal_id=4, gestor_sucursal=8 Miguel, gestor_salones=9 Daniela)
INSERT INTO salones (nombre, capacidad_maxima, costo_por_hora, activo, sucursal_id, gestor_id) VALUES
('Salón Ejecutivo', 25, 50000, true, 4, 9),
('Salón Empresarial', 50, 80000, true, 4, 9),
('Salón Conferencias', 100, 120000, true, 4, 8);