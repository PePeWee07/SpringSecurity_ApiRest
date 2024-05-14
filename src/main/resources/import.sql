-- INSERT INTO roles (id, nombre) VALUES (1, 'ADMIN');
-- INSERT INTO roles (id, nombre) VALUES (2, 'USER');

-- INSERT INTO permissions (id, name) VALUES (1, 'CREATE');
-- INSERT INTO permissions (id, name) VALUES (2, 'UPDATE');
-- INSERT INTO permissions (id, name) VALUES (3, 'READ');
-- INSERT INTO permissions (id, name) VALUES (4, 'DELETE');

-- INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 1); -- Rol 1 tiene permiso CREATE
-- INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 2); -- Rol 1 tiene permiso UPDATE
-- INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 3); -- Rol 1 tiene permiso READ
-- INSERT INTO roles_permissions (role_id, permission_id) VALUES (1, 4); -- Rol 1 tiene permiso DELETE


-- INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 1); -- Rol 2 tiene permiso CREATE
-- INSERT INTO roles_permissions (role_id, permission_id) VALUES (2, 3); -- Rol 2 tiene permiso READ