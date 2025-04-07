INSERT INTO provincias(id, nombre)
VALUES (0, 'Por registrar'),
       (1, 'Bocas del Toro'),
       (2, 'Coclé'),
       (3, 'Colón'),
       (4, 'Chiriquí'),
       (5, 'Darién'),
       (6, 'Herrera'),
       (7, 'Los Santos'),
       (8, 'Panamá'),
       (9, 'Veraguas'),
       (10, 'Guna Yala'),
       (11, 'Emberá-Wounaan'),
       (12, 'Ngäbe-Buglé'),
       (13, 'Panamá Oeste'),
       (14, 'Naso Tjër Di'),
       (15, 'Guna de Madugandí'),
       (16, 'Guna de Wargandí'),
       (17, 'Extranjero');

INSERT INTO distritos(id, nombre, provincia)
VALUES (0, 'Por registrar', 0),
       (1, 'Aguadulce', 2),
       (2, 'Alanje', 4),
       (3, 'Almirante', 1),
       (4, 'Antón', 2),
       (5, 'Arraiján', 13),
       (6, 'Atalaya', 9),
       (7, 'Balboa', 8),
       (8, 'Barú', 4),
       (9, 'Besikó', 12),
       (10, 'Bocas del Toro', 1),
       (11, 'Boquerón', 4),
       (12, 'Boquete', 4),
       (13, 'Bugaba', 4),
       (14, 'Calobre', 9),
       (15, 'Calovébora', 12),
       (16, 'Cañazas', 9),
       (17, 'Capira', 13),
       (18, 'Chagres', 3),
       (19, 'Chame', 13),
       (20, 'Changuinola', 1),
       (21, 'Chepigana', 5),
       (22, 'Chepo', 8),
       (23, 'Chimán', 8),
       (24, 'Chiriquí Grande', 1),
       (25, 'Chitré', 6),
       (26, 'Colón', 3),
       (27, 'Cémaco', 11),
       (28, 'David', 4),
       (29, 'Donoso', 3),
       (30, 'Dolega', 4),
       (31, 'Gualaca', 4),
       (32, 'Guararé', 7),
       (33, 'Guna de Wargandí', 5),
       (34, 'Jirondai', 12),
       (35, 'Kankintú', 12),
       (36, 'Kusapín', 12),
       (37, 'La Chorrera', 13),
       (38, 'La Mesa', 9),
       (39, 'La Pintada', 2),
       (40, 'Las Minas', 6),
       (41, 'Las Palmas', 9),
       (42, 'Las Tablas', 7),
       (43, 'Los Pozos', 6),
       (44, 'Los Santos', 7),
       (45, 'Macaracas', 7),
       (46, 'Mariato', 9),
       (47, 'Mironó', 12),
       (48, 'Montijo', 9),
       (49, 'Müna', 12),
       (50, 'Natá', 2),
       (51, 'Nole Duima', 12),
       (52, 'Ñürüm', 12),
       (53, 'Ocú', 6),
       (54, 'Olá', 2),
       (55, 'Omar Torrijos Herrera', 3),
       (56, 'Panamá', 8),
       (57, 'Parita', 6),
       (58, 'Pedasí', 7),
       (59, 'Penonomé', 2),
       (60, 'Pesé', 6),
       (61, 'Pinogana', 5),
       (62, 'Pocrí', 7),
       (63, 'Portobelo', 3),
       (64, 'Remedios', 4),
       (65, 'Renacimiento', 4),
       (66, 'Río de Jesús', 9),
       (67, 'Sambú', 11),
       (68, 'San Carlos', 13),
       (69, 'San Félix', 4),
       (70, 'San Francisco', 9),
       (71, 'San Lorenzo', 4),
       (72, 'San Miguelito', 8),
       (73, 'Santa Catalina', 12),
       (74, 'Santa Fe', 5),
       (75, 'Santa Fe', 9),
       (76, 'Santa Isabel', 3),
       (77, 'Santa María', 6),
       (78, 'Santiago', 9),
       (79, 'Soná', 9),
       (80, 'Taboga', 8),
       (81, 'Tierras Altas', 4),
       (82, 'Tolé', 4),
       (83, 'Tonosí', 7),
       (84, 'Naso Tjër Di', 14),
       (85, 'Extranjero', 17);

