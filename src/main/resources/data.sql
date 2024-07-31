-------------------------------------------- INSERTAR DATOS --------------------------------------------
-- Insertar permisos
INSERT INTO auth.permissions (name) VALUES ('CREATE');
INSERT INTO auth.permissions (name) VALUES ('READ');
INSERT INTO auth.permissions (name) VALUES ('UPDATE');
INSERT INTO auth.permissions (name) VALUES ('DELETE');

-- Insertar roles y asociarlos con permisos
INSERT INTO auth.roles (name) VALUES ('ADMIN');
INSERT INTO auth.roles (name) VALUES ('USER');

-- Asignar permisos a roles
INSERT INTO auth.roles_permissions (role_id, permission_id)
SELECT (SELECT id FROM auth.roles WHERE name = 'ADMIN'), id FROM auth.permissions;

INSERT INTO auth.roles_permissions (role_id, permission_id)
SELECT (SELECT id FROM auth.roles WHERE name = 'USER'), (SELECT id FROM auth.permissions WHERE name = 'READ');

-- Insertar usuarios
INSERT INTO auth.users (email, name, last_name, phone_number, address, dni, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('pepewee07@gmail.com', 'José', 'Román', '0983439289', 'Av. Pumapungo y Renaciente', '0704713619', '$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6', true, true, true, true);

INSERT INTO auth.users (email, name, last_name, phone_number, address, dni, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)
VALUES ('daniel@gmail.com', 'Daniel', 'Alvarez', '000000', 'Cuenca', 'V0002', '$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6', true, true, true, true);

-- Asignar roles a usuarios
INSERT INTO auth.user_roles (user_id, role_id)
SELECT (SELECT id FROM auth.users WHERE email = 'pepewee07@gmail.com'), (SELECT id FROM auth.roles WHERE name = 'ADMIN');

INSERT INTO auth.user_roles (user_id, role_id)
SELECT (SELECT id FROM auth.users WHERE email = 'pepewee07@gmail.com'), (SELECT id FROM auth.roles WHERE name = 'USER');

INSERT INTO auth.user_roles (user_id, role_id)
SELECT (SELECT id FROM auth.users WHERE email = 'daniel@gmail.com'), (SELECT id FROM auth.roles WHERE name = 'USER');

