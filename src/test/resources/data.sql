INSERT INTO provincias(nombre)
VALUES ('Por registrar' /*0*/),
       ('Bocas del Toro'), /*1*/
       (N'Coclé'), /*2*/
       (N'Colón'), /*3*/
       (N'Chiriquí'), /*4*/
       (N'Darién'), /*5*/
       ('Herrera'), /*6*/
       ('Los Santos'), /*7*/
       (N'Panamá'), /*8*/
       ('Veraguas'), /*9*/
       ('Guna Yala'), /*10*/
       (N'Emberá-Wounaan'), /*11*/
       (N'Ngäbe-Buglé'),/*12*/
       (N'Panamá Oeste'), /*13*/
       (N'Naso Tjër Di'), /*14*/
       (N'Guna de Madugandí'), /*15*/
       (N'Guna de Wargandí'), /*16*/
       ('Extranjero'); /* 17 */
GO
INSERT INTO distritos(nombre, provincia)
VALUES ('Por registrar', 0),
       ('Aguadulce', 2),
       ('Alanje', 4),
       ('Almirante', 1),
       (N'Antón', 2),
       (N'Arraiján', 13),
       ('Atalaya', 9),
       ('Balboa', 8),
       (N'Barú', 4),
       (N'Besikó', 12),
       ('Bocas del Toro', 1),
       (N'Boquerón', 4),
       ('Boquete', 4),
       ('Bugaba', 4),
       ('Calobre', 9),
       (N'Calovébora', 12),
       (N'Cañazas', 9),
       ('Capira', 13),
       ('Chagres', 3),
       ('Chame', 13),
       ('Changuinola', 1),
       ('Chepigana', 5),
       ('Chepo', 8),
       (N'Chimán', 8),
       (N'Chiriquí Grande', 1),
       (N'Chitré', 6),
       (N'Colón', 3),
       (N'Cémaco', 11),
       ('David', 4),
       ('Donoso', 3),
       ('Dolega', 4),
       ('Gualaca', 4),
       (N'Guararé', 7),
       (N'Guna de Wargandí', 5),
       ('Jirondai', 12),
       (N'Kankintú', 12),
       (N'Kusapín', 12),
       ('La Chorrera', 13),
       ('La Mesa', 9),
       ('La Pintada', 2),
       ('Las Minas', 6),
       ('Las Palmas', 9),
       ('Las Tablas', 7),
       ('Los Pozos', 6),
       ('Los Santos', 7),
       ('Macaracas', 7),
       ('Mariato', 9),
       (N'Mironó', 12),
       ('Montijo', 9),
       (N'Müna', 12),
       (N'Natá', 2),
       ('Nole Duima', 12),
       (N'Ñürüm', 12),
       (N'Ocú', 6),
       (N'Olá', 2),
       ('Omar Torrijos Herrera', 3),
       (N'Panamá', 8),
       ('Parita', 6),
       (N'Pedasí', 7),
       (N'Penonomé', 2),
       (N'Pesé', 6),
       ('Pinogana', 5),
       (N'Pocrí', 7),
       ('Portobelo', 3),
       ('Remedios', 4),
       ('Renacimiento', 4),
       (N'Río de Jesús', 9),
       (N'Sambú', 11),
       ('San Carlos', 13),
       (N'San Félix', 4),
       ('San Francisco', 9),
       ('San Lorenzo', 4),
       ('San Miguelito', 8),
       ('Santa Catalina', 12),
       ('Santa Fe', 5),
       ('Santa Fe', 9),
       ('Santa Isabel', 3),
       (N'Santa María', 6),
       ('Santiago', 9),
       (N'Soná', 9),
       ('Taboga', 8),
       ('Tierras Altas', 4),
       (N'Tolé', 4),
       (N'Tonosí', 7),
       (N'Naso Tjër Di', 14),
       ('Extranjero', 17)
GO
INSERT INTO direcciones (direccion, distrito)
VALUES ('Por registrar', 0);
INSERT INTO vacunas (nombre, edad_minima_dias, intervalo_dosis_1_2_dias)
VALUES ('Por registrar', NULL, NULL);
GO
INSERT INTO roles (nombre, descripcion)
VALUES ('PACIENTE', N'Usuario que recibe tratamiento y consulta información médica.'),
       ('FABRICANTE', 'Persona o empresa que produce o distribuye vacunas.'),
       ('ENFERMERA', N'Especialista en cuidados y asistencia directa a pacientes.'),
       ('DOCTOR', N'Profesional médico que diagnostica y trata a los pacientes.'),
       ('ADMINISTRATIVO', N'Responsable de la atención, gestión, planificación de la institución.'),
       ('DEVELOPER', 'Administra y desarrolla aplicaciones, bases de datos y sistemas.'),
       ('AUTORIDAD', N'Persona con poderes de decisión y supervisión en la institución.')
GO
INSERT INTO permisos (nombre, descripcion)
VALUES ('PACIENTE_READ', N'Permite leer datos básicos de pacientes y sus referencias médicas.'),
       ('MED_READ', N'Permite leer datos médicos detallados de pacientes.'),
       ('MED_WRITE', N'Permite añadir o modificar datos médicos.'),
       ('USER_MANAGER_WRITE', N'Permite gestionar los usuarios, sin incluir restricciones a los mismos.'),
       ('USER_MANAGER_READ', 'Permite leer los datos de los usuarios.'),
       ('FABRICANTE_READ', 'Permite leer los datos generales del fabricante de vacunas.'),
       ('FABRICANTE_WRITE',
        N'Permite gestionar datos relacionados a las vacunas ofrecidos y referencias médicas de las mismas.'),
       ('ADMINISTRATIVO_WRITE', N'Permite gestionar usuarios y configuraciones de enfermedades, síntomas y vacunas.'),
       ('AUTORIDAD_READ', N'Permite supervisar todos los datos.'),
       ('AUTORIDAD_WRITE', N'Permite modificar todos los datos sin restricciones de lógica del negocio o permisos.'),
       ('DEV_DB_ADMIN', N'Permite administrar, configurar y desarrollar la base de datos.'),
       ('GUEST_READ', N'Permite leer datos generales de la base de datos. Información no sensitiva ni confidencial.')
GO
INSERT INTO roles_permisos (rol, permiso)
VALUES (1, 1),  -- Paciente, PACIENTE_READ
       (4, 2),  -- Doctor, MED_READ
       (4, 3),  -- Doctor, MED_WRITE
       (4, 4),  -- Doctor, USER_MANAGER_WRITE
       (4, 5),  -- Doctor, USER_MANAGER_READ
       (3, 2),  -- Enfermera, MED_READ
       (3, 3),  -- Enfermera, MED_WRITE
       (5, 8),  -- Administrativo, ADMINISTRATIVO_WRITE
       (5, 5),  -- Administrativo, USER_MANAGER_READ
       (5, 12), -- Administrativo, GUEST_READ
       (7, 9),  -- Autoridad, AUTORIDAD_READ
       (7, 10), -- Autoridad, AUTORIDAD_WRITE
       (6, 11), -- Developer, DEV_DB_ADMIN
       (2, 6),  -- Fabricante, FABRICANTE_READ
       (2, 7); -- Fabricante, FABRICANTE_WRITE
GO
INSERT INTO vacunas (nombre, edad_minima_dias, intervalo_dosis_1_2_dias)
VALUES ('Adacel', 132 * 30.44, NULL),
       ('BCG', 0 * 30.44, NULL),
       ('COVID-19', 6 * 30.44, 0.92 * 30.44),
       ('Fiebre Amarilla', NULL, NULL),
       ('Hep A (Euvax) (adultos)', 240 * 30.44, 6 * 30.44),
       ('Hep A (Euvax) (infantil)', 12 * 30.44, NULL),
       ('Hep B (adultos)', 240 * 30.44, 6 * 30.44),
       ('Hep B (infantil)', 0, 1 * 30.44),
       ('Hexaxim', 2 * 30.44, NULL),
       ('Influenza (FluQuadri)', 6 * 30.44, 12 * 30.44),
       ('Meningococo', 132 * 30.44, 48 * 30.44),
       ('MMR', 12 * 30.44, NULL),
       (N'MR (antisarampión, antirrubéola)', 12 * 30.44, 72 * 30.44),
       ('Neumoco conjugado (Prevenar 13 valente)', 2 * 30.44, NULL),
       ('Papiloma Humano (Gardasil)', 132 * 30.44, NULL),
       ('Pneumo23', 780 * 30.44, NULL),
       ('Pneumovax', 780 * 30.44, NULL),
       ('Priorix', 9 * 30.44, 3 * 30.44),
       ('Rotarix', 2 * 30.44, NULL),
       ('TD', 48 * 30.44, 120 * 30.44),
       ('Tetravalente', NULL, NULL), -- No se especifica la edad mínima y el intervalo es según el calendario infantil
       ('Varivax', 12 * 30.44, 69 * 30.44),
       ('Verorab', NULL, NULL); -- Según el esquema de post-exposición
GO