INSERT INTO direcciones (id, direccion, distrito, created_at)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'Por registrar', 0, '2025-04-04 12:34:56');

INSERT INTO vacunas (id, nombre, edad_minima_dias)
VALUES ('123e4567-e89b-12d3-a456-426614174001', 'Por registrar', 0);

INSERT INTO roles (id, nombre, descripcion, created_at)
VALUES (1, 'PACIENTE', 'Usuario que recibe tratamiento y consulta información médica.', '2025-04-04 12:34:56'),
       (2, 'FABRICANTE', 'Persona o empresa que produce o distribuye vacunas.', '2025-04-04 12:34:56'),
       (3, 'ENFERMERA', 'Especialista en cuidados y asistencia directa a pacientes.', '2025-04-04 12:34:56'),
       (4, 'DOCTOR', 'Profesional médico que diagnostica y trata a los pacientes.', '2025-04-04 12:34:56'),
       (5, 'ADMINISTRATIVO', 'Responsable de la atención, gestión, planificación de la institución.', '2025-04-04 12:34:56'),
       (6, 'DEVELOPER', 'Administra y desarrolla aplicaciones, bases de datos y sistemas.', '2025-04-04 12:34:56'),
       (7, 'AUTORIDAD', 'Persona con poderes de decisión y supervisión en la institución.', '2025-04-04 12:34:56');

INSERT INTO permisos (id, nombre, descripcion, created_at)
VALUES (1, 'PACIENTE_READ', 'Permite leer datos básicos de pacientes y sus referencias médicas.', '2025-04-04 12:34:56'),
       (2, 'MED_READ', 'Permite leer datos médicos detallados de pacientes.', '2025-04-04 12:34:56'),
       (3, 'MED_WRITE', 'Permite añadir o modificar datos médicos.', '2025-04-04 12:34:56'),
       (4, 'USER_MANAGER_WRITE', 'Permite gestionar los usuarios, sin incluir restricciones a los mismos.', '2025-04-04 12:34:56'),
       (5, 'USER_MANAGER_READ', 'Permite leer los datos de los usuarios.', '2025-04-04 12:34:56'),
       (6, 'FABRICANTE_READ', 'Permite leer los datos generales del fabricante de vacunas.', '2025-04-04 12:34:56'),
       (7, 'FABRICANTE_WRITE',
        'Permite gestionar datos relacionados a las vacunas ofrecidos y referencias médicas de las mismas.', '2025-04-04 12:34:56'),
       (8, 'ADMINISTRATIVO_WRITE', 'Permite gestionar usuarios y configuraciones de enfermedades, síntomas y vacunas.', '2025-04-04 12:34:56'),
       (9, 'AUTORIDAD_READ', 'Permite supervisar todos los datos.', '2025-04-04 12:34:56'),
       (10, 'AUTORIDAD_WRITE', 'Permite modificar todos los datos sin restricciones de lógica del necio o permisos.', '2025-04-04 12:34:56'),
       (11, 'DEV_DB_ADMIN', 'Permite administrar, configurar y desarrollar la base de datos.', '2025-04-04 12:34:56'),
       (12, 'GUEST_READ', 'Permite leer datos generales de la base de datos. Información no sensitiva ni confidencial.', '2025-04-04 12:34:56');

INSERT INTO roles_permisos (rol, permiso, created_at)
VALUES (1, 1, '2025-04-04 12:34:56'),
       (4, 2, '2025-04-04 12:34:56'),
       (4, 3, '2025-04-04 12:34:56'),
       (4, 4, '2025-04-04 12:34:56'),
       (4, 5, '2025-04-04 12:34:56'),
       (3, 2, '2025-04-04 12:34:56'),
       (3, 3, '2025-04-04 12:34:56'),
       (5, 8, '2025-04-04 12:34:56'),
       (5, 5, '2025-04-04 12:34:56'),
       (5, 12, '2025-04-04 12:34:56'),
       (7, 9, '2025-04-04 12:34:56'),
       (7, 10, '2025-04-04 12:34:56'),
       (6, 11, '2025-04-04 12:34:56'),
       (2, 6, '2025-04-04 12:34:56'),
       (2, 7, '2025-04-04 12:34:56');

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
