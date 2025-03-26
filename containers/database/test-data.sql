USE vacunas;
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

PRINT (N'Insertando datos globales y por defectos');
GO
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
GO
EXEC sp_vacunas_insert_sede 'Por registrar', 'POR_REGISTRAR', NULL, NULL, 'INACTIVO', NULL, NULL, NULL;
GO

INSERT INTO vacunas (nombre, edad_minima_dias, intervalo_dosis_1_2_dias)
VALUES ('Por registrar', NULL, NULL);
GO
INSERT INTO enfermedades (nombre, nivel_gravedad)
VALUES ('Desconocida', NULL);
GO
INSERT INTO sintomas (nombre)
VALUES ('Desconocido');
GO
INSERT INTO enfermedades_sintomas (enfermedad, sintoma)
VALUES
-- Enfermedad desconocida
(0, 0); -- Síntoma desconocido
GO
-- una vacuna por registrar tendrá su enfermedad desconocida y síntomas desconocidos
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Por registrar', NULL, 'Desconocida', NULL;
GO
PRINT ('-----------------------------------FIN------------------------------------------------');
PRINT (N'Insertando datos de prueba');
GO
-- Datos de prueba. Las direcciones se insertan a medida que se requieren.
-- Se recomienda utilizar los procedimientos para insertar, ya que, respeta la lógica y facilita insertar a varias tablas
PRINT ('Insertando enfermedades');
GO
INSERT INTO enfermedades (nombre, nivel_gravedad)
VALUES ('Bacteriemia', 'Alta'),
       ('COVID-19', 'Alta'),
       ('Difteria', 'Alta'),
       (N'Enfermedad meningocócica', 'Alta'),
       (N'Enfermedades neumocócicas', 'Alta'),
       ('Fiebre Amarilla', 'Alta'),
       ('Hepatitis A', 'Moderada'),
       ('Hepatitis B', 'Moderada'),
       ('Hib (Haemophilus influenzae tipo b)', 'Alta'),
       ('Influenza (Gripe)', 'Moderada'),
       ('Meningitis', 'Alta'),
       (N'Neumonía', 'Moderada'),
       ('Paperas', 'Moderada'),
       ('Poliomelitis (Polio)', 'Alta'),
       ('Rabia', 'Alta'),
       ('Rotavirus', 'Moderada'),
       (N'Rubéola', 'Moderada'),
       (N'Sarampión', 'Alta'),
       (N'Tétanos', 'Alta'),
       ('Tos ferina', 'Alta'),
       ('Tuberculosis', 'Alta'),
       ('Varicela', 'Moderada'),
       ('Virus del papiloma humano (VPH)', 'Moderada');
GO
PRINT (N'Insertando síntomas');
GO
INSERT INTO sintomas (nombre)
VALUES ('Ataques de tos'),
       (N'Cáncer de cuello uterino'),
       (N'Confusión'),
       ('Conjuntivitis'),
       ('Convulsiones'),
       ('Diarrea grave'),
       ('Dificultad para respirar'),
       ('Dolor abdominal'),
       ('Dolor de cabeza'),
       ('Dolor de garganta'),
       (N'Dolor e hinchazón en las glándulas salivales'),
       ('Dolor en el pecho'),
       ('Dolor muscular'),
       (N'Erupción cutánea característica'),
       (N'Escalofríos'),
       ('Espasmos'),
       ('Fatiga'),
       ('Fiebre'),
       (N'Formación de una membrana gruesa en la garganta'),
       ('Ganglios inflamados'),
       ('Ictericia'),
       (N'Infección de la sangre'),
       ('Meningitis'),
       (N'Náuseas'),
       (N'Neumonía'),
       ('Orina oscura'),
       (N'Otros tipos de cáncer'),
       (N'Parálisis'),
       (N'Pérdida de peso'),
       (N'Pérdida del gusto o olfato'),
       (N'Picazón'),
       ('Poco apetito'),
       ('Rigidez en el cuello'),
       ('Rigidez muscular'),
       (N'Secreción nasal'),
       ('Sensibilidad a la luz'),
       ('Sepsis'),
       ('Sudores nocturnos'),
       ('Tos intensa y persistente'),
       ('Tos persistente'),
       ('Tos');
GO
PRINT (N'Relacionando enfermedades con sus síntomas');
GO
INSERT INTO enfermedades_sintomas (enfermedad, sintoma)
VALUES
-- Bacteriemia
(1, 18),  -- Fiebre
(1, 16),  -- Escalofríos
(1, 35),  -- Sepsis

-- COVID-19
(2, 18),  -- Fiebre
(2, 40),  -- Tos persistente
(2, 7),   -- Dificultad para respirar
(2, 16),  -- Fatiga
(2, 29),  -- Pérdida del gusto o olfato
(2, 13),  -- Dolor muscular
(2, 9),   -- Dolor de cabeza

-- Difteria
(3, 18),  -- Fiebre
(3, 10),  -- Dolor de garganta
(3, 19),  -- Formación de una membrana gruesa en la garganta
(3, 7),   -- Dificultad para tragar
(3, 20),  -- Ganglios inflamados

-- Enfermedad meningocócica
(4, 18),  -- Fiebre
(4, 9),   -- Dolor de cabeza
(4, 31),  -- Rigidez en el cuello
(4, 3),   -- Confusión
(4, 32),  -- Sensibilidad a la luz
(4, 4),   -- Convulsiones

-- Enfermedades neumocócicas
(5, 18),  -- Fiebre
(5, 9),   -- Dolor de cabeza
(5, 31),  -- Rigidez en el cuello
(5, 3),   -- Confusión
(5, 32),  -- Sensibilidad a la luz
(5, 35),  -- Sepsis

-- Fiebre Amarilla
(6, 18),  -- Fiebre
(6, 21),  -- Ictericia
(6, 13),  -- Dolor muscular
(6, 22),  -- Náuseas
(6, 23),  -- Vómito
(6, 16),  -- Fatiga

-- Hepatitis A
(7, 18),  -- Fiebre
(7, 16),  -- Fatiga
(7, 7),   -- Dolor abdominal
(7, 24),  -- Orina oscura
(7, 21),  -- Ictericia
(7, 22),  -- Náuseas
(7, 23),  -- Vómito
(7, 31),  -- Poco apetito

-- Hepatitis B
(8, 18),  -- Fiebre
(8, 16),  -- Fatiga
(8, 7),   -- Dolor abdominal
(8, 24),  -- Orina oscura
(8, 21),  -- Ictericia
(8, 22),  -- Náuseas
(8, 23),  -- Vómito
(8, 31),  -- Poco apetito

-- Hib (Haemophilus influenzae tipo b)
(9, 18),  -- Fiebre
(9, 25),  -- Meningitis
(9, 26),  -- Neumonía
(9, 27),  -- Infección de la sangre

-- Influenza (Gripe)
(10, 18), -- Fiebre
(10, 16), -- Fatiga
(10, 13), -- Dolor muscular
(10, 9),  -- Dolor de cabeza
(10, 41), -- Tos

-- Meningitis
(11, 18), -- Fiebre
(11, 9),  -- Dolor de cabeza
(11, 31), -- Rigidez en el cuello
(11, 3),  -- Confusión
(11, 32), -- Sensibilidad a la luz
(11, 4),  -- Convulsiones

-- Neumonía
(12, 18), -- Fiebre
(12, 7),  -- Dificultad para respirar
(12, 41), -- Tos
(12, 11), -- Dolor en el pecho

-- Paperas
(13, 18), -- Fiebre
(13, 11), -- Dolor e hinchazón en las glándulas salivales
(13, 9),  -- Dolor de cabeza
(13, 16), -- Fatiga

-- Poliomelitis (Polio)
(14, 18), -- Fiebre
(14, 16), -- Fatiga
(14, 9),  -- Dolor de cabeza
(14, 31), -- Rigidez en el cuello
(14, 24), -- Parálisis

-- Rabia
(15, 18), -- Fiebre
(15, 9),  -- Dolor de cabeza
(15, 15), -- Espasmos
(15, 3),  -- Confusión
(15, 24), -- Parálisis

-- Rotavirus
(16, 6),  -- Diarrea grave
(16, 23), -- Vómito
(16, 18), -- Fiebre
(16, 7),  -- Dolor abdominal

-- Rubéola
(17, 18), -- Fiebre
(17, 12), -- Erupción cutánea característica
(17, 20), -- Ganglios inflamados

-- Sarampión
(18, 18), -- Fiebre
(18, 12), -- Erupción cutánea característica
(18, 41), -- Tos
(18, 34), -- Secreción nasal
(18, 3),  -- Conjuntivitis

-- Tétanos
(19, 33), -- Rigidez muscular
(19, 15), -- Espasmos
(19, 9),  -- Dolor de cabeza
(19, 7),  -- Dificultad para tragar

-- Tos ferina
(20, 39), -- Tos intensa y persistente
(20, 1),  -- Ataques de tos
(20, 7),  -- Dificultad para respirar

-- Tuberculosis
(21, 18), -- Fiebre
(21, 16), -- Fatiga
(21, 40), -- Tos persistente
(21, 7),  -- Dificultad para respirar
(21, 28), -- Pérdida de peso
(21, 6),  -- Escalofríos
(21, 36), -- Sudores nocturnos

-- Varicela
(22, 18), -- Fiebre
(22, 12), -- Erupción cutánea característica
(22, 31), -- Picazón
(22, 16), -- Fatiga

-- Virus del papiloma humano (VPH)
(23, 37), -- Verrugas genitales
(23, 1),  -- Cáncer de cuello uterino
(23, 24); -- Otros tipos de cáncer
GO
PRINT ('Insertando roles');
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
PRINT ('Insertando permisos');
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
/*
Aclaraciones:
- Diferencia entre ADMINISTRATIVO_WRITE y MED_WRITE, radica en poder gestionar las categorías padre llamase Vacunas, sus enfermedades y síntomas.
  MED únicamente puede gestionar las dosis de las categorías ya creadas, dando la posibilidad de usar 'Por registrar' para el rol correspondiente corrija.
- Diferencia entre ADMINISTRATIVO_WRITE y USER_MANAGER_WRITE, ambos roles permiten modificar usuarios, pero administrativo puede deshabilitar un usuario y sus roles.
  Ninguno puede crear usuarios, ya que, es una facultad de las aplicaciones o sistemas que implementan automáticamente el hash de contraseñas y otorga roles ya definidos.
- Diferencia entre AUTORIDAD_WRITE y DEV_DB_ADMIN es directamente en generar datos sin restricciones, los dev pueden modificar la estructura más no los datos.
*/
PRINT ('Relacionando roles con sus permisos');
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
PRINT ('Insertando vacunas');
GO
-- TODO cambiar el cálculo a dia exacto
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
PRINT (N'Relacionando vacunas con enfermedades');
GO
-- Adacel
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Adacel', NULL, 'Difteria', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Adacel', NULL, N'Tétanos', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Adacel', NULL, 'Tos ferina', NULL;
GO
-- BCG
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'BCG', NULL, 'Tuberculosis', NULL;
GO
-- COVID-19
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'COVID-19', NULL, 'COVID-19', NULL;
GO
-- Fiebre Amarilla
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Fiebre Amarilla', NULL, 'Fiebre Amarilla', NULL;
GO
-- Hepatitis A
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Hep A (Euvax) (adultos)', NULL, 'Hepatitis A', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Hep A (Euvax) (infantil)', NULL, 'Hepatitis A', NULL;
GO
-- Hepatitis B
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Hep B (adultos)', NULL, 'Hepatitis B', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Hep B (infantil)', NULL, 'Hepatitis B', NULL;
GO
-- Hexaxim
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Hexaxim', NULL, 'Difteria', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Hexaxim', NULL, N'Tétanos', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Hexaxim', NULL, 'Tos ferina', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Hexaxim', NULL, 'Hepatitis B', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Hexaxim', NULL, 'Poliomelitis (Polio)', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Hexaxim', NULL, 'Hib (Haemophilus influenzae tipo b)', NULL;
GO
-- Influenza
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Influenza (FluQuadri)', NULL, 'Influenza (Gripe)', NULL;
GO
-- Meningococo
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Meningococo', NULL, N'Enfermedad meningocócica', NULL;
GO
-- MMR
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'MMR', NULL, N'Sarampión', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'MMR', NULL, 'Paperas', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'MMR', NULL, N'Rubéola', NULL;
GO
-- MR (antisarampión, antirrubéola)
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, N'MR (antisarampión, antirrubéola)', NULL, N'Sarampión', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, N'MR (antisarampión, antirrubéola)', NULL, N'Rubéola', NULL;
GO
-- Neumoco conjugado (Prevenar 13 valente)
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Neumoco conjugado (Prevenar 13 valente)', NULL, N'Neumonía', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Neumoco conjugado (Prevenar 13 valente)', NULL, 'Meningitis', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Neumoco conjugado (Prevenar 13 valente)', NULL, 'Bacteriemia', NULL;
GO
-- VPH (Gardasil)
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Papiloma Humano (Gardasil)', NULL, 'Virus del papiloma humano (VPH)',
     NULL;
GO
-- Pneumo23
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Pneumo23', NULL, N'Enfermedades neumocócicas', NULL;
GO
-- Pneumovax
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Pneumovax', NULL, N'Enfermedades neumocócicas', NULL;
GO
-- Priorix
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Priorix', NULL, N'Sarampión', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Priorix', NULL, N'Rubéola', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Priorix', NULL, 'Paperas', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Rotarix', NULL, 'Rotavirus', NULL;
GO
-- TD
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'TD', NULL, N'Tétanos', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'TD', NULL, 'Difteria', NULL;
GO
-- Tetravalente
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Tetravalente', NULL, 'Difteria', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Tetravalente', NULL, N'Tétanos', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Tetravalente', NULL, 'Tos ferina', NULL;
GO
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Tetravalente', NULL, 'Poliomelitis (Polio)', NULL;
GO
-- Varivax
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Varivax', NULL, 'Varicela', NULL;
GO
-- Verorab
EXEC sp_vacunas_insert_vacuna_enfermedad NULL, 'Verorab', NULL, 'Rabia', NULL;
GO

PRINT (N'Insertando fabricantes y las vacunas que producen');
GO
-- La licencia_fabricante es ficticia*
EXEC sp_vacunas_insert_fabricante '08-001 LA/DNFD', 'Sanofi Pasteur', 'info@sanofipasteur.com', '+18008222463',
     'ACTIVO', 'Sanofi Pasteur Inc., 1 Discovery Drive, Swiftwater, PA 18370, USA', 'Extranjero', NULL, NULL, NULL,
     NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Sanofi Pasteur', NULL, 'Adacel', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Sanofi Pasteur', NULL, 'BCG', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Sanofi Pasteur', NULL, 'Fiebre Amarilla', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Sanofi Pasteur', NULL, 'Hexaxim', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Sanofi Pasteur', NULL, 'Influenza (FluQuadri)', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Sanofi Pasteur', NULL, 'TD', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Sanofi Pasteur', NULL, 'Verorab', NULL;
GO
EXEC sp_vacunas_insert_fabricante '08-002 LA/DNFD', 'Pfizer', 'support@pfizer.com', '+12127332323', 'ACTIVO',
     'Pfizer Inc., 235 East 42nd Street, New York, NY 10017, USA', 'Extranjero', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Pfizer', NULL, 'COVID-19', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Pfizer', NULL, 'Neumoco conjugado (Prevenar 13 valente)', NULL;
GO
EXEC sp_vacunas_insert_fabricante '08-003 LA/DNFD', 'GlaxoSmithKline', 'info@gsk.com', '+442080475000', 'ACTIVO',
     'GSK plc, 980 Great West Road, Brentford, Middlesex, TW8 9GS, UK', 'Extranjero', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'GlaxoSmithKline', NULL, 'Hep A (Euvax) (adultos)', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'GlaxoSmithKline', NULL, 'Hep A (Euvax) (infantil)', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'GlaxoSmithKline', NULL, 'Meningococo', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'GlaxoSmithKline', NULL, 'Priorix', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'GlaxoSmithKline', NULL, 'Rotarix', NULL;
GO
EXEC sp_vacunas_insert_fabricante '08-004 LA/DNFD', 'Merck', 'contact@merck.com', '+19087404000', 'ACTIVO',
     'Merck & Co., Inc., 2000 Galloping Hill Road, Kenilworth, NJ 07033, USA', 'Extranjero', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Merck', NULL, 'Hep B (adultos)', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Merck', NULL, 'Hep B (infantil)', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Merck', NULL, 'MMR', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Merck', NULL, 'Papiloma Humano (Gardasil)', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Merck', NULL, 'Pneumo23', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Merck', NULL, 'Pneumovax', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Merck', NULL, 'Tetravalente', NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Merck', NULL, 'Varivax', NULL;
GO
EXEC sp_vacunas_insert_fabricante '08-005 LA/DNFD', 'Serum Institute', 'contact@seruminstitute.com', '+912026993900',
     'ACTIVO', '212/2, Hadapsar, Off Soli Poonawalla Road, Pune 411028, Maharashtra, India', 'Extranjero', NULL, NULL,
     NULL, NULL;
EXEC sp_vacunas_insert_fabricante_vacuna NULL, 'Serum Institute', NULL, N'MR (antisarampión, antirrubéola)', NULL;
GO

PRINT (N'Insertando almacén');
GO
-- ficticios
EXEC sp_vacunas_insert_almacen N'Almacén Vacúnate Panamá', 'MINSA', NULL, '+5072759689', 'ACTIVO', NULL, NULL,
     'Carlos Gonzalez', '2-1-1', NULL, NULL;
