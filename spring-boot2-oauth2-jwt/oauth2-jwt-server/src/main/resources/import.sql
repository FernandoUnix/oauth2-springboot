
INSERT INTO usuarios (id,username,password, is_enable, nombre, apellido, email) values (1,'fernando','$2a$10$7lrAGPhbkHzvu3wblP4Db.sh68mQZD2aBK20p2z8ofKv.ZHGufNlq',true,'fernando', 'gus', 'fernando@gmail.com');
INSERT INTO usuarios (id,username,password, is_enable,nombre, apellido, email) values (2,'admin','$2a$10$tX61Q3PbR99WIFykky2v1.UvsgSX2urE6zDpvIviGK9Rp4IxlDMGy',true, 'admin nombre', 'adm', 'adm@gmail.com');

INSERT INTO roles (id,nombre) values (1,'ROLE_USER');
INSERT INTO roles (id,nombre) values (2,'ROLE_ADMIN');

INSERT INTO usuarios_roles (usuario_id, role_id) values (1,1);
INSERT INTO usuarios_roles (usuario_id, role_id) values (2,2);
INSERT INTO usuarios_roles (usuario_id, role_id) values (2,1);