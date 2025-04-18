INSERT INTO roles (id, nombre, descripcion)
VALUES (1, 'PACIENTE', 'Usuario que recibe tratamiento y consulta información médica.'),
       (2, 'FABRICANTE', 'Persona o empresa que produce o distribuye vacunas.'),
       (3, 'ENFERMERA', 'Especialista en cuidados y asistencia directa a pacientes.'),
       (4, 'DOCTOR', 'Profesional médico que diagnostica y trata a los pacientes.'),
       (5, 'ADMINISTRATIVO', 'Responsable de la atención, gestión, planificación de la institución.'),
       (6, 'DEVELOPER', 'Administra y desarrolla aplicaciones, bases de datos y sistemas.'),
       (7, 'AUTORIDAD', 'Persona con poderes de decisión y supervisión en la institución.');

INSERT INTO permisos (id, nombre, descripcion)
VALUES (1, 'PACIENTE_READ', 'Permite leer datos básicos de pacientes y sus referencias médicas.'),
       (2, 'MED_READ', 'Permite leer datos médicos detallados de pacientes.'),
       (3, 'MED_WRITE', 'Permite añadir o modificar datos médicos.'),
       (4, 'USER_MANAGER_WRITE', 'Permite gestionar los usuarios, sin incluir restricciones a los mismos.'),
       (5, 'USER_MANAGER_READ', 'Permite leer los datos de los usuarios.'),
       (6, 'FABRICANTE_READ', 'Permite leer los datos generales del fabricante de vacunas.'),
       (7, 'FABRICANTE_WRITE',
        'Permite gestionar datos relacionados a las vacunas ofrecidos y referencias médicas de las mismas.'),
       (8, 'ADMINISTRATIVO_WRITE', 'Permite gestionar usuarios y configuraciones de enfermedades, síntomas y vacunas.'),
       (9, 'AUTORIDAD_READ', 'Permite supervisar todos los datos.'),
       (10, 'AUTORIDAD_WRITE', 'Permite modificar todos los datos sin restricciones de lógica del necio o permisos.'),
       (11, 'DEV_DB_ADMIN', 'Permite administrar, configurar y desarrollar la base de datos.'),
       (12, 'GUEST_READ', 'Permite leer datos generales de la base de datos. Información no sensitiva ni confidencial.');

INSERT INTO roles_permisos (rol, permiso)
VALUES (1, 1),
       (4, 2),
       (4, 3),
       (4, 4),
       (4, 5),
       (3, 2),
       (3, 3),
       (5, 8),
       (5, 5),
       (5, 12),
       (7, 9),
       (7, 10),
       (6, 11),
       (2, 6),
       (2, 7);

INSERT INTO vacunas (id, nombre, edad_minima_dias)
VALUES ('123e4567-e89b-12d3-a456-426614174002', 'Adacel', 132 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174003', 'BCG', 0 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174004', 'COVID-19', 6 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174005', 'Fiebre Amarilla', 0),
       ('123e4567-e89b-12d3-a456-426614174006', 'Hep A (Euvax) (adultos)', 240 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174007', 'Hep A (Euvax) (infantil)', 12 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174008', 'Hep B (adultos)', 240 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174009', 'Hep B (infantil)', 0),
       ('123e4567-e89b-12d3-a456-426614174010', 'Hexaxim', 2 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174011', 'Influenza (FluQuadri)', 6 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174012', 'Meningococo', 132 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174013', 'MMR', 12 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174014', 'MR (antisarampión, antirrubéola)', 12 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174015', 'Neumoco conjugado (Prevenar 13 valente)', 2 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174016', 'Papiloma Humano (Gardasil)', 132 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174017', 'Pneumo23', 780 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174018', 'Pneumovax', 780 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174019', 'Priorix', 9 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174020', 'Rotarix', 2 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174021', 'TD', 48 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174022', 'Tetravalente', 0),
       ('123e4567-e89b-12d3-a456-426614174023', 'Varivax', 12 * 30.44),
       ('123e4567-e89b-12d3-a456-426614174024', 'Verorab', 0);

-- Insertar 2 sedes para pruebas
CALL sp_vacunas_insertar_entidad_sede('Hospital Santo Tomás', 'MINSA', NULL, '+5075075830', 'ACTIVO','Avenida Balboa y Calle 37 Este', 'Panamá');

CALL sp_vacunas_insertar_entidad_sede('CSS Complejo Metropolitano', 'CSS', NULL, '+5075064000', 'ACTIVO','Vía España, Ciudad de Panamá', 'Panamá');

-- Paciente de prueba con 1 dosis
SELECT sp_vacunas_gestionar_paciente('Elena', 'Cruz','1991-01-15','F','4-4321-123', NULL, p_telefono := '+50760009999',  p_correo := 'elena.cruz@example.com', p_estado := 'ACTIVO', p_direccion_residencial := 'Calle 9, Casa 43', p_distrito_reside := 'Penonomé');
SELECT sp_vacunas_insert_dosis(now()::timestamp, '1', cedula_paciente := '4-4321-123', nombre_vacuna := 'Papiloma Humano (Gardasil)', nombre_sede := 'Hospital Santo Tomás');