GO

PRINT (N'Insertando almacén inventario');
GO
-- Insertar el inventario en el almacén usando el procedimiento almacenado
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Adacel', 160, '2025-12-15', 'LoteA1',
     NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'BCG', 215, '2025-12-16', 'LoteA2',
     NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'COVID-19', 140, '2025-12-17',
     'LoteA3', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Fiebre Amarilla', 325, '2025-12-18',
     'LoteA4', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Hep A (Euvax) (adultos)', 280,
     '2025-12-19', 'LoteA5', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Hep A (Euvax) (infantil)', 215,
     '2025-12-20', 'LoteA6', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Hep B (adultos)', 260, '2025-12-21',
     'LoteA7', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Hep B (infantil)', 235, '2025-12-22',
     'LoteA8', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Hexaxim', 190, '2025-12-23',
     'LoteA9', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Influenza (FluQuadri)', 185,
     '2025-12-24', 'LoteA10', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Meningococo', 170, '2025-12-25',
     'LoteA11', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'MMR', 235, '2025-12-26', 'LoteA12',
     NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, N'MR (antisarampión, antirrubéola)',
     230, '2025-12-27', 'LoteA13', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL,
     'Neumoco conjugado (Prevenar 13 valente)', 165, '2025-12-28', 'LoteA14', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Papiloma Humano (Gardasil)', 160,
     '2025-12-29', 'LoteA15', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Pneumo23', 155, '2025-12-30',
     'LoteA16', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Pneumovax', 150, '2025-12-31',
     'LoteA17', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Priorix', 145, '2025-12-01',
     'LoteA18', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Rotarix', 140, '2025-12-02',
     'LoteA19', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'TD', 135, '2025-12-03', 'LoteA20',
     NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Tetravalente', 130, '2025-12-04',
     'LoteA21', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Varivax', 125, '2025-12-05',
     'LoteA22', NULL;
GO
EXEC sp_vacunas_insert_almacen_inventario NULL, N'Almacén Vacúnate Panamá', NULL, 'Verorab', 125, '2025-12-06',
     'LoteA23', NULL;
GO

PRINT (N'Insertando sedes y distribuyendo inventario de Almacén a sedes');
GO
-- Sedes algunos datos no son veraces*
EXEC sp_vacunas_insert_sede N'Hospital San Miguel Arcángel', 'MINSA', NULL, '+5075236906', 'ACTIVO',
     N'HISMA, Vía Ricardo J. Alfaro', 'San Miguelito', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, N'Hospital San Miguel Arcángel', NULL,
     'Adacel', 10, 'LoteA1', NULL, NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, N'Hospital San Miguel Arcángel', NULL,
     'Rotarix', 100, 'LoteA19', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede 'MINSA CAPSI FINCA 30 CHANGINOLA', 'MINSA', NULL, '+5077588096', 'ACTIVO', 'Finca 32',
     'Changuinola', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, 'MINSA CAPSI FINCA 30 CHANGINOLA', NULL,
     'BCG', 15, 'LoteA2', NULL, NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, 'MINSA CAPSI FINCA 30 CHANGINOLA', NULL,
     'TD', 105, 'LoteA20', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede 'Hospital Aquilino Tejeira', 'CSS', NULL, '+5079979386', 'ACTIVO', 'Calle Manuel H Robles',
     N'Penonomé', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, 'Hospital Aquilino Tejeira', NULL,
     'COVID-19', 20, 'LoteA3', NULL, NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, 'Hospital Aquilino Tejeira', NULL,
     'Tetravalente', 110, 'LoteA21', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede N'CENTRO DE SALUD METETÍ', 'MINSA', NULL, '+5072996151', 'ACTIVO', 'La Palma', 'Pinogana',
     NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, N'CENTRO DE SALUD METETÍ', NULL,
     'Fiebre Amarilla', 25, 'LoteA4', NULL, NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, N'CENTRO DE SALUD METETÍ', NULL, 'Varivax',
     115, 'LoteA22', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede 'POLICENTRO DE SALUD de Chepo', 'MINSA', NULL, '+5072967220', 'ACTIVO',
     'Via Panamericana Las Margaritas', 'Chepo', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, 'POLICENTRO DE SALUD de Chepo', NULL,
     'Hep A (Euvax) (adultos)', 30, 'LoteA5', NULL, NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, 'POLICENTRO DE SALUD de Chepo', NULL,
     'Verorab', 120, 'LoteA23', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede N'Clínica Hospital San Fernando', 'Privada', NULL, '+5073056300', 'ACTIVO',
     N'Via España, Hospital San Fernando', N'Panamá', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, N'Clínica Hospital San Fernando', NULL,
     'Hep A (Euvax) (infantil)', 35, 'LoteA6', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede 'Complejo Hospitalario Doctor Arnulfo Arias Madrid', 'CSS', NULL, '+5075036032', 'ACTIVO',
     N'Avenida José de Fábrega, Complejo Hospitalario', N'Panamá', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL,
     'Complejo Hospitalario Doctor Arnulfo Arias Madrid', NULL, 'Hep B (adultos)', 40, 'LoteA7', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede N'Hospital Santo Tomás', 'MINSA', NULL, '+5075075830', 'ACTIVO',
     'Avenida Balboa y Calle 37 Este', N'Panamá', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, N'Hospital Santo Tomás', NULL,
     'Hep B (infantil)', 45, 'LoteA8', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede N'Hospital Regional de Changuinola Raul Dávila Mena', 'MINSA', NULL, '+5077588295',
     'ACTIVO', 'Changuinola, Bocas del Toro', 'Changuinola', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL,
     N'Hospital Regional de Changuinola Raul Dávila Mena', NULL, 'Hexaxim', 50, 'LoteA9', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede N'Hospital Dr. Rafael Hernández', 'MINSA', NULL, '+5077748400', 'ACTIVO',
     N'David, Chiriquí', 'David', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, N'Hospital Dr. Rafael Hernández', NULL,
     'Influenza (FluQuadri)', 55, 'LoteA10', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede N'Pacífica Salud Hospital Punta Pacífica', 'Privada', NULL, '+5072048000', 'ACTIVO',
     N'Punta Pacífica, Ciudad de Panamá', N'Panamá', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, N'Pacífica Salud Hospital Punta Pacífica',
     NULL, 'Meningococo', 60, 'LoteA11', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede 'Hospital Nacional', 'Privada', NULL, '+5073072102', 'ACTIVO',
     N'Av. Cuba, Ciudad de Panamá', N'Panamá', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, 'Hospital Nacional', NULL, 'MMR', 65,
     'LoteA12', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede 'Centro de salud Pacora', 'MINSA', NULL, '+5072960005', 'ACTIVO',
     N'Pacora, Ciudad de Panamá', N'Panamá', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, 'Centro de salud Pacora', NULL,
     N'MR (antisarampión, antirrubéola)', 70, 'LoteA13', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede N'Hospital Dr. Nicolás A. Solano', 'MINSA', NULL, '+5072548926', 'ACTIVO',
     N'La Chorrera, Panamá Oeste', 'La Chorrera', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, N'Hospital Dr. Nicolás A. Solano', NULL,
     'Neumoco conjugado (Prevenar 13 valente)', 75, 'LoteA14', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede 'Complejo Hospitalario Dr. Manuel Amador Guerrero', 'CSS', NULL, '+5074752207', 'ACTIVO',
     N'Colón, Colón', N'Colón', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL,
     'Complejo Hospitalario Dr. Manuel Amador Guerrero', NULL, 'Papiloma Humano (Gardasil)', 80, 'LoteA15', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede N'Policlínica Lic. Manuel María Valdés', 'CSS', NULL, '+5075031500', 'ACTIVO',
     N'San Miguelito, Ciudad de Panamá', N'Panamá', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, N'Policlínica Lic. Manuel María Valdés',
     NULL, 'Pneumo23', 85, 'LoteA16', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede 'CSS Complejo Metropolitano', 'CSS', NULL, '+5075064000', 'ACTIVO',
     N'Vía España, Ciudad de Panamá', N'Panamá', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL, 'CSS Complejo Metropolitano', NULL,
     'Pneumovax', 90, 'LoteA17', NULL, NULL;
GO
EXEC sp_vacunas_insert_sede N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', 'CSS', NULL, '+5075137008',
     'ACTIVO', N'Vía España, Ciudad de Panamá', N'Panamá', NULL;
EXEC sp_vacunas_distribuir_vacunas NULL, N'Almacén Vacúnate Panamá', NULL,
     N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, 'Priorix', 95, 'LoteA18', NULL, NULL;
GO

PRINT ('Insertando pacientes e insertando sus dosis');
-- Pacientes ficticios
EXEC sp_vacunas_gestionar_paciente '1-145-987', NULL, NULL, 'Luis', N'Gómez', NULL, '1984-01-12', 'M', '+50760086666',
     'luis.mez@example.com', 'ACTIVO', 'Calle 7, Edificio 6', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, '1-145-987', NULL, NULL, '2018-03-15 12:15', '1', NULL, 'Influenza (FluQuadri)',
     NULL, N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '1-54-9635', NULL, NULL, 'Luis', 'Mendoza', NULL, '2006-05-05', 'M', '+50763257865',
     NULL, 'ACTIVO', 'Finca 30, casa 45', 'Changuinola', NULL;
EXEC sp_vacunas_insert_dosis NULL, '1-54-9635', NULL, NULL, '2024-08-10 10:00', '1', NULL, 'Hep B (adultos)', NULL,
     'Hospital Aquilino Tejeira', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '1PI-678-12', NULL, NULL, 'Beatriz', 'Castillo', NULL, '1996-06-19', 'F',
     '+50760028888', 'beatriz.castillo@example.com', 'ACTIVO', 'Calle 3, Local 10', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, '1PI-678-12', NULL, NULL, '2022-06-05 09:00', '1', NULL, 'Hep B (adultos)', NULL,
     N'Hospital Regional de Changuinola Raul Dávila Mena', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '2-1234-654', NULL, NULL, 'Daniel', 'Cruz', 'Alvarado', '1993-10-20', 'M',
     '+50761070000', 'daniel.cruz@example.com', 'ACTIVO', 'Avenida 2, Edificio 4', 'David', NULL;
EXEC sp_vacunas_insert_dosis NULL, '2-1234-654', NULL, NULL, '2018-02-10 09:45', '1', NULL, 'COVID-19', NULL,
     N'Hospital Regional de Changuinola Raul Dávila Mena', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '2-4558-5479', NULL, NULL, N'José', 'Perez', NULL, '1959-12-13', 'M', '+50762654789',
     NULL, 'ACTIVO', N'Vía Interamericana, casa L78', N'Natá', NULL;
EXEC sp_vacunas_insert_dosis NULL, '2-4558-5479', NULL, NULL, '2020-04-10 10:00', '1', NULL, 'Tetravalente', NULL,
     'Centro de salud Pacora', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '2-4567-876543', NULL, NULL, 'Gabriela', N'Vásquez', N'Rodríguez', '1991-07-09', 'F',
     '+50760023333', 'gabriela.vasquez@example.com', 'ACTIVO', 'Calle 7, Apartamento 4', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, '2-4567-876543', NULL, NULL, '2024-07-22 11:15', '1', NULL,
     'Neumoco conjugado (Prevenar 13 valente)', NULL, N'Hospital Dr. Rafael Hernández', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '2PI-23-789', NULL, NULL, 'Alejandro', N'Jiménez', 'Salazar', '1992-08-19', 'M',
     '+50760047777', 'alejandro.jimenez@example.com', 'ACTIVO', N'Edificio El Águila, Piso 1', 'Bocas del Toro', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente '2PI-3421-456', NULL, NULL, 'Ricardo', 'Mora', NULL, '1980-12-30', 'M',
     '+50760006666', NULL, 'ACTIVO', 'Calle 14, Casa 89', 'Bocas del Toro', NULL;
EXEC sp_vacunas_insert_dosis NULL, '2PI-3421-456', NULL, NULL, '2022-05-20 11:15', '1', NULL, 'Pneumovax', NULL,
     N'Hospital Santo Tomás', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '3-12-543', NULL, NULL, 'Valeria', 'Castillo', N'García', '1997-11-24', 'F',
     '+50760015555', 'valeria.castillo@example.com', 'ACTIVO', 'Calle 13, Edificio A', 'Bocas del Toro', NULL;
EXEC sp_vacunas_insert_dosis NULL, '3-12-543', NULL, NULL, '2018-01-05 13:00', '1', NULL, 'Varivax', NULL,
     N'Policlínica Lic. Manuel María Valdés', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '3-567-9876', NULL, NULL, 'Marta', 'Alvarez', N'Rodríguez', '1992-07-19', 'F',
     '+50760060000', 'marta.alvarez@example.com', 'ACTIVO', 'Calle 5, Casa 10', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, '3-567-9876', NULL, NULL, '2024-06-30 13:00', '1', NULL, 'Hexaxim', NULL,
     N'Policlínica Lic. Manuel María Valdés', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '3-721-284', NULL, NULL, 'Sandra', N'Pérez', 'Castillo', '1995-10-10', 'F',
     '+50760587777', 'sandra.perez@example.com', 'ACTIVO', 'Calle 5, Casa 12', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, '3-721-284', NULL, NULL, '2022-04-10 10:30', '1', NULL, 'MMR', NULL,
     'Hospital Aquilino Tejeira', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '3PI-1-654', NULL, NULL, 'Elena', N'González', NULL, '1993-09-29', 'F',
     '+50760036666', 'elena.nzalez@example.com', 'ACTIVO', 'Calle 11, Edificio C', 'La Chorrera', NULL;
EXEC sp_vacunas_insert_dosis NULL, '3PI-1-654', NULL, NULL, '2020-03-01 09:30', '1', NULL, 'Rotarix', NULL,
     'Hospital Aquilino Tejeira', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '4-123-5678', NULL, NULL, 'Fernando', N'Pérez', NULL, '1990-05-14', 'M',
     '+50767080000', 'fernando.perez@example.com', 'ACTIVO', 'Edificio La Roca, Apartamento 7', 'David', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente '4-123-56789', NULL, NULL, 'Paola', 'Ramos', NULL, '1987-03-28', 'F', '+50760030000',
     'paola.ramos@example.com', 'ACTIVO', 'Calle 6, Edificio 3', N'Panamá', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente '4-4321-123', NULL, NULL, 'Elena', 'Cruz', N'Gómez', '1991-01-15', 'F',
     '+50760009999', 'elena.cruz@example.com', 'ACTIVO', 'Calle 9, Casa 43', N'Penonomé', NULL;
EXEC sp_vacunas_insert_dosis NULL, '4-4321-123', NULL, NULL, '2024-05-25 14:30', '1', NULL,
     'Papiloma Humano (Gardasil)', NULL, N'Hospital Santo Tomás', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '4-5678-12345', NULL, NULL, 'Fernanda', 'Torres', NULL, '1998-03-13', 'F',
     '+50760046666', 'fernanda.torres@example.com', 'ACTIVO', 'Calle 7, Casa 5', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, '4-5678-12345', NULL, NULL, '2020-02-15 11:45', '1', NULL, 'Pneumo23', NULL,
     N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '4-987-123456', NULL, NULL, 'Felipe', N'Hernández', NULL, '1987-03-17', 'M',
     '+50760020000', 'felipe.hernandez@example.com', 'ACTIVO', 'Calle 5, Local 6', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, '4-987-123456', NULL, NULL, '2022-03-15 14:00', '1', NULL,
     'Neumoco conjugado (Prevenar 13 valente)', NULL, N'Hospital Dr. Nicolás A. Solano', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '4AV-8-12', NULL, NULL, 'Victoria', N'Gómez', NULL, '1992-03-14', 'F',
     '+50760054444', 'victoria.mez@example.com', 'ACTIVO', 'Edificio La Vista, Piso 5', 'Bocas del Toro', NULL;
EXEC sp_vacunas_insert_dosis NULL, '4AV-8-12', NULL, NULL, '2017-12-15 10:30', '1', NULL, 'Hep B (adultos)', NULL,
     'Hospital Aquilino Tejeira', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '4PI-12-345', NULL, NULL, 'Eduardo', 'Salazar', NULL, '1984-04-22', 'M',
     '+50760061111', 'eduardo.salazar@example.com', 'ACTIVO', 'Avenida 6, Edificio 3', N'Panamá', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente '5-554-321', NULL, NULL, 'Martha', 'Cornejo', NULL, '1979-08-24', 'F',
     '+50767841296', NULL, 'ACTIVO', N'Vía Interamericana, Metetí, casa 87F', 'Pinogana', NULL;
EXEC sp_vacunas_insert_dosis NULL, '5-554-321', NULL, NULL, '2017-11-10 11:45', '1', NULL, 'Priorix', NULL,
     N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '5-567-65432', NULL, NULL, 'Natalia', 'Moreno', NULL, '1992-07-21', 'F',
     '+50760032222', 'natalia.moreno@example.com', 'ACTIVO', 'Edificio La Vista, Piso 6', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, '5-567-65432', NULL, NULL, '2020-01-10 10:30', '1', NULL, 'Hep A (Euvax) (adultos)',
     NULL, N'Policlínica Lic. Manuel María Valdés', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '5-678-98765', NULL, NULL, 'Ricardo', 'Salazar', N'Pérez', '1983-09-13', 'M',
     '+50760053333', 'ricardo.salazar@example.com', 'ACTIVO', 'Calle 5, Casa 8', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, '5-678-98765', NULL, NULL, '2022-02-05 12:15', '1', NULL, 'Hep A (Euvax) (infantil)',
     NULL, 'Centro de salud Pacora', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '5AV-34-654', NULL, NULL, N'Tomás', 'Alvarado', 'Castillo', '1988-02-18', 'M',
     '+50760041111', 'tomas.alvarado@example.com', 'ACTIVO', 'Calle 2, Edificio 1', N'Panamá', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente '5AV-9-1234', NULL, NULL, 'Javier', N'Fernández', NULL, '1988-02-21', 'M',
     '+50760004444', 'javier.fernandez@example.com', 'ACTIVO', 'Avenida 1A, Local 9', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, '5AV-9-1234', NULL, NULL, '2024-04-15 09:00', '1', NULL, 'Varivax', NULL,
     N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '6-1234-65432', NULL, NULL, 'Rosa', N'González', NULL, '1981-10-29', 'F',
     '+50760048888', 'rosa.nzalez@example.com', 'ACTIVO', 'Calle 8, Edificio 6', 'La Chorrera', NULL;
EXEC sp_vacunas_insert_dosis NULL, '6-1234-65432', NULL, NULL, '2019-12-25 14:00', '1', NULL, 'Hep B (infantil)', NULL,
     N'Hospital Santo Tomás', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '6-1234-678', NULL, NULL, 'Laura', N'Gómez', 'Morales', '1995-10-30', 'F',
     '+50760062222', 'laura.mez@example.com', 'ACTIVO', 'Calle 12, Edificio 7', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, '6-1234-678', NULL, NULL, '2022-01-25 10:00', '1', NULL, 'Varivax', NULL,
     'Complejo Hospitalario Dr. Manuel Amador Guerrero', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '6-2345-123', NULL, NULL, N'Andrés', 'Torres', 'Castillo', '1986-04-12', 'M',
     '+50760035555', 'andres.torres@example.com', 'ACTIVO', 'Edificio La Colina, Apartamento 3', 'Bocas del Toro', NULL;
EXEC sp_vacunas_insert_dosis NULL, '6-2345-123', NULL, NULL, '2024-03-20 10:30', '1', NULL, 'Pneumovax', NULL,
     'Centro de salud Pacora', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '6-2-987654', NULL, NULL, 'Sofia', N'Méndez', N'Hernández', '1993-07-17', 'F',
     '+50760011111', 'sofia.mendez@example.com', 'ACTIVO', 'Avenida 2B, Piso 2', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, '6-2-987654', NULL, NULL, '2017-10-01 09:30', '1', NULL, 'Pneumovax', NULL,
     N'Hospital San Miguel Arcángel', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '6-789-654', NULL, NULL, 'Laura', 'Salazar', N'García', '1993-07-12', 'F',
     '+50760079999', 'laura.salazar@example.com', 'ACTIVO', 'Calle 8, Edificio 6', N'Colón', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente '6AV-99-1', NULL, NULL, 'Luis', 'Salazar', NULL, '1985-12-14', 'M', '+50760022222',
     'luis.salazar@example.com', 'ACTIVO', 'Avenida 1, Casa 9', 'David', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente '7-114-10', NULL, NULL, 'Antonio', N'Sánchez', NULL, '1989-07-14', 'M',
     '+50760088888', 'antonio.sanchez@example.com', 'ACTIVO', 'Calle 9, Edificio 8', 'David', NULL;
EXEC sp_vacunas_insert_dosis NULL, '7-114-10', NULL, NULL, '2017-09-20 14:00', '1', NULL,
     'Neumoco conjugado (Prevenar 13 valente)', NULL, 'Complejo Hospitalario Dr. Manuel Amador Guerrero', NULL, NULL,
     NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '7-234-1234', NULL, NULL, 'Die', N'Jiménez', 'Morales', '1983-12-15', 'M',
     '+50760029999', 'die.jimenez@example.com', 'ACTIVO', 'Avenida 9, Edificio B', 'David', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente '7-345-678', NULL, NULL, 'Gabriel', N'Ramírez', 'Ramos', '1996-04-21', 'M',
     '+50760049999', 'gabriel.ramirez@example.com', 'ACTIVO', 'Calle 11, Casa 15', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, '7-345-678', NULL, NULL, '2021-12-15 13:30', '1', NULL, 'Hep B (infantil)', NULL,
     N'Hospital San Miguel Arcángel', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '7-4567-123', NULL, NULL, 'Camila', 'Ruiz', NULL, '1996-03-14', 'F', '+50760040000',
     'camila.ruiz@example.com', 'ACTIVO', 'Avenida 3, Casa 12', 'David', NULL;
EXEC sp_vacunas_insert_dosis NULL, '7-4567-123', NULL, NULL, '2019-11-15 16:30', '1', NULL, 'COVID-19', NULL,
     'Hospital Nacional', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '7-456-987', NULL, NULL, 'Francisco', N'Sánchez', NULL, '1988-11-02', 'M',
     '+50763069999', 'francisco.sanchez@example.com', 'ACTIVO', 'Calle 7, Casa 9', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, '7-456-987', NULL, NULL, '2024-02-12 15:00', '1', NULL, 'Fiebre Amarilla', NULL,
     'Hospital Nacional', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '7AV-678-123', NULL, NULL, 'Jorge', N'Fernández', NULL, '1986-09-30', 'M',
     '+50760016666', NULL, 'ACTIVO', 'Calle 8, Apartamento 2', 'La Chorrera', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente '8-1006-14', NULL, NULL, 'Jorge', 'Ruiz', NULL, '1999-05-31', 'M', '+50760684595',
     NULL, 'ACTIVO', 'Samaria, sector 4, casa E1', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, '8-1006-14', NULL, NULL, '2021-11-05 09:45', '1', NULL, 'TD', NULL,
     N'Policlínica Lic. Manuel María Valdés', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '8-123-1234', NULL, NULL, N'Andrés', N'Martínez', N'García', '1986-12-11', 'M',
     '+50760055555', 'andres.martinez@example.com', 'ACTIVO', 'Calle 12, Local 2', 'La Chorrera', NULL;
EXEC sp_vacunas_insert_dosis NULL, '8-123-1234', NULL, NULL, '2017-08-15 12:30', '1', NULL, 'Rotarix', NULL,
     'Hospital Nacional', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '8-12-321', NULL, NULL, 'Isabel', N'García', NULL, '1994-07-09', 'F', '+50760042222',
     'isabel.garcia@example.com', 'ACTIVO', 'Edificio Los Robles, Apartamento 8', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, '8-12-321', NULL, NULL, '2024-01-30 16:45', '1', NULL, 'TD', NULL,
     N'Hospital San Miguel Arcángel', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '8-123-56789', NULL, NULL, 'Hu', 'Cruz', NULL, '1980-02-15', 'M', '+50760014444',
     NULL, 'ACTIVO', 'Avenida 3, Local 14', 'San Miguelito', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente '8-3456-123', NULL, NULL, N'Sofía', N'Rodríguez', NULL, '1995-12-20', 'F',
     '+50768083333', 'sofia.rodriguez@example.com', 'ACTIVO', 'Avenida 3, Edificio 9', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, '8-3456-123', NULL, NULL, '2019-10-05 09:00', '1', NULL, 'Hep B (adultos)', NULL,
     'Centro de salud Pacora', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '8-9754-1236', NULL, NULL, 'Suleimi', 'Rodriguez', NULL, '2001-02-17', 'F',
     '+50767859631', NULL, 'ACTIVO', 'Calle 51, casa 74', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, '8-9754-1236', NULL, NULL, '2017-12-01 15:00', '1', NULL, 'Tetravalente', NULL,
     'Complejo Hospitalario Dr. Manuel Amador Guerrero', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '9-1-123456', NULL, NULL, 'Sandra', 'Montes', NULL, '1984-08-19', 'F',
     '+50760038888', 'sandra.montes@example.com', 'ACTIVO', 'Edificio Los Jardines, Piso 2', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, '9-1-123456', NULL, NULL, '2023-12-25 09:30', '1', NULL, 'BCG', NULL,
     'Complejo Hospitalario Dr. Manuel Amador Guerrero', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '9-123-123', NULL, NULL, 'Ricardo', 'Castillo', NULL, '1987-11-15', 'M',
     '+50760059999', 'ricardo.castillo@example.com', 'ACTIVO', 'Edificio Los Robles, Piso 3', 'David', NULL;
EXEC sp_vacunas_insert_dosis NULL, '9-123-123', NULL, NULL, '2016-07-20 09:45', '1', NULL, 'Hep B (infantil)', NULL,
     N'Hospital San Miguel Arcángel', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '9PI-1-98765', NULL, NULL, 'Natalia', 'Morales', N'Vásquez', '1990-06-14', 'F',
     '+50760017777', 'natalia.morales@example.com', 'ACTIVO', 'Edificio Los Pinos, Piso 4', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, '9PI-1-98765', NULL, NULL, '2019-09-20 11:15', '1', NULL, 'Varivax', NULL,
     N'Hospital Dr. Nicolás A. Solano', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente '9PI-234-1', NULL, NULL, N'Tomás', N'González', N'Sánchez', '1991-08-15', 'M',
     '+50760072222', 'tomas.nzalez@example.com', 'ACTIVO', 'Calle 6, Local 8', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, '9PI-234-1', NULL, NULL, '2021-10-20 12:00', '1', NULL, 'Hep B (adultos)', NULL,
     N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, '9PI-234-1', NULL, NULL, '2017-07-10 11:15', '1', NULL, 'Fiebre Amarilla', NULL,
     'Centro de salud Pacora', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'E-12-123456', NULL, NULL, 'Luis', N'Pérez', NULL, '1978-03-19', 'M', NULL,
     'luis.perez@example.com', 'ACTIVO', 'Edificio Las Palmas, Piso 3', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'E-12-123456', NULL, NULL, '2023-09-20 16:30', '1', NULL, 'Influenza (FluQuadri)',
     NULL, N'Hospital Regional de Changuinola Raul Dávila Mena', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, 'E-12-123456', NULL, NULL, '2019-06-20 09:45', '1', NULL, 'TD', NULL,
     N'Hospital Regional de Changuinola Raul Dávila Mena', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'E-12-54321', NULL, NULL, 'Mauricio', 'Ruiz', N'Gómez', '1987-02-16', 'M',
     '+50760051111', 'mauricio.ruiz@example.com', 'ACTIVO', 'Edificio La Palma, Apartamento 6', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'E-12-54321', NULL, NULL, '2017-04-10 13:00', '1', NULL, 'Hexaxim', NULL,
     N'Hospital Dr. Nicolás A. Solano', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'E-23-321', NULL, NULL, 'Alejandra', 'Morales', NULL, '1984-10-18', 'F',
     '+50760024444', 'alejandra.morales@example.com', 'ACTIVO', 'Edificio El Sol, Oficina 3', 'Bocas del Toro', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'E-2345-123', NULL, NULL, 'Luis', N'Vásquez', NULL, '1988-02-15', 'M',
     '+50760063333', 'luis.vasquez@example.com', 'ACTIVO', 'Calle 3, Casa 7', 'David', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'E-345-1', NULL, NULL, 'Federico', N'Gómez', NULL, '1991-06-28', 'M', '+50760043333',
     'federico.mez@example.com', 'ACTIVO', 'Calle 10, Local 7', N'Colón', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'E-3456-78', NULL, NULL, 'Elena', N'Sánchez', N'Vásquez', '1994-09-19', 'F',
     '+50765075555', 'elena.sanchez@example.com', 'ACTIVO', 'Avenida 8, Edificio 5', 'David', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'E-3456-78', NULL, NULL, '2021-07-15 13:45', '1', NULL, 'Influenza (FluQuadri)',
     NULL, N'Hospital Dr. Rafael Hernández', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'E-7-7654', NULL, NULL, 'Julia', N'Sánchez', NULL, '1991-12-25', 'F', '+50760034444',
     'julia.sanchez@example.com', 'ACTIVO', 'Calle 8, Casa 14', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'E-7-7654', NULL, NULL, '2023-08-15 11:00', '1', NULL, 'Hep A (Euvax) (adultos)',
     NULL, 'Hospital Aquilino Tejeira', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'E-78-654321', NULL, NULL, 'Paola', N'García', 'Torres', '1996-06-11', 'F',
     '+50760007777', 'paola.garcia@example.com', 'ACTIVO', 'Edificio El Dorado, Oficina 10', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'E-78-654321', NULL, NULL, '2019-05-15 10:30', '1', NULL,
     'Neumoco conjugado (Prevenar 13 valente)', NULL, N'Hospital Santo Tomás', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'E-7-876', NULL, NULL, 'Javier', N'García', NULL, '1991-08-20', 'M', '+50760057777',
     'javier.garcia@example.com', 'ACTIVO', 'Avenida 7, Edificio 9', 'San Miguelito', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'N-1234-654', NULL, NULL, 'Camila', N'Vásquez', 'Ruiz', '1995-10-11', 'F',
     '+50760013333', 'camila.vasquez@example.com', 'ACTIVO', 'Edificio La Roca, Apartamento 10', 'David', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'N-12-987', NULL, NULL, 'Marta', N'Gómez', 'Alvarado', '1988-11-12', 'F',
     '+50766077777', 'marta.mez@example.com', 'ACTIVO', 'Edificio El Sol, Apartamento 4', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'N-12-987', NULL, NULL, '2023-02-10 08:30', '1', NULL, 'BCG', NULL,
     N'Pacífica Salud Hospital Punta Pacífica', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'N-234-567', NULL, NULL, 'Daniela', N'Hernández', NULL, '1995-01-18', 'F',
     '+50760052222', 'daniela.hernandez@example.com', 'ACTIVO', 'Calle 6, Edificio 4', 'David', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'N-456-789', NULL, NULL, N'María', N'Rodríguez', N'Hernández', '1992-11-05', 'F',
     '+50760003333', NULL, 'ACTIVO', 'Calle 12, Casa 56', 'David', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'N-543-567', NULL, NULL, 'Daniela', N'Gómez', N'Fernández', '1994-04-22', 'F',
     '+50760021111', 'daniela.mez@example.com', 'ACTIVO', 'Calle 4, Edificio 7', N'Colón', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'N-56-2345', NULL, NULL, 'Carla', N'Sánchez', NULL, '1994-06-28', 'F',
     '+50760056666', 'carla.sanchez@example.com', 'ACTIVO', 'Calle 9, Casa 4', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'N-56-2345', NULL, NULL, '2021-01-05 16:30', '1', NULL, 'Varivax', NULL,
     N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'N-56-987', NULL, NULL, 'Juan', N'García', 'Ruiz', '1994-08-15', 'M', '+50760031111',
     'juan.garcia@example.com', 'ACTIVO', 'Calle 4, Casa 8', 'San Miguelito', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'N-78-987', NULL, NULL, 'Santiago', N'Méndez', N'Vásquez', '1990-11-25', 'M',
     '+50760039999', 'santia.mendez@example.com', 'ACTIVO', 'Calle 9, Apartamento 5', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'N-78-987', NULL, NULL, '2018-11-10 09:00', '1', NULL, 'Pneumo23', NULL,
     N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'N-89-1', NULL, NULL, N'Sofía', N'González', NULL, '1986-12-15', 'F', '+50762066666',
     'sofia.nzalez@example.com', 'ACTIVO', 'Calle 11, Casa 6', 'La Chorrera', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'N-89-654', NULL, NULL, N'Joaquín', N'Ramírez', N'García', '1999-02-20', 'M',
     '+50760025555', 'joaquin.ramirez@example.com', 'ACTIVO', 'Calle 10, Edificio 8', 'La Chorrera', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'N-9-567', NULL, NULL, 'Carlos', 'Alvarez', N'Gómez', '1986-06-14', 'M',
     '+50760084444', 'carlos.alvarez@example.com', 'ACTIVO', 'Calle 12, Local 7', 'David', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'N-9-876', NULL, NULL, 'Miguel', N'Vásquez', NULL, '1989-05-21', 'M', '+50760045555',
     'miguel.vasquez@example.com', 'ACTIVO', 'Avenida 8, Edificio 3', N'Panamá', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'PE-12-1234', NULL, NULL, N'Verónica', N'Vásquez', N'Gómez', '1992-07-15', 'F',
     '+50761068888', 'veronica.vasquez@example.com', 'ACTIVO', 'Edificio La Vista, Piso 7', 'San Miguelito', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'PE-12-3456', NULL, NULL, N'Héctor', N'Ramírez', 'Ruiz', '1997-06-14', 'M',
     '+50760037777', 'hector.ramirez@example.com', 'ACTIVO', 'Calle 5, Casa 10', N'Panamá', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'PE-1234-567', NULL, NULL, 'Paola', N'Martínez', N'Vásquez', '1985-11-15', 'F',
     '+50760044444', 'paola.martinez@example.com', 'ACTIVO', 'Calle 4, Casa 6', 'David', NULL;
EXEC sp_vacunas_gestionar_paciente 'PE-1-4321', NULL, NULL, 'Natalia', N'Vásquez', NULL, '1988-02-24', 'F',
     '+50769081111', 'natalia.vasquez@example.com', 'ACTIVO', 'Calle 4, Edificio 1', N'Panamá', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'PE-234-567', NULL, NULL, 'Carlos', 'Mendez', NULL, '1990-04-12', 'M',
     '+50760001111', 'carlos.mendez@example.com', 'ACTIVO', 'Avenida 4B, Casa 12', N'Panamá', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'PE-345-234', NULL, NULL, 'Laura', N'Rodríguez', N'Pérez', '1998-08-05', 'F',
     '+50760019999', 'laura.rodriguez@example.com', 'ACTIVO', 'Avenida 6, Apartamento 8', N'Penonomé', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'PE-3456-321', NULL, NULL, 'Cristina', N'Pérez', NULL, '1988-11-12', 'F',
     '+50760026666', 'cristina.perez@example.com', 'ACTIVO', 'Calle 11, Casa 12', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'PE-3456-321', NULL, NULL, '2022-12-25 13:00', '1', NULL, 'COVID-19', NULL,
     'Hospital Nacional', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'PE-345-6789', NULL, NULL, 'Elena', N'Pérez', 'Morales', '1998-05-10', 'F',
     '+50760058888', 'elena.perez@example.com', 'ACTIVO', 'Calle 4, Apartamento 11', N'Colón', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'PE-5-3456', NULL, NULL, 'Alejandro', N'Rodríguez', NULL, '1989-04-09', 'M',
     '+50760012222', NULL, 'ACTIVO', 'Calle 6, Edificio 5', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'PE-5-3456', NULL, NULL, '2018-09-15 12:00', '1', NULL, 'Varivax', NULL,
     N'Hospital Regional de Changuinola Raul Dávila Mena', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'PE-8-123', NULL, NULL, 'Marcos', 'Salinas', N'Gómez', '1989-05-30', 'M',
     '+50760033333', 'marcos.salinas@example.com', 'ACTIVO', 'Avenida 7, Local 5', 'David', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'PE-8-567', NULL, NULL, 'Luis', 'Alvarado', NULL, '1986-05-18', 'M', '+50764074444',
     'luis.alvarado@example.com', 'ACTIVO', 'Calle 3, Casa 9', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, 'PE-8-567', NULL, NULL, '2020-11-10 10:30', '1', NULL, 'Hep B (infantil)', NULL,
     N'Policlínica Lic. Manuel María Valdés', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente 'PE-9-1', NULL, NULL, 'Luisa', 'Castillo', NULL, '1990-06-25', 'F', '+50760050000',
     'luisa.castillo@example.com', 'ACTIVO', 'Avenida 5, Local 3', 'San Miguelito', NULL;
-- TODO insertarle dosis
GO
EXEC sp_vacunas_gestionar_paciente 'PE-9-123', NULL, NULL, 'Sergio', N'Ramírez', NULL, '1983-09-23', 'M',
     '+50760008888', 'sergio.ramirez@example.com', 'ACTIVO', 'Avenida 8, Apartamento 20', 'La Chorrera', NULL;
-- TODO insertarle dosis
GO
-- Pasaportes
EXEC sp_vacunas_gestionar_paciente NULL, 'AB1234567X', NULL, N'Sofía', N'González', NULL, '1986-12-15', 'F',
     '+50760066666', 'sofia.nzalez1@example.com', 'ACTIVO', 'Calle 11, Casa 6', 'La Chorrera', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'AB1234567X', NULL, '2015-03-14 13:30', '1', NULL, 'Hep B (adultos)', NULL,
     N'Hospital Regional de Changuinola Raul Dávila Mena', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'AB1234567X', NULL, '2019-08-10 12:30', '1', NULL, 'Influenza (FluQuadri)',
     NULL, N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'AB9876543K', NULL, 'Laura', 'Salazar', N'García', '1993-07-12', 'F',
     '+50767079999', 'laura.salazar1@example.com', 'ACTIVO', 'Calle 8, Edificio 6', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'AB9876543K', NULL, '2023-11-10 12:15', '1', NULL, 'Rotarix', NULL,
     N'Pacífica Salud Hospital Punta Pacífica', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'AB9876543K', NULL, '2021-09-10 14:15', '1', NULL, 'Pneumovax', NULL,
     'CSS Complejo Metropolitano', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'AB9876543K', NULL, '2017-06-05 10:30', '1', NULL, 'Tetravalente', NULL,
     N'Hospital Santo Tomás', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'CD6543210L', NULL, 'Fernando', N'Pérez', NULL, '1990-05-14', 'M',
     '+50760080000', 'fernando.perez1@example.com', 'ACTIVO', 'Edificio La Roca, Apartamento 7', 'David', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'CD6543210L', NULL, '2023-10-05 14:00', '1', NULL, 'Priorix', NULL,
     'CSS Complejo Metropolitano', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'CD6543210L', NULL, '2019-07-05 13:00', '1', NULL, 'Meningococo', NULL,
     'Hospital Aquilino Tejeira', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'CD9876543Y', NULL, 'Mario', N'Ramírez', NULL, '1995-09-12', 'M',
     '+50760067777', 'mario.ramirez@example.com', 'ACTIVO', 'Calle 3, Edificio 2', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'CD9876543Y', NULL, '2021-08-01 10:30', '1', NULL, 'COVID-19', NULL,
     N'Pacífica Salud Hospital Punta Pacífica', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'CD9876543Y', NULL, '2017-05-20 09:00', '1', NULL, 'Hep A (Euvax) (adultos)',
     NULL, N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'EF123456Z', NULL, N'Verónica', N'Vásquez', N'Gómez', '1992-07-15', 'F',
     '+50760068888', 'veronica.vasquez1@example.com', 'ACTIVO', 'Edificio La Vista, Piso 7', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'EF123456Z', NULL, '2023-07-10 09:45', '1', NULL, 'Tetravalente', NULL,
     N'Hospital Dr. Nicolás A. Solano', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'EF123456Z', NULL, '2019-04-10 11:45', '1', NULL, 'Hep B (adultos)', NULL,
     'Centro de salud Pacora', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'EF4321098M', NULL, 'Natalia', N'Vásquez', NULL, '1988-02-24', 'F',
     '+50760081111', 'natalia.vasquez1@example.com', 'ACTIVO', 'Calle 4, Edificio 1', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'EF4321098M', NULL, '2021-06-25 11:00', '1', NULL, 'Priorix', NULL,
     N'Hospital Santo Tomás', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'EF4321098M', NULL, '2017-03-05 14:15', '1', NULL, 'Varivax', NULL,
     N'Hospital Regional de Changuinola Raul Dávila Mena', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'GH3210987N', NULL, 'Pedro', 'Castillo', 'Morales', '1992-04-18', 'M',
     '+50760082222', 'pedro.castillo@example.com', 'ACTIVO', 'Calle 9, Casa 14', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'GH3210987N', NULL, '2023-06-05 13:30', '1', NULL, 'Meningococo', NULL,
     N'Hospital Santo Tomás', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'GH6543210W', NULL, 'Francisco', N'Sánchez', NULL, '1988-11-02', 'M',
     '+50760069999', 'francisco.sanchez1@example.com', 'ACTIVO', 'Calle 7, Casa 9', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'GH6543210W', NULL, '2021-05-10 12:30', '1', NULL, 'Tetravalente', NULL,
     'Complejo Hospitalario Doctor Arnulfo Arias Madrid', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'GH6543210W', NULL, '2019-03-05 14:00', '1', NULL, 'Hexaxim', NULL,
     N'Hospital San Miguel Arcángel', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'IJ2109876O', NULL, N'Sofía', N'Rodríguez', NULL, '1995-12-20', 'F',
     '+50760083333', 'sofia.rodriguez1@example.com', 'ACTIVO', 'Avenida 3, Edificio 9', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'IJ2109876O', NULL, '2021-04-20 09:00', '1', NULL, 'Hep A (Euvax) (adultos)',
     NULL, N'Hospital Regional de Changuinola Raul Dávila Mena', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'IJ789456A', NULL, 'Daniel', 'Cruz', 'Alvarado', '1993-10-20', 'M',
     '+50760070000', 'daniel.cruz1@example.com', 'ACTIVO', 'Avenida 2, Edificio 4', 'David', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'IJ789456A', NULL, '2023-05-25 10:00', '1', NULL, 'Hep B (infantil)', NULL,
     N'Policlínica Lic. Manuel María Valdés', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'IJ789456A', NULL, '2019-02-20 15:30', '1', NULL, 'COVID-19', NULL,
     N'Policlínica Lic. Manuel María Valdés', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'KL0123456B', NULL, 'Camila', N'Pérez', NULL, '1995-01-05', 'F',
     '+50760071111', 'camila.perez@example.com', 'ACTIVO', 'Calle 4, Casa 12', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'KL0123456B', NULL, '2021-03-15 10:45', '1', NULL,
     'Neumoco conjugado (Prevenar 13 valente)', NULL, 'Hospital Aquilino Tejeira', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'KL1098765P', NULL, 'Carlos', 'Alvarez', N'Gómez', '1986-06-14', 'M',
     '+50760984444', 'carlos.alvarez1@example.com', 'ACTIVO', 'Calle 12, Local 7', 'David', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'KL1098765P', NULL, '2023-04-20 14:15', '1', NULL,
     'Neumoco conjugado (Prevenar 13 valente)', NULL, 'Complejo Hospitalario Dr. Manuel Amador Guerrero', NULL, NULL,
     NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'KL1098765P', NULL, '2019-01-10 12:00', '1', NULL, 'Fiebre Amarilla', NULL,
     N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'MN0987654Q', NULL, 'Camila', N'Hernández', NULL, '1993-08-15', 'F',
     '+50760085555', 'camila.hernandez@example.com', 'ACTIVO', 'Edificio El Dorado, Apartamento 2', 'San Miguelito',
     NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'MN0987654Q', NULL, '2021-02-10 14:00', '1', NULL, 'BCG', NULL,
     'Centro de salud Pacora', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'MN876543C', NULL, N'Tomás', N'González', N'Sánchez', '1991-08-15', 'M',
     '+50766072222', 'tomas.nzalez1@example.com', 'ACTIVO', 'Calle 6, Local 8', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'MN876543C', NULL, '2023-03-15 16:00', '1', NULL, 'Hep A (Euvax) (infantil)',
     NULL, 'Centro de salud Pacora', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'MN876543C', NULL, '2018-12-05 13:30', '1', NULL, 'Hep A (Euvax) (adultos)',
     NULL, 'Hospital Nacional', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'OP3456789D', NULL, N'María', N'Rodríguez', NULL, '1987-12-25', 'F',
     '+50760073333', 'maria.rodriguez@example.com', 'ACTIVO', 'Edificio Los Cedros, Apartamento 2', 'San Miguelito',
     NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'OP3456789D', NULL, '2020-12-15 13:00', '1', NULL, 'Pneumovax', NULL,
     N'Hospital Santo Tomás', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'OP8765432R', NULL, 'Luis', N'Gómez', NULL, '1984-01-12', 'M', '+50762086666',
     'luis.mez1@example.com', 'ACTIVO', 'Calle 7, Edificio 6', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'OP8765432R', NULL, '2023-01-05 10:45', '1', NULL, 'Pneumovax', NULL,
     N'Hospital San Miguel Arcángel', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'OP8765432R', NULL, '2018-10-01 10:15', '1', NULL, 'Priorix', NULL,
     'Hospital Aquilino Tejeira', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'QR4567890E', NULL, 'Luis', 'Alvarado', NULL, '1986-05-18', 'M',
     '+50760074444', 'luis.alvarado1@example.com', 'ACTIVO', 'Calle 3, Casa 9', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'QR4567890E', NULL, '2020-10-05 09:45', '1', NULL, 'Hep A (Euvax) (infantil)',
     NULL, N'Pacífica Salud Hospital Punta Pacífica', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'QR4567890E', NULL, '2018-08-10 13:00', '1', NULL, 'Meningococo', NULL,
     N'Hospital Santo Tomás', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'QR7654321S', NULL, 'Sandra', N'Pérez', 'Castillo', '1995-10-10', 'F',
     '+50760087777', 'sandra.perez1@example.com', 'ACTIVO', 'Calle 5, Casa 12', N'Colón', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'QR7654321S', NULL, '2022-11-10 11:30', '1', NULL, 'Fiebre Amarilla', NULL,
     N'Hospital Dr. Rafael Hernández', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'ST2345678F', NULL, 'Elena', N'Sánchez', N'Vásquez', '1994-09-19', 'F',
     '+50760075555', 'elena.sanchez1@example.com', 'ACTIVO', 'Avenida 8, Edificio 5', 'David', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'ST2345678F', NULL, '2020-09-20 15:00', '1', NULL, 'Priorix', NULL,
     'Complejo Hospitalario Doctor Arnulfo Arias Madrid', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'ST6543210T', NULL, 'Antonio', N'Sánchez', NULL, '1989-07-14', 'M',
     '+50768088888', 'antonio.sanchez1@example.com', 'ACTIVO', 'Calle 9, Edificio 8', 'David', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'ST6543210T', NULL, '2022-10-15 09:00', '1', NULL, 'COVID-19', NULL,
     N'Policlínica Lic. Manuel María Valdés', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'ST6543210T', NULL, '2018-07-20 14:15', '1', NULL, 'TD', NULL,
     N'Policlínica Lic. Manuel María Valdés', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'UV3456789G', NULL, 'Gabriel', 'Ruiz', NULL, '1992-06-22', 'M', '+50760076666',
     'gabriel.ruiz@example.com', 'ACTIVO', 'Calle 7, Edificio 3', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'UV3456789G', NULL, '2020-08-10 14:15', '1', NULL, 'MMR', NULL,
     'Hospital Nacional', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'UV5432109U', NULL, 'Isabella', 'Alvarado', 'Ruiz', '1996-03-22', 'F',
     '+50760089999', 'isabella.alvarado@example.com', 'ACTIVO', 'Edificio La Vista, Piso 4', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'UV5432109U', NULL, '2022-09-05 15:00', '1', NULL, 'Varivax', NULL,
     N'Hospital de Especialidades Pediátricas Omar Torrijos Herrera', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'UV5432109U', NULL, '2018-06-10 16:00', '1', NULL, 'Hep B (infantil)', NULL,
     N'Hospital Dr. Nicolás A. Solano', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'WX4321098V', NULL, 'Santiago', 'Morales', N'Rodríguez', '1991-03-27', 'M',
     '+50760065555', 'santia.morales@example.com', 'ACTIVO', 'Calle 8, Local 11', 'Bocas del Toro', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'WX4321098V', NULL, '2020-07-01 12:00', '1', NULL, 'Influenza (FluQuadri)',
     NULL, N'Hospital San Miguel Arcángel', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'WX4321098V', NULL, '2018-05-05 10:30', '1', NULL,
     'Neumoco conjugado (Prevenar 13 valente)', NULL, 'Centro de salud Pacora', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'WX4561234H', NULL, 'Marta', N'Gómez', 'Alvarado', '1988-11-12', 'F',
     '+50760077777', 'marta.mez1@example.com', 'ACTIVO', 'Edificio El Sol, Apartamento 4', N'Panamá', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'WX4561234H', NULL, '2022-08-20 10:30', '1', NULL, 'Priorix', NULL,
     'Complejo Hospitalario Doctor Arnulfo Arias Madrid', NULL, NULL, NULL, NULL;
GO
EXEC sp_vacunas_gestionar_paciente NULL, 'YZ7890123J', NULL, 'Juan', N'Martínez', NULL, '1996-03-28', 'M',
     '+50760078888', 'juan.martinez@example.com', 'ACTIVO', 'Calle 12, Casa 15', 'San Miguelito', NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'YZ7890123J', NULL, '2022-07-10 12:45', '1', NULL, 'Influenza (FluQuadri)',
     NULL, 'CSS Complejo Metropolitano', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'YZ7890123J', NULL, '2020-06-10 11:15', '1', NULL, 'Hep A (Euvax) (adultos)',
     NULL, N'Hospital Dr. Rafael Hernández', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'YZ7890123J', NULL, '2020-05-15 13:30', '1', NULL, 'Fiebre Amarilla', NULL,
     N'Hospital Regional de Changuinola Raul Dávila Mena', NULL, NULL, NULL, NULL;
EXEC sp_vacunas_insert_dosis NULL, NULL, 'YZ7890123J', NULL, '2018-04-20 11:00', '1', NULL, 'Hexaxim', NULL,
     N'Pacífica Salud Hospital Punta Pacífica', NULL, NULL, NULL, NULL;
GO

/*
PRINT('Insertando usuarios de roles superior para pruebas');
-- COLOCAR SU CONTRASEÑA HASH CON BCRYPT AQUÍ DONDE DICE 'HASH AQUI'
EXEC sp_vacunas_gestionar_doctor '1-1-2', NULL, '123456789', 'Pedro', 'Jiménez',  NULL, '2000-12-12', 'M', NULL, NULL, 'ACTIVO', NULL, NULL, 'MEDICO GENERAL', NULL, NULL, NULL;
EXEC sp_vacunas_gestionar_usuario NULL, '1-1-2', NULL, NULL, NULL, 'pruebasDoc', 'HASH AQUI', NULL, NULL;
EXEC sp_vacunas_insert_roles_usuario NULL, NULL, NULL, 'pruebasDoc', NULL, NULL, '4', NULL;
GO

EXEC sp_vacunas_gestionar_persona '1-1-1', NULL, 'Juan', NULL, NULL, NULL, '2000-01-12', 'M', NULL, NULL, 'ACTIVO', NULL, NULL, NULL;
EXEC sp_vacunas_gestionar_usuario NULL, '1-1-1', NULL, NULL, NULL, 'pruebasAut', 'HASH AQUI', 'ACTIVO', NULL;
EXEC sp_vacunas_insert_roles_usuario NULL, NULL, NULL, 'pruebasAut', NULL, NULL, '7', NULL;
GO
*/
PRINT (N'Fin de la inicialización con datos de pruebas Vacunas Panamá');
USE master;
