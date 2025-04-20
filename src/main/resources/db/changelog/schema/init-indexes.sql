-- indexes
CREATE INDEX IF NOT EXISTS ix_direcciones_descripcion ON direcciones (descripcion);

CREATE INDEX IF NOT EXISTS ix_distritos_nombre ON distritos (nombre);

CREATE INDEX IF NOT EXISTS ix_doctores_idoneidad ON doctores (idoneidad);

CREATE INDEX IF NOT EXISTS ix_fabricantes_licencia ON fabricantes (licencia);

CREATE UNIQUE INDEX IF NOT EXISTS ix_fabricantes_usuario ON fabricantes (usuario) WHERE usuario IS NOT NULL;

CREATE INDEX IF NOT EXISTS ix_permisos_nombre ON permisos (nombre);

CREATE INDEX IF NOT EXISTS ix_personas_nombres_apellidos ON personas (nombre, nombre2, apellido1, apellido2);

CREATE UNIQUE INDEX IF NOT EXISTS ix_entidades_correo ON entidades (correo) WHERE correo IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ix_entidades_telefono ON entidades (telefono) WHERE telefono IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ix_pacientes_id_temporal ON pacientes (identificacion_temporal) WHERE identificacion_temporal IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ix_personas_cedula ON personas (cedula) WHERE cedula IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ix_personas_pasaporte ON personas (pasaporte) WHERE pasaporte IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ix_personas_correo ON personas (correo) WHERE correo IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ix_personas_telefono ON personas (telefono) WHERE telefono IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ix_personas_usuario ON personas (usuario) WHERE usuario IS NOT NULL;

CREATE INDEX IF NOT EXISTS ix_provincias_nombre ON provincias (nombre);

CREATE INDEX IF NOT EXISTS ix_sedes_region ON sedes (region) WHERE region IS NOT NULL;

CREATE INDEX IF NOT EXISTS ix_usuarios_username ON usuarios (username);

CREATE INDEX IF NOT EXISTS ix_vacunas_nombre ON vacunas (nombre);
