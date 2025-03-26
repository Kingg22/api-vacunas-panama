USE master;
GO
-- Verifica si la base de datos ya existe
IF DB_ID('vacunas') IS NULL
    BEGIN
        CREATE DATABASE vacunas;
    END
ELSE
    BEGIN
        DROP DATABASE vacunas;
        CREATE DATABASE vacunas;
    END
GO

USE vacunas;
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

PRINT (N'Creando tablas');
GO

CREATE TABLE permisos
(
    id          SMALLINT PRIMARY KEY IDENTITY (1,1),
    nombre      NVARCHAR(100) NOT NULL,
    descripcion NVARCHAR(100),
    created_at  DATETIME      NOT NULL
        CONSTRAINT df_permisos_created DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME,
    CONSTRAINT uq_permisos_nombre UNIQUE (nombre),
    INDEX ix_permisos_nombre (nombre)
);
GO
CREATE TABLE roles
(
    id          SMALLINT PRIMARY KEY IDENTITY (1,1),
    nombre      NVARCHAR(100) NOT NULL,
    descripcion NVARCHAR(100),
    created_at  DATETIME      NOT NULL
        CONSTRAINT df_roles_created DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME,
    CONSTRAINT uq_roles_nombre UNIQUE (nombre),
    INDEX ix_roles_nombre (nombre)
);
GO
CREATE TABLE roles_permisos
(
    rol        SMALLINT,
    permiso    SMALLINT,
    created_at DATETIME NOT NULL
        CONSTRAINT df_roles_permisos_created DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    PRIMARY KEY (rol, permiso),
    CONSTRAINT fk_roles_permisos_roles FOREIGN KEY (rol) REFERENCES roles (id) ON DELETE CASCADE,
    CONSTRAINT fk_roles_permisos_permisos FOREIGN KEY (permiso) REFERENCES permisos (id) ON DELETE CASCADE
);
GO
CREATE TABLE usuarios
(
    id         UNIQUEIDENTIFIER PRIMARY KEY
        CONSTRAINT df_usuarios_id DEFAULT NEWID(),
    usuario    NVARCHAR(50),
    clave      NVARCHAR(100) NOT NULL,
    created_at DATETIME      NOT NULL
        CONSTRAINT df_usuarios_created DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    last_used  DATETIME,
    CONSTRAINT ck_usuarios_clave CHECK (clave LIKE '$2_$_%' AND LEN(clave) >= 60)
);
GO
CREATE UNIQUE NONCLUSTERED INDEX ix_usuarios_usuario
    ON usuarios (usuario)
    WHERE usuario IS NOT NULL;
GO
CREATE TABLE usuarios_roles
(
    usuario    UNIQUEIDENTIFIER,
    rol        SMALLINT,
    created_at DATETIME NOT NULL
        CONSTRAINT df_usuarios_roles_created DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    PRIMARY KEY (usuario, rol),
    CONSTRAINT fk_usuarios_roles_usuarios FOREIGN KEY (usuario) REFERENCES usuarios (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuarios_roles_roles FOREIGN KEY (rol) REFERENCES roles (id) ON DELETE CASCADE
);
GO
CREATE TABLE provincias
(
    id     TINYINT PRIMARY KEY IDENTITY (0,1),
    nombre NVARCHAR(30) NOT NULL,
    CONSTRAINT ck_provincia_existe CHECK (nombre IN
                                          ('Por registrar', /* Problemas o nueva provincia sin registrar aún */
                                           'Bocas del Toro', /* 1 */
                                           N'Coclé', /* 2 */
                                           N'Colón', /* 3 */
                                           N'Chiriquí', /* 4 */
                                           N'Darién', /* 5 */
                                           'Herrera', /* 6 */
                                           'Los Santos', /* 7 */
                                           N'Panamá', /* 8 */
                                           'Veraguas', /* 9 */
                                           'Guna Yala', /* 10 */
                                           N'Emberá-Wounaan', /* 11*/
                                           N'Ngäbe-Buglé',/* 12 */
                                           N'Panamá Oeste', /* 13 */
                                           N'Naso Tjër Di', /* 14 */
/* No Fijas, pueden moverse por nuevas provincias o comarcas nivel provincia oficiales. Orden dado por número de cédula panameña */
                                           N'Guna de Madugandí', /*15 comarca nivel corregimiento */
                                           N'Guna de Wargandí', /*16 comarca nivel corregimiento */
                                           'Extranjero' /* 17 */
                                              )),
    INDEX ix_provincias_nombre (nombre)
);
GO
CREATE TABLE distritos
(
    id        TINYINT PRIMARY KEY IDENTITY (0,1),
    nombre    NVARCHAR(100) NOT NULL,
    provincia TINYINT       NOT NULL,
    CONSTRAINT ck_distritos_provincias CHECK (
        (provincia = 0 AND nombre LIKE 'Por registrar') OR
        (provincia = 1 AND nombre IN ('Almirante',
                                      'Bocas del Toro',
                                      'Changuinola',
                                      N'Chiriquí Grande')) OR
        (provincia = 2 AND nombre IN ('Aguadulce',
                                      N'Antón',
                                      'La Pintada',
                                      N'Natá',
                                      N'Olá',
                                      N'Penonomé')) OR
        (provincia = 3 AND nombre IN ('Chagres',
                                      N'Colón',
                                      'Donoso',
                                      'Portobelo',
                                      'Santa Isabel',
                                      'Omar Torrijos Herrera')) OR
        (provincia = 4 AND nombre IN ('Alanje',
                                      N'Barú',
                                      N'Boquerón',
                                      'Boquete',
                                      'Bugaba',
                                      'David',
                                      'Dolega',
                                      'Gualaca',
                                      'Remedios',
                                      'Renacimiento',
                                      N'San Félix',
                                      'San Lorenzo',
                                      'Tierras Altas',
                                      N'Tolé')) OR
        (provincia = 5 AND nombre IN ('Chepigana',
                                      'Pinogana',
                                      'Santa Fe',
                                      N'Guna de Wargandí')) OR
        (provincia = 6 AND nombre IN (N'Chitré',
                                      'Las Minas',
                                      'Los Pozos',
                                      N'Ocú',
                                      'Parita',
                                      N'Pesé',
                                      N'Santa María')) OR
        (provincia = 7 AND nombre IN (N'Guararé',
                                      'Las Tablas',
                                      'Los Santos',
                                      'Macaracas',
                                      N'Pedasí',
                                      N'Pocrí',
                                      N'Tonosí')) OR
        (provincia = 8 AND nombre IN ('Balboa',
                                      'Chepo',
                                      N'Chimán',
                                      N'Panamá',
                                      'San Miguelito',
                                      'Taboga')) OR
        (provincia = 9 AND nombre IN ('Atalaya',
                                      'Calobre',
                                      N'Cañazas',
                                      'La Mesa',
                                      'Las Palmas',
                                      'Mariato',
                                      'Montijo',
                                      N'Río de Jesús',
                                      'San Francisco',
                                      'Santa Fe',
                                      'Santiago',
                                      N'Soná')) OR
/* comarca Guna Yala, Madugandí, Wargandí no tiene distrito, provincia 10 */
        (provincia = 11 AND nombre IN (N'Cémaco', N'Sambú')) OR
        (provincia = 12 AND nombre IN (N'Besikó',
                                       'Jirondai',
                                       N'Kankintú',
                                       N'Kusapín',
                                       N'Mironó',
                                       N'Müna',
                                       'Nole Duima',
                                       N'Ñürüm',
                                       'Santa Catalina',
                                       N'Calovébora')) OR
        (provincia = 13 AND nombre IN (N'Arraiján',
                                       'Capira',
                                       'Chame',
                                       'La Chorrera',
                                       'San Carlos')) OR
        (provincia = 14 AND nombre IN (N'Naso Tjër Di')) OR
        (provincia = 17 AND nombre IN ('Extranjero')) OR
        (provincia IS NULL AND nombre IS NULL) -- Permitir NULL si es necesario
        ),
    CONSTRAINT fk_distritos_provincias FOREIGN KEY (provincia) REFERENCES provincias (id),
    INDEX ix_distritos_nombre (nombre)
);
GO
CREATE TABLE direcciones
(
    id         UNIQUEIDENTIFIER PRIMARY KEY
        CONSTRAINT df_direcciones_id DEFAULT NEWID(),
    direccion  VARCHAR(150) NOT NULL,
    distrito   TINYINT      NOT NULL
        CONSTRAINT df_direcciones_distrito DEFAULT 0,
    created_at DATETIME     NOT NULL
        CONSTRAINT df_direcciones_created DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    CONSTRAINT fk_direcciones_distritos FOREIGN KEY (distrito) REFERENCES distritos (id),
    INDEX ix_direcciones_direccion (direccion)
);
GO
CREATE TABLE personas
(
    id               UNIQUEIDENTIFIER PRIMARY KEY
        CONSTRAINT df_personas_id DEFAULT NEWID(),
    cedula           VARCHAR(15),
    pasaporte        VARCHAR(20),
    nombre           NVARCHAR(100),
    nombre2          NVARCHAR(100),
    apellido1        NVARCHAR(100),
    apellido2        NVARCHAR(100),
    correo           VARCHAR(254),
    telefono         VARCHAR(15),
    fecha_nacimiento SMALLDATETIME,
    edad             TINYINT,
    sexo             CHAR(1)
        CONSTRAINT df_personas_sexo DEFAULT ('X'),
    estado           NVARCHAR(50)     NOT NULL
        CONSTRAINT df_personas_estado DEFAULT ('NO_VALIDADO'),
    disabled         BIT
        CONSTRAINT df_personas_disabled DEFAULT (0),
    direccion        UNIQUEIDENTIFIER NOT NULL,
    usuario          UNIQUEIDENTIFIER,
    CONSTRAINT ck_personas_sexo CHECK (sexo LIKE 'M' OR sexo LIKE 'F' OR sexo LIKE 'X'),
    CONSTRAINT ck_personas_telefono CHECK (telefono IS NULL OR (telefono LIKE '+[0-9]%' AND LEN(telefono) >= 5 AND
                                                                TRY_CAST(SUBSTRING(telefono, 2, LEN(telefono)) AS BIGINT) IS NOT NULL)),
    CONSTRAINT ck_personas_cedula CHECK (cedula IS NULL OR (
        (cedula LIKE 'PE-%' OR cedula LIKE 'E-%' OR cedula LIKE 'N-%' OR cedula LIKE '[1-9]-%' OR
         cedula LIKE '[10-13]-%'
            OR cedula LIKE '[1-9]AV-%' OR cedula LIKE '[10-13]AV-%' OR cedula LIKE '[1-9]PI-%' OR
         cedula LIKE '[10-13]PI-%')
            AND cedula LIKE '%-[0-9][0-9][0-9][0-9]-%'
            AND cedula LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')),
    CONSTRAINT ck_personas_pasaporte CHECK (pasaporte LIKE '[A-Z0-9]%' AND LEN(pasaporte) >= 5),
    CONSTRAINT ck_personas_fecha_nacimiento CHECK (fecha_nacimiento IS NULL OR fecha_nacimiento <= GETDATE()),
    CONSTRAINT ck_personas_estado CHECK (estado IN ('ACTIVO', 'NO_VALIDADO', 'FALLECIDO', 'INACTIVO', 'DESACTIVADO')),
    CONSTRAINT ck_personas_identificacion CHECK ((cedula IS NOT NULL) OR (pasaporte IS NOT NULL)),
    CONSTRAINT fk_personas_direcciones FOREIGN KEY (direccion) REFERENCES direcciones (id) ON UPDATE CASCADE,
    CONSTRAINT fk_personas_usuarios FOREIGN KEY (usuario) REFERENCES usuarios (id) ON UPDATE CASCADE,
    INDEX ix_personas_nombres_apellidos (nombre, nombre2, apellido1, apellido2)
)
GO
CREATE UNIQUE NONCLUSTERED INDEX ix_personas_cedula
    ON personas (cedula)
    WHERE cedula IS NOT NULL;
GO
CREATE UNIQUE NONCLUSTERED INDEX ix_personas_pasaporte
    ON personas (pasaporte)
    WHERE pasaporte IS NOT NULL;
GO
CREATE UNIQUE NONCLUSTERED INDEX ix_personas_correo
    ON personas (correo)
    WHERE correo IS NOT NULL;
GO
CREATE UNIQUE NONCLUSTERED INDEX ix_personas_telefono
    ON personas (telefono)
    WHERE telefono IS NOT NULL;
GO
CREATE UNIQUE NONCLUSTERED INDEX ix_personas_usuario
    ON personas (usuario)
    WHERE usuario IS NOT NULL;
GO

CREATE TABLE entidades
(
    id          UNIQUEIDENTIFIER PRIMARY KEY
        CONSTRAINT df_entidades_id DEFAULT NEWID(),
    nombre      NVARCHAR(100)    NOT NULL,
    correo      VARCHAR(254),
    telefono    VARCHAR(15),
    dependencia NVARCHAR(13),
    estado      NVARCHAR(50)     NOT NULL
        CONSTRAINT df_entidades_estado DEFAULT ('ACTIVO'),
    disabled    BIT
        CONSTRAINT df_entidades_disabled DEFAULT (0),
    direccion   UNIQUEIDENTIFIER NOT NULL,
    CONSTRAINT ck_entidades_correo CHECK (correo IS NULL OR (correo LIKE '%_@_%.__%' AND LEN(correo) >= 5)),
    CONSTRAINT ck_entidades_telefono CHECK (telefono IS NULL OR (telefono LIKE '+[0-9]%' AND LEN(telefono) >= 5 AND
                                                                 TRY_CAST(SUBSTRING(telefono, 2, LEN(telefono)) AS BIGINT) IS NOT NULL)),
    CONSTRAINT ck_entidades_dependencia CHECK (dependencia IS NULL OR
                                               dependencia IN ('CSS', 'MINSA', 'PRIVADA', 'POR_REGISTRAR')),
    CONSTRAINT ck_entidades_estado CHECK (estado IN ('ACTIVO', 'NO_VALIDADO', 'INACTIVO', 'DESACTIVADO')),
    CONSTRAINT fk_entidades_direcciones FOREIGN KEY (direccion) REFERENCES direcciones (id) ON UPDATE CASCADE,
);
GO
CREATE UNIQUE NONCLUSTERED INDEX ix_entidades_correo
    ON entidades (correo)
    WHERE correo IS NOT NULL;
GO
CREATE UNIQUE NONCLUSTERED INDEX ix_entidades_telefono
    ON entidades (telefono)
    WHERE telefono IS NOT NULL;
GO

CREATE TABLE pacientes
(
    id                      UNIQUEIDENTIFIER PRIMARY KEY,
    identificacion_temporal VARCHAR(255),
    created_at              DATETIME NOT NULL
        CONSTRAINT df_pacientes_created DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME,
    CONSTRAINT ck_pacientes_id_temporal CHECK (
-- opción 1 no identificado
        (identificacion_temporal LIKE 'NI-%') OR
-- opción 2 recién nacidos
        ((identificacion_temporal LIKE 'RN-%' OR
          (identificacion_temporal LIKE ('RN[1-9]-%') OR identificacion_temporal LIKE ('RN[1-9][1-9]-%')) AND
          (identificacion_temporal LIKE '%-PE-%' OR identificacion_temporal LIKE '%-E-%' OR
           identificacion_temporal LIKE '%-N-%' OR identificacion_temporal LIKE '%-[1-13]%')
              AND identificacion_temporal LIKE '%-[0-9][0-9][0-9][0-9]-%'
              AND identificacion_temporal LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
            )),
    CONSTRAINT fk_pacientes_personas FOREIGN KEY (id) REFERENCES personas (id)
);
GO
CREATE UNIQUE NONCLUSTERED INDEX ix_pacientes_id_temporal
    ON pacientes (identificacion_temporal)
    WHERE identificacion_temporal IS NOT NULL;
GO

CREATE TABLE sedes
(
    id         UNIQUEIDENTIFIER PRIMARY KEY,
    region     NVARCHAR(50),
    created_at DATETIME NOT NULL
        CONSTRAINT df_sedes_created DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    CONSTRAINT fk_sedes_entidades FOREIGN KEY (id) REFERENCES entidades (id),
    INDEX ix_sedes_region (region)
);
GO

CREATE TABLE doctores
(
    id         UNIQUEIDENTIFIER PRIMARY KEY
        CONSTRAINT df_doctores_id DEFAULT NEWID(),
    idoneidad  NVARCHAR(20) NOT NULL,
    categoria  NVARCHAR(100),
    sede       UNIQUEIDENTIFIER,
    created_at DATETIME     NOT NULL
        CONSTRAINT df_doctores_created DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME,
    CONSTRAINT fk_doctores_personas FOREIGN KEY (id) REFERENCES personas (id),
    CONSTRAINT fk_doctores_sedes FOREIGN KEY (sede) REFERENCES sedes (id),
    INDEX ix_doctores_idoneidad (idoneidad)
);
GO

-- TODO crear tabla intermedia dosis_intervalos
-- TODO eliminar la relación vacuna fabricante a una vacuna por fabricante
CREATE TABLE vacunas
(
    id                       UNIQUEIDENTIFIER PRIMARY KEY
        CONSTRAINT df_vacunas_id DEFAULT NEWID(),
    nombre                   NVARCHAR(100) NOT NULL,
    --fabricante UNIQUEIDENTIFIER NOT NULL,
    edad_minima_dias         INT,
    intervalo_dosis_1_2_dias INT,
    dosis_maxima             CHAR(2),
    created_at               DATETIME      NOT NULL
        CONSTRAINT df_vacunas_created DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME,
    CONSTRAINT ck_vacunas_dosis_maxima CHECK (dosis_maxima IN ('1', '2', '3', 'R', 'R1', 'R2', 'P')),
    CONSTRAINT ck_vacunas_edad_minima CHECK (edad_minima_dias >= 0),
    --CONSTRAINT fk_vacunas_fabricantes FOREIGN KEY (fabricante) REFERENCES fabricantes (id) ON UPDATE CASCADE,
    INDEX ix_vacunas_nombre (nombre)
);
GO

CREATE TABLE dosis
(
    id               UNIQUEIDENTIFIER PRIMARY KEY
        CONSTRAINT df_dosis_id DEFAULT NEWID(),
    paciente         UNIQUEIDENTIFIER NOT NULL,
    fecha_aplicacion DATETIME         NOT NULL
        CONSTRAINT df_dosis_aplicacion DEFAULT CURRENT_TIMESTAMP,
    numero_dosis     CHAR(2)          NOT NULL,
    vacuna           UNIQUEIDENTIFIER NOT NULL,
    sede             UNIQUEIDENTIFIER NOT NULL,
    doctor           UNIQUEIDENTIFIER,
    lote             NVARCHAR(50),
    created_at       DATETIME         NOT NULL
        CONSTRAINT df_dosis_created DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME,
    CONSTRAINT ck_dosis_numero_dosis CHECK (numero_dosis IN ('1', '2', '3', 'R', 'R1', 'R2', 'P')),
    CONSTRAINT fk_dosis_pacientes FOREIGN KEY (paciente) REFERENCES pacientes (id),
    CONSTRAINT fk_dosis_vacunas FOREIGN KEY (vacuna) REFERENCES vacunas (id) ON UPDATE CASCADE,
    CONSTRAINT fk_dosis_sedes FOREIGN KEY (sede) REFERENCES sedes (id) ON UPDATE CASCADE
);
GO

CREATE TABLE fabricantes
(
    id                UNIQUEIDENTIFIER PRIMARY KEY,
    licencia          NVARCHAR(50) NOT NULL,
    contacto_nombre   NVARCHAR(100),
    contacto_correo   VARCHAR(254),
    contacto_telefono VARCHAR(15),
    created_at        DATETIME     NOT NULL
        CONSTRAINT df_fabricantes_created DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME,
    usuario           UNIQUEIDENTIFIER,
    CONSTRAINT ck_fabricantes_licencia CHECK (licencia LIKE '%/DNFD'),
    CONSTRAINT ck_fabricantes_correo CHECK (contacto_correo IS NULL OR
                                            (contacto_correo LIKE '%_@_%.__%' AND LEN(contacto_correo) >= 5)),
    CONSTRAINT ck_fabricantes_telefono CHECK (contacto_telefono IS NULL OR
                                              (contacto_telefono LIKE '+[0-9]%' AND
                                               LEN(contacto_telefono) >= 5 AND
                                               TRY_CAST(SUBSTRING(contacto_telefono, 2, LEN(contacto_telefono)) AS BIGINT) IS NOT NULL)),
    CONSTRAINT fk_fabricantes_entidades FOREIGN KEY (id) REFERENCES entidades (id),
    CONSTRAINT fk_fabricantes_usuarios FOREIGN KEY (usuario) REFERENCES usuarios (id) ON UPDATE CASCADE,
    INDEX ix_fabricantes_licencia (licencia)
);
GO
CREATE UNIQUE NONCLUSTERED INDEX ix_fabricantes_usuario
    ON fabricantes (usuario)
    WHERE usuario IS NOT NULL;
GO

CREATE TABLE almacenes
(
    id        UNIQUEIDENTIFIER PRIMARY KEY,
    encargado UNIQUEIDENTIFIER,
    CONSTRAINT fk_almacenes_entidades FOREIGN KEY (id) REFERENCES entidades (id),
    CONSTRAINT fk_almacenes_personas FOREIGN KEY (encargado) REFERENCES personas (id),
);
GO

CREATE TABLE almacenes_inventarios
(
    almacen          UNIQUEIDENTIFIER NOT NULL,
    vacuna           UNIQUEIDENTIFIER NOT NULL,
    cantidad         INT              NOT NULL,
    fecha_expiracion DATETIME         NOT NULL,
    lote             NVARCHAR(50)     NOT NULL,
    fecha_recepcion  DATETIME         NOT NULL
        CONSTRAINT df_almacenes_inventario_recepcion DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (almacen, vacuna),
    CONSTRAINT ck_almacenes_inventarios_fecha_expiracion CHECK (fecha_expiracion > GETDATE()),
    CONSTRAINT ck_almacenes_inventarios_cantidad CHECK (cantidad >= 0),
    CONSTRAINT fk_almacenes_inventarios_almacenes FOREIGN KEY (almacen) REFERENCES almacenes (id),
    CONSTRAINT fk_almacenes_inventarios_vacunas FOREIGN KEY (vacuna) REFERENCES vacunas (id)
);
GO

CREATE TABLE sedes_inventarios
(
    sede             UNIQUEIDENTIFIER NOT NULL,
    vacuna           UNIQUEIDENTIFIER NOT NULL,
    cantidad         INT              NOT NULL,
    fecha_expiracion DATETIME         NOT NULL,
    lote             NVARCHAR(50)     NOT NULL,
    fecha_recepcion  DATETIME         NOT NULL
        CONSTRAINT df_sedes_inventarios_recepcion DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (sede, vacuna),
    CONSTRAINT ck_sedes_inventarios_fecha_expiracion CHECK (fecha_expiracion > GETDATE()),
    CONSTRAINT ck_sedes_inventarios_cantidad CHECK (cantidad >= 0),
    CONSTRAINT fk_sedes_inventarios_sedes FOREIGN KEY (sede) REFERENCES sedes (id),
    CONSTRAINT fk_sedes_inventarios_vacunas FOREIGN KEY (vacuna) REFERENCES vacunas (id)
);
GO

CREATE TABLE distribuciones_vacunas
(
    id                 UNIQUEIDENTIFIER PRIMARY KEY
        CONSTRAINT df_distribuciones_id DEFAULT NEWID(),
    almacen            UNIQUEIDENTIFIER NOT NULL,
    sede               UNIQUEIDENTIFIER NOT NULL,
    vacuna             UNIQUEIDENTIFIER NOT NULL,
    cantidad           INT              NOT NULL,
    lote               NVARCHAR(50)     NOT NULL,
    fecha_distribucion DATETIME         NOT NULL
        CONSTRAINT df_distribuciones_fecha_distribucion DEFAULT CURRENT_TIMESTAMP,
    created_at         DATETIME         NOT NULL
        CONSTRAINT df_distribuciones_created DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME,
    CONSTRAINT ck_distribuciones_fecha_distribucion CHECK (fecha_distribucion <= GETDATE()),
    CONSTRAINT fk_distribuciones_almacenes FOREIGN KEY (almacen) REFERENCES almacenes (id),
    CONSTRAINT fk_distribuciones_sedes FOREIGN KEY (sede) REFERENCES sedes (id),
    CONSTRAINT fk_distribuciones_vacunas FOREIGN KEY (vacuna) REFERENCES vacunas (id)
);
GO

CREATE TABLE fabricantes_vacunas
(
    fabricante UNIQUEIDENTIFIER,
    vacuna     UNIQUEIDENTIFIER,
    PRIMARY KEY (fabricante, vacuna),
    CONSTRAINT fk_fabricantes_vacunas_fabricantes FOREIGN KEY (fabricante) REFERENCES fabricantes (id) ON UPDATE CASCADE,
    CONSTRAINT fk_fabricantes_vacunas_vacunas FOREIGN KEY (vacuna) REFERENCES vacunas (id) ON DELETE CASCADE
);
GO

CREATE TABLE enfermedades
(
    id             INT PRIMARY KEY IDENTITY (0,1),
    nombre         NVARCHAR(100) NOT NULL,
    nivel_gravedad NVARCHAR(50),
    CONSTRAINT uq_enfermedades_nombre UNIQUE (nombre),
    INDEX ix_enfermedades_nombre (nombre)
);
GO

CREATE TABLE sintomas
(
    id     INT PRIMARY KEY IDENTITY (0,1),
    nombre NVARCHAR(50) NOT NULL,
    CONSTRAINT uq_sintomas_nombre UNIQUE (nombre),
    INDEX ix_sintomas_nombre (nombre)
);
GO

CREATE TABLE enfermedades_sintomas
(
    enfermedad INT,
    sintoma    INT,
    PRIMARY KEY (sintoma, enfermedad),
    CONSTRAINT fk_enfermedades_sintomas_sintomas FOREIGN KEY (sintoma) REFERENCES sintomas (id) ON UPDATE CASCADE,
    CONSTRAINT fk_enfermedades_sintomas_enfermedades FOREIGN KEY (enfermedad) REFERENCES enfermedades (id) ON UPDATE CASCADE
);
GO

CREATE TABLE vacunas_enfermedades
(
    vacuna     UNIQUEIDENTIFIER,
    enfermedad INT,
    PRIMARY KEY (vacuna, enfermedad),
    CONSTRAINT fk_vacunas_enfermedades_vacunas FOREIGN KEY (vacuna) REFERENCES vacunas (id) ON DELETE CASCADE,
    CONSTRAINT fk_vacunas_enfermedades_enfermedades FOREIGN KEY (enfermedad) REFERENCES enfermedades (id) ON DELETE CASCADE
);
GO
PRINT (N'Tablas terminadas');
GO
PRINT (N'Creando vistas');
GO
CREATE VIEW view_usuarios_detalles AS
SELECT u.id,
       u.usuario,
       pe.id       AS 'id_persona',
       pe.cedula,
       pe.pasaporte,
       pe.correo   AS 'correo_persona',
       pe.telefono AS 'telefono_persona',
       e.id        AS 'id_entidad',
       e.correo    AS 'correo_entidad',
       e.telefono  AS 'telefono_entidad',
       p.identificacion_temporal,
       doc.idoneidad,
       f.licencia,
       u.clave,
       u.created_at,
       u.updated_at,
       u.last_used
FROM usuarios u
         LEFT JOIN personas pe ON pe.usuario = u.id
         LEFT JOIN fabricantes f ON u.id = f.usuario
         LEFT JOIN entidades e ON e.id = f.id
         LEFT JOIN pacientes p ON pe.id = p.id
         LEFT JOIN doctores doc ON pe.id = doc.id
GO

CREATE VIEW view_pacientes_detalles AS
SELECT pe.cedula                 AS N'Cédula',
       pe.pasaporte              AS 'Pasaporte',
       p.identificacion_temporal AS N'Identificación Temporal',
       pe.nombre                 AS 'Nombre',
       pe.apellido1              AS 'Primer Apellido',
       pe.apellido2              AS 'Segundo Apellido',
       pe.fecha_nacimiento       AS 'Fecha de Nacimiento',
       pe.fecha_nacimiento       AS 'Edad',
       pe.sexo                   AS 'Sexo',
       pe.telefono               AS N'Teléfono',
       pe.correo                 AS N'Correo Electrónico',
       d.direccion               AS N'Dirección',
       dd.nombre                 AS 'Distrito',
       pp.nombre                 AS 'Provincia',
       pe.estado,
       p.created_at,
       p.updated_at,
       pe.usuario,
       p.id,
       d.id                      AS 'id_direccion',
       dd.id                     AS 'id_distrito',
       pp.id                     AS 'id_provincia'
FROM personas pe
         LEFT JOIN pacientes p ON p.id = pe.id
         LEFT JOIN direcciones d ON pe.direccion = d.id
         LEFT JOIN distritos dd ON d.distrito = dd.id
         LEFT JOIN provincias pp ON dd.provincia = pp.id;
GO

CREATE VIEW view_fabricantes_detalles AS
SELECT e.nombre            AS 'Nombre',
       f.licencia          AS 'Licencia Autorizada',
       e.telefono          AS 'Teléfono',
       e.correo            AS 'Correo Electrónico',
       f.contacto_nombre   AS 'Nombre Contacto',
       f.contacto_correo   AS 'Correo Electrónico Contacto',
       f.contacto_telefono AS 'Teléfono Contacto',
       d.direccion         AS N'Dirección',
       dd.nombre           AS 'Distrito',
       pp.nombre           AS 'Provincia',
       e.estado,
       f.created_at,
       f.updated_at,
       f.usuario,
       f.id,
       d.id                AS 'id_direccion',
       dd.id               AS 'id_distrito',
       pp.id               AS 'id_provincia'
FROM entidades e
         LEFT JOIN fabricantes f ON f.id = e.id
         LEFT JOIN direcciones d ON e.direccion = d.id
         LEFT JOIN distritos dd ON d.distrito = dd.id
         LEFT JOIN provincias pp ON dd.provincia = pp.id;
GO

CREATE VIEW view_pacientes_usuarios AS
SELECT pe.cedula                 AS N'Cédula Paciente',
       pe.pasaporte              AS 'Pasaporte Paciente',
       p.identificacion_temporal AS N'Identificación Temporal',
       pe.nombre                 AS 'Nombre',
       pe.apellido1              AS 'Primer Apellido',
       pe.apellido2              AS 'Segundo Apellido',
       pe.fecha_nacimiento       AS 'Fecha de Nacimiento Paciente',
       pe.fecha_nacimiento       AS 'Edad',
       pe.sexo                   AS 'Sexo',
       pe.telefono               AS N'Teléfono Paciente',
       pe.correo                 AS N'Correo Electrónico Paciente',
       u.usuario                 AS 'Usuario',
       pe.estado                 AS 'estado_paciente',
       p.created_at              AS 'created_at_paciente',
       p.updated_at              AS 'updated_at_paciente',
       p.id,
       u.created_at              AS 'created_at_usuario',
       u.updated_at              AS 'updated_at_usuario',
       u.last_used,
       u.id                      AS 'id_usuario'
FROM personas pe
         LEFT JOIN pacientes p ON p.id = pe.id
         LEFT JOIN usuarios u ON pe.usuario = u.id;
GO

CREATE VIEW view_enfermedades_sintomas AS
SELECT e.nombre                   AS 'Enfermedad',
       e.nivel_gravedad           AS 'Nivel de Gravedad',
       STRING_AGG(s.nombre, ', ') AS N'Síntomas',
       e.id,
       STRING_AGG(s.id, ', ')     AS 'ids_sintomas'
FROM enfermedades e
         LEFT JOIN enfermedades_sintomas es ON e.id = es.enfermedad
         LEFT JOIN sintomas s ON es.sintoma = s.id
GROUP BY e.nombre, e.nivel_gravedad, e.id;
GO

CREATE VIEW view_vacunas_enfermedades AS
SELECT v.nombre                           AS 'Nombre Vacuna',
       v.edad_minima_dias                 AS 'Edad mínima Recomendada en Meses',
       v.intervalo_dosis_1_2_dias            'Intervalo entre Dosis 1 y 2 Recomendado en Meses',
       v.dosis_maxima                     AS 'Dosis Máxima Recomendada',
       STRING_AGG(e.nombre, ', ')         AS 'Enfermedades Prevenidas',
       STRING_AGG(e.nivel_gravedad, ', ') AS 'Niveles de Gravedad Enfermedades',
       v.id,
       v.created_at,
       v.updated_at,
       e.id                               AS 'id_enfermedad'
FROM vacunas v
         LEFT JOIN vacunas_enfermedades ve ON v.id = ve.vacuna
         LEFT JOIN enfermedades e ON ve.enfermedad = e.id
GROUP BY v.nombre, v.edad_minima_dias, v.intervalo_dosis_1_2_dias, v.dosis_maxima, v.id, v.created_at, v.updated_at,
         e.id
GO

CREATE VIEW view_roles_permisos AS
SELECT r.nombre                   AS 'Nombre Rol',
       r.descripcion              AS N'Descripción Rol',
       STRING_AGG(p.nombre, ', ') AS 'Nombres Permisos',
       r.created_at,
       r.updated_at,
       r.id,
       STRING_AGG(p.id, ', ')     AS 'ids_permisos'
FROM roles r
         LEFT JOIN roles_permisos rp ON r.id = rp.rol
         LEFT JOIN permisos p ON rp.permiso = p.id
GROUP BY r.nombre, r.descripcion, r.id, r.created_at, r.updated_at;
GO

CREATE VIEW view_roles_usuarios AS
SELECT r.nombre                                    AS 'Nombre Rol',
       STRING_AGG(CAST(u.id AS VARCHAR(36)), ', ') AS 'ids_usuarios',
       STRING_AGG(ur.created_at, ', ')             AS 'Rol Otorgado desde',
       r.id
FROM roles r
         LEFT JOIN usuarios_roles ur ON r.id = ur.rol
         INNER JOIN usuarios u ON ur.usuario = u.id
GROUP BY r.nombre, r.id;
GO

CREATE VIEW view_direcciones_detalles AS
SELECT d.direccion AS 'Dirección',
       dd.nombre   AS 'Distrito',
       pp.nombre   AS 'Provincia',
       d.id,
       dd.id       AS 'id_distrito',
       pp.id       AS 'id_provincia'
FROM direcciones d
         LEFT JOIN distritos dd ON d.distrito = dd.id
         LEFT JOIN provincias pp ON dd.provincia = pp.id;
GO

CREATE VIEW view_vacunas_fabricantes AS
SELECT v.nombre                                    AS 'Nombre Vacuna',
       STRING_AGG(e.nombre, ', ')                  AS 'Fabricantes',
       v.id,
       STRING_AGG(CAST(f.id AS VARCHAR(36)), ', ') AS 'ids_fabricante'
FROM vacunas v
         INNER JOIN fabricantes_vacunas fv ON v.id = fv.vacuna
         LEFT JOIN fabricantes f ON fv.fabricante = f.id
         LEFT JOIN entidades e ON f.id = e.id
GROUP BY v.nombre, v.id
GO

CREATE VIEW view_pacientes_vacunas_enfermedades AS
SELECT v.nombre                   AS 'Nombre Vacuna',
       d.numero_dosis             AS N'Número de dosis',
       STRING_AGG(e.nombre, ', ') AS N'Enfermedades Prevenidas',
       v.edad_minima_dias         AS N'Edad Mínima Recomendada en Días',
       d.fecha_aplicacion         AS N'Fecha de Aplicación',
       v.intervalo_dosis_1_2_dias AS N'Intervalo Recomendado entre Dosis 1 y 2 en Días',
       DATEDIFF(DAY, d.fecha_aplicacion,
                (SELECT MAX(d2.fecha_aplicacion)
                 FROM dosis d2
                 WHERE d2.paciente = p.id
                   AND d2.vacuna = d.vacuna
                   AND d2.numero_dosis > d.numero_dosis))
                                  AS N'Intervalo Real en Días',
       ee.nombre                  AS 'Sede',
       ee.dependencia             AS 'Dependencia',
       p.id,
       v.id                       AS 'id_vacuna',
       ee.id                      AS 'id_sede',
       d.id                       AS 'id_dosis',
       STRING_AGG(e.id, ', ')     AS 'ids_enfermedades'
FROM pacientes p
         JOIN dosis d ON p.id = d.paciente
         JOIN vacunas v ON d.vacuna = v.id
         LEFT JOIN vacunas_enfermedades ve ON v.id = ve.vacuna
         LEFT JOIN enfermedades e ON ve.enfermedad = e.id
         LEFT JOIN sedes s ON d.sede = s.id
         LEFT JOIN entidades ee ON s.id = ee.id
GROUP BY p.id,
         v.nombre, v.edad_minima_dias, v.intervalo_dosis_1_2_dias,
         ee.nombre, ee.dependencia, ee.id,
         v.id,
         d.id, d.vacuna, d.fecha_aplicacion, d.numero_dosis;
GO

CREATE VIEW view_distribuciones_almacenes_sedes_vacunas_fabricantes AS
SELECT ae.nombre            AS N'Nombre Almacén',
       ae.dependencia       AS N'Dependencia Almacén',
       se.nombre            AS 'Nombre Sede',
       se.dependencia       AS 'Dependencia Sede',
       v.nombre             AS 'Nombre Vacuna',
       fe.nombre            AS 'Nombre Fabricante',
       d.cantidad           AS 'Cantidad',
       d.lote               AS 'Lote',
       d.fecha_distribucion AS 'Fecha de Distribución',
       d.created_at,
       d.updated_at,
       d.id,
       a.id                 AS 'id_almacen',
       s.id                 AS 'id_sede',
       v.id                 AS 'id_vacuna',
       f.id                 AS 'id_fabricante'
FROM distribuciones_vacunas d
         LEFT JOIN almacenes a ON d.almacen = a.id
         LEFT JOIN entidades ae ON a.id = ae.id
         LEFT JOIN sedes s ON d.sede = s.id
         LEFT JOIN entidades se ON s.id = se.id
         LEFT JOIN vacunas v ON d.vacuna = v.id
         LEFT JOIN fabricantes_vacunas fv ON v.id = fv.vacuna
         LEFT JOIN fabricantes f ON fv.fabricante = f.id
         LEFT JOIN entidades fe ON f.id = fe.id;
GO

CREATE VIEW view_almacenes_inventarios_vacunas_fabricantes AS
SELECT ae.nombre           AS N'Nombre Almacén',
       ae.dependencia      AS N'Dependencia Almacén',
       v.nombre            AS 'Nombre Vacuna',
       fe.nombre           AS 'Nombre Fabricante',
       ai.cantidad         AS 'Cantidad Disponible',
       ai.fecha_expiracion AS 'Fecha de Expiración',
       ai.lote             AS 'Lote',
       ai.fecha_recepcion  AS 'Fecha de Recepción',
       a.id,
       v.id                AS 'id_vacuna',
       f.id                AS 'id_fabricante'
FROM almacenes_inventarios ai
         LEFT JOIN almacenes a ON ai.almacen = a.id
         LEFT JOIN entidades ae ON a.id = ae.id
         LEFT JOIN vacunas v ON ai.vacuna = v.id
         LEFT JOIN fabricantes_vacunas fv ON v.id = fv.vacuna
         LEFT JOIN fabricantes f ON fv.fabricante = f.id
         LEFT JOIN entidades fe ON f.id = fe.id;
GO

CREATE VIEW view_sedes_inventarios_vacunas_fabricantes AS
SELECT se.nombre           AS 'Nombre Sede',
       se.dependencia      AS 'Dependencia Sede',
       v.nombre            AS 'Nombre Vacuna',
       fe.nombre           AS 'Nombre Fabricante',
       si.cantidad         AS 'Cantidad Disponible',
       si.fecha_expiracion AS N'Fecha de Expiración',
       si.lote             AS 'Lote',
       si.fecha_recepcion  AS N'Fecha de Recepción',
       s.id,
       v.id                AS 'id_vacuna',
       f.id                AS 'id_fabricante'
FROM sedes_inventarios si
         LEFT JOIN sedes s ON si.sede = s.id
         LEFT JOIN entidades se ON s.id = se.id
         LEFT JOIN vacunas v ON si.vacuna = v.id
         LEFT JOIN fabricantes_vacunas fv ON v.id = fv.vacuna
         LEFT JOIN fabricantes f ON fv.fabricante = f.id
         LEFT JOIN entidades fe ON f.id = fe.id;
GO

CREATE VIEW view_doctores_sedes AS
SELECT doc.idoneidad  AS 'Idoneidad',
       pe.nombre      AS 'Nombre Doctor',
       pe.apellido1   AS 'Primer Apellido',
       doc.categoria  AS 'Categoría del Doctor',
       se.nombre      AS 'Nombre Sede',
       se.dependencia AS 'Dependencia Sede',
       s.region       AS 'Región de la Sede',
       d.direccion    AS 'Dirección Sede',
       dd.nombre      AS 'Distrito Sede',
       pp.nombre      AS 'Provincia Sede'
FROM doctores doc
         LEFT JOIN personas pe ON doc.id = pe.id
         LEFT JOIN sedes s ON doc.sede = s.id
         LEFT JOIN entidades se ON s.id = se.id
         LEFT JOIN direcciones d ON pe.direccion = d.id
         LEFT JOIN distritos dd ON d.distrito = dd.id
         LEFT JOIN provincias pp ON dd.provincia = pp.id;
GO

PRINT (N'Creando triggers');
GO
-- Triggers
-- Trigger para asignar automáticamente la región a las sedes cuando coincide con la provincia y/o distrito
CREATE TRIGGER tr_sedes_insert_region
    ON sedes
    AFTER INSERT, UPDATE
    AS
BEGIN
    IF TRIGGER_NESTLEVEL() > 1
        RETURN
    BEGIN TRY
        UPDATE S
        SET region =
                CASE
                    WHEN P.id = 1 THEN 'Bocas del Toro'
                    WHEN P.id = 2 THEN N'Coclé'
                    WHEN P.id = 3 THEN N'Colón'
                    WHEN P.id = 4 THEN N'Chiriquí'
                    WHEN P.id = 5 THEN N'Darién y la comarca Embera Waunán y Wargandí'
                    WHEN P.id = 6 THEN 'Herrera'
                    WHEN P.id = 7 THEN 'Los Santos'
                    WHEN P.id = 8 AND D.distrito = 53 THEN 'San Miguelito'
                    WHEN P.id = 8 AND D.distrito <> 53 THEN N'Panamá Norte/Este/Metro'
                    WHEN P.id = 9 THEN 'Veraguas'
                    WHEN P.id = 10 THEN 'Kuna Yala'
                    WHEN P.id = 11 THEN N'Darién y la comarca Embera Waunán y Wargandí'
                    WHEN P.id = 12 THEN N'Ngabe Buglé'
                    WHEN P.id = 13 AND D.distrito <> 79 THEN N'Panamá Oeste'
                    WHEN P.id = 13 AND D.distrito = 79 THEN N'Arraiján'
                    WHEN P.id = 16 THEN N'Darién y la comarca Embera Waunán y Wargandí'
                    ELSE 'Por registrar'
                    END
        FROM sedes S
                 INNER JOIN inserted I ON S.id = I.id
                 INNER JOIN entidades PE ON S.id = PE.id
                 INNER JOIN direcciones D ON PE.direccion = D.id
                 INNER JOIN distritos DD ON D.distrito = DD.id
                 INNER JOIN provincias P ON DD.provincia = P.id
    END TRY
    BEGIN CATCH
        THROW;
    END CATCH
END;
GO

-- trigger para mantener registro de cambios
CREATE TRIGGER tr_usuarios_updated
    ON usuarios
    AFTER UPDATE
    AS
BEGIN
    IF TRIGGER_NESTLEVEL() > 1
        RETURN
    BEGIN TRY
        UPDATE usuarios
        SET updated_at = CURRENT_TIMESTAMP
        WHERE id = (SELECT id FROM inserted)
    END TRY
    BEGIN CATCH
        THROW;
    END CATCH
END
GO

CREATE TRIGGER tr_doctores_updated
    ON doctores
    AFTER UPDATE
    AS
BEGIN
    IF TRIGGER_NESTLEVEL() > 1
        RETURN
    BEGIN TRY
        UPDATE doctores
        SET updated_at = CURRENT_TIMESTAMP
        WHERE id = (SELECT id FROM inserted)
    END TRY
    BEGIN CATCH
        THROW;
    END CATCH
END
GO

CREATE TRIGGER tr_pacientes_updated
    ON pacientes
    AFTER UPDATE
    AS
BEGIN
    IF TRIGGER_NESTLEVEL() > 1
        RETURN
    BEGIN TRY
        UPDATE pacientes
        SET updated_at = CURRENT_TIMESTAMP
        WHERE id = (SELECT id FROM inserted)
    END TRY
    BEGIN CATCH
        THROW;
    END CATCH
END
GO

-- trigger que le da el formato correcto a las cédulas al insertar
CREATE TRIGGER tr_personas_format_insert
    ON personas
    INSTEAD OF INSERT
    AS
BEGIN
    IF TRIGGER_NESTLEVEL() > 1
        RETURN
    BEGIN TRY
        DECLARE @cedula VARCHAR(15);
        DECLARE @inicio VARCHAR(4);
        DECLARE @libro VARCHAR(4);
        DECLARE @tomo VARCHAR(6);
        DECLARE @fecha_nacimiento SMALLDATETIME;
        DECLARE @edad TINYINT;

        SELECT @cedula = cedula, @fecha_nacimiento = fecha_nacimiento
        FROM inserted

        IF @fecha_nacimiento IS NOT NULL
            BEGIN
                SET @edad = DATEDIFF(YEAR, @fecha_nacimiento, GETDATE())
                    - IIF(DATEADD(YEAR, DATEDIFF(YEAR, @fecha_nacimiento, GETDATE()), @fecha_nacimiento) > GETDATE(), 1,
                          0)
            END

        IF @cedula IS NOT NULL AND NOT (
            (@cedula LIKE 'PE-%' OR @cedula LIKE 'E-%' OR @cedula LIKE 'N-%' OR @cedula LIKE '[1-9]-%' OR
             @cedula LIKE '[10-13]-%'
                OR @cedula LIKE '[1-9]AV-%' OR @cedula LIKE '[10-13]AV-%' OR @cedula LIKE '[1-9]PI-%' OR
             @cedula LIKE '[10-13]PI-%')
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9]-%'
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
            BEGIN
                IF @cedula NOT LIKE 'PE-%'
                    AND @cedula NOT LIKE 'E-%'
                    AND @cedula NOT LIKE 'N-%'
                    AND @cedula NOT LIKE '[1-9]-%'
                    AND @cedula NOT LIKE '[10-13]-%'
                    AND @cedula NOT LIKE '[1-9]AV-%'
                    AND @cedula NOT LIKE '[10-13]AV-%'
                    AND @cedula NOT LIKE '[1-9]PI-%'
                    AND @cedula NOT LIKE '[10-13]PI-%'
                    BEGIN
                        print (@cedula)
                        RAISERROR (N'cédula no cumple el formato en el principio', 16, 1);
                    END

                IF @cedula NOT LIKE '%-%-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato', 16, 1);
                    END

                SET @inicio = SUBSTRING(@cedula, 1, CHARINDEX('-', @cedula) - 1);
                SET @libro = SUBSTRING(@cedula, CHARINDEX('-', @cedula) + 1,
                                       CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) - CHARINDEX('-', @cedula) -
                                       1);
                SET @tomo = SUBSTRING(@cedula, CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) + 1, LEN(@cedula));

                SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                SET @cedula = CONCAT(@inicio, '-', @libro, '-', @tomo);
            END

        INSERT personas (id, cedula, pasaporte, nombre, nombre2, apellido1, apellido2, fecha_nacimiento, edad, sexo,
                         telefono,
                         correo, estado, disabled, direccion, usuario)
        SELECT id,
               @cedula,
               pasaporte,
               nombre,
               nombre2,
               apellido1,
               apellido2,
               fecha_nacimiento,
               @edad,
               sexo,
               telefono,
               correo,
               estado,
               disabled,
               direccion,
               usuario
        FROM inserted
    END TRY
    BEGIN CATCH
        THROW;
    END CATCH
END
GO

CREATE TRIGGER tr_pacientes_format_insert
    ON pacientes
    INSTEAD OF INSERT
    AS
BEGIN
    IF TRIGGER_NESTLEVEL() > 1
        RETURN
    BEGIN TRY
        DECLARE @temporal VARCHAR(50);
        DECLARE @inicio VARCHAR(4);
        DECLARE @libro VARCHAR(4);
        DECLARE @tomo VARCHAR(6);

        SELECT @temporal = identificacion_temporal
        FROM inserted

        IF @temporal IS NOT NULL AND (@temporal LIKE 'RN-%' OR @temporal LIKE 'RN[1-15]-%')
            BEGIN
                DECLARE @cedula_madre VARCHAR(15);
                DECLARE @recien_nacido VARCHAR(5);
                -- Extraer la parte del recién nacido y la cédula de la madre
                SET @recien_nacido = SUBSTRING(@temporal, 1, CHARINDEX('-', @temporal) - 1);
                SET @cedula_madre = SUBSTRING(@temporal, CHARINDEX('-', @temporal) + 1, LEN(@temporal));

                -- Verificar si la cédula de la madre ya está en el formato correcto
                IF NOT ((@cedula_madre LIKE 'PE-%' OR @cedula_madre LIKE 'E-%' OR @cedula_madre LIKE 'N-%' OR
                         @cedula_madre LIKE '[1-9]-%'
                    OR @cedula_madre LIKE '[10-13]-%' OR @cedula_madre LIKE '[1-9]AV-%' OR
                         @cedula_madre LIKE '[10-13]AV-%' OR @cedula_madre LIKE '[1-9]PI-%'
                    OR @cedula_madre LIKE '[10-13]PI-%')
                    AND @cedula_madre LIKE '%-[0-9][0-9][0-9][0-9]-%'
                    AND @cedula_madre LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
                    BEGIN
                        IF @cedula_madre NOT LIKE 'PE-%'
                            AND @cedula_madre NOT LIKE 'E-%'
                            AND @cedula_madre NOT LIKE 'N-%'
                            AND @cedula_madre NOT LIKE '[1-9]-%'
                            AND @cedula_madre NOT LIKE '[10-13]-%'
                            AND @cedula_madre NOT LIKE '[1-9]AV-%'
                            AND @cedula_madre NOT LIKE '[10-13]AV-%'
                            AND @cedula_madre NOT LIKE '[1-9]PI-%'
                            AND @cedula_madre NOT LIKE '[10-13]PI-%'
                            BEGIN
                                RAISERROR (N'cédula de la madre del recién nacido no cumple el formato en el principio', 16, 1);
                            END

                        IF @cedula_madre NOT LIKE '%-%-%'
                            BEGIN
                                RAISERROR (N'cédula de la madre del recién nacido no cumple el formato', 16, 1);
                            END

                        SET @inicio = SUBSTRING(@cedula_madre, 1, CHARINDEX('-', @cedula_madre) - 1);
                        SET @libro = SUBSTRING(@cedula_madre, CHARINDEX('-', @cedula_madre) + 1,
                                               CHARINDEX('-', @cedula_madre, CHARINDEX('-', @cedula_madre) + 1) -
                                               CHARINDEX('-', @cedula_madre) - 1);
                        SET @tomo = SUBSTRING(@cedula_madre,
                                              CHARINDEX('-', @cedula_madre, CHARINDEX('-', @cedula_madre) + 1) + 1,
                                              LEN(@cedula_madre));

                        SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                        SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                        SET @cedula_madre = CONCAT(@inicio, @libro, @tomo);
                    END
                SET @temporal = CONCAT(@recien_nacido, '-', @cedula_madre);
            END

        INSERT pacientes (id, identificacion_temporal, created_at, updated_at)
        SELECT id, @temporal, created_at, updated_at
        FROM inserted
    END TRY
    BEGIN CATCH
        THROW;
    END CATCH
END
GO

-- trigger para validar las distribuciones y de forma automática cuando se inserta en los inventarios, no es posible modificarlo de forma automática
CREATE TRIGGER tr_distribuciones_validate_insert
    ON distribuciones_vacunas
    INSTEAD OF INSERT
    AS
BEGIN
    IF TRIGGER_NESTLEVEL() > 1
        RETURN
    BEGIN TRY
        DECLARE @id_almacen UNIQUEIDENTIFIER, @id_vacuna UNIQUEIDENTIFIER, @cantidad INT, @lote NVARCHAR(10), @id_sede UNIQUEIDENTIFIER, @fecha_lote DATETIME;

        SELECT @id_almacen = almacen, @id_vacuna = vacuna, @cantidad = cantidad, @lote = lote, @id_sede = sede
        FROM inserted;

        -- Verificar la cantidad disponible en almacenes_inventarios
        IF EXISTS (SELECT 1
                   FROM almacenes_inventarios
                   WHERE almacen = @id_almacen
                     AND vacuna = @id_vacuna
                     AND lote LIKE @lote
                     AND cantidad >= @cantidad)
            BEGIN
                -- Actualizar inventario del almacén
                UPDATE almacenes_inventarios
                SET cantidad = cantidad - @cantidad
                WHERE almacen = @id_almacen
                  AND vacuna = @id_vacuna
                  AND lote LIKE @lote;

                SELECT @fecha_lote = fecha_expiracion
                FROM almacenes_inventarios
                WHERE almacen = @id_almacen
                  AND vacuna = @id_vacuna
                  AND lote LIKE @lote

                IF @fecha_lote < GETDATE()
                    BEGIN
                        RAISERROR (N'No se puede distribuir un lote con fecha menor al día de hoy. Revisar inventario Almacén', 16, 1);
                    END
            END
        ELSE
            BEGIN
                RAISERROR (N'No hay suficiente cantidad o no existe inventario de esa vacuna y lote en el almacén', 16, 1);
            END

        -- Si existe la vacuna en la sede, se actualiza
        IF EXISTS (SELECT 1
                   FROM sedes_inventarios
                   WHERE sede = @id_sede
                     AND vacuna = @id_vacuna
                     AND lote LIKE @lote)
            BEGIN
                UPDATE sedes_inventarios
                SET cantidad = cantidad + @cantidad
                WHERE sede = @id_sede
                  AND vacuna = @id_vacuna
                  AND lote LIKE @lote;
            END
        ELSE
            BEGIN
                -- Si no existe, se inserta un nuevo registro
                INSERT INTO sedes_inventarios (sede, vacuna, cantidad, fecha_expiracion, lote)
                VALUES (@id_sede, @id_vacuna, @cantidad, @fecha_lote, @lote);
            END

        -- Insertar la distribución
        INSERT INTO distribuciones_vacunas (id, almacen, sede, vacuna, cantidad, lote,
                                            fecha_distribucion)
        SELECT id, almacen, sede, vacuna, cantidad, lote, fecha_distribucion
        FROM inserted;
    END TRY
    BEGIN CATCH
        THROW;
    END CATCH
END;
GO

-- trigger que resta del inventario de la sede si existe para esa vacuna de dosis insertada
CREATE TRIGGER tr_dosis_validate_update
    ON dosis
    INSTEAD OF INSERT
    AS
BEGIN
    IF TRIGGER_NESTLEVEL() > 1
        RETURN
    BEGIN TRY
        DECLARE @numero_dosis CHAR(2), @id_paciente UNIQUEIDENTIFIER, @id_vacuna UNIQUEIDENTIFIER, @id_sede UNIQUEIDENTIFIER, @lote NVARCHAR(10), @cantidad_disponible INT, @fecha_lote DATETIME;

        SELECT @numero_dosis = i.numero_dosis,
               @id_vacuna = i.vacuna,
               @id_sede = i.sede,
               @lote = i.lote,
               @id_paciente = paciente
        FROM inserted i

        SET @numero_dosis = RTRIM(@numero_dosis);
        SET @numero_dosis = UPPER(@numero_dosis);

        -- Verificar si hay suficiente inventario y fecha de vencimiento del mismo
        IF EXISTS (SELECT 1
                   FROM sedes_inventarios
                   WHERE sede = @id_sede
                     AND vacuna = @id_vacuna
                     AND lote LIKE @lote)
            BEGIN
                SELECT @cantidad_disponible = Cantidad, @fecha_lote = fecha_expiracion
                FROM sedes_inventarios
                WHERE sede = @id_sede
                  AND vacuna = @id_vacuna
                  AND lote LIKE @lote;

                IF @cantidad_disponible < 1
                    BEGIN
                        RAISERROR ('Cantidad insuficiente de dosis de la vacuna en el inventario de la sede.', 16, 1);
                    END

                IF @fecha_lote <= CURRENT_TIMESTAMP
                    BEGIN
                        RAISERROR (N'La fecha de vencimiento del lote de esta vacuna es el día de hoy o anterior.', 16, 1);
                    END

                UPDATE sedes_inventarios
                SET cantidad = cantidad - 1
                WHERE sede = @id_sede
                  AND vacuna = @id_vacuna
                  AND lote LIKE @lote;
            END

        -- Validar dosis previas del paciente en secuencia esperada
        IF @numero_dosis = 'P' AND EXISTS (SELECT 1
                                           FROM dosis
                                           WHERE paciente = @id_paciente
                                             AND vacuna = @id_vacuna)
            BEGIN
                RAISERROR ('La dosis P previa solo puede ser aplicada antes de la primera dosis de la misma vacuna.', 16, 1);
            END

        IF @numero_dosis = '2' AND NOT EXISTS (SELECT 1
                                               FROM dosis
                                               WHERE paciente = @id_paciente
                                                 AND vacuna = @id_vacuna
                                                 AND numero_dosis = '1')
            BEGIN
                RAISERROR ('La dosis 1 de la misma vacuna debe ser aplicada antes de la dosis 2.', 16, 1);
            END

        IF @numero_dosis = '3' AND NOT EXISTS (SELECT 1
                                               FROM dosis
                                               WHERE paciente = @id_paciente
                                                 AND vacuna = @id_vacuna
                                                 AND numero_dosis = '2')
            BEGIN
                RAISERROR ('La dosis 2 de la misma vacuna debe ser aplicada antes de la dosis 3.', 16, 1);
            END

        -- Validar refuerzos (R1, R2) para la misma vacuna
        IF @numero_dosis = 'R1' AND NOT EXISTS (SELECT 1
                                                FROM dosis
                                                WHERE paciente = @id_paciente
                                                  AND vacuna = @id_vacuna
                                                  AND numero_dosis = '1')
            BEGIN
                RAISERROR ('La dosis 1 de la misma vacuna debe ser aplicada antes de la dosis R1.', 16, 1);
            END

        IF @numero_dosis = 'R2' AND NOT EXISTS (SELECT 1
                                                FROM dosis
                                                WHERE paciente = @id_paciente
                                                  AND vacuna = @id_vacuna
                                                  AND numero_dosis IN ('R1', '1'))
            BEGIN
                RAISERROR ('La dosis R1 o 1 de la misma vacuna debe ser aplicada antes de la dosis R2.', 16, 1);
            END

        INSERT INTO dosis (id, paciente, fecha_aplicacion, numero_dosis, vacuna, sede, doctor, lote, created_at)
        SELECT id,
               paciente,
               fecha_aplicacion,
               @numero_dosis,
               vacuna,
               sede,
               doctor,
               lote,
               created_at
        FROM inserted
    END TRY
    BEGIN CATCH
        THROW;
    END CATCH
END;
GO

PRINT (N'Creando procedimientos almacenados');
GO
-- Procedimientos
-- Algunos procedimientos dan opcional el nombre tabla, los sistemas deben procurar usar id y no el nombre o
-- hacer su adaptación propia de la operación, todos los procedimientos tienen una variable de salida int con el recuento total de inserted y updated.
-- En caso de dar el nombre y id se da prioridad al ID, los nombres se buscan si solo proporcionó el nombre.
CREATE PROCEDURE sp_vacunas_update_paciente_edad(
    @result INT OUTPUT
)
AS
BEGIN
    SET NOCOUNT ON;
    SET @result = 0;
    -- Solo actualiza a los pacientes que cumplen años hoy y cuya edad no esté correctamente calculada.
    -- Procedimiento para el job diario, no es necesario usarlo los usuarios
    UPDATE personas
    SET edad =
            IIF(DATEADD(YEAR, DATEDIFF(YEAR, fecha_nacimiento, GETDATE()), fecha_nacimiento) > GETDATE(),
                DATEDIFF(YEAR, fecha_nacimiento, GETDATE()) - 1,
                DATEDIFF(YEAR, fecha_nacimiento, GETDATE()))
    WHERE DATEPART(MONTH, fecha_nacimiento) = DATEPART(MONTH, GETDATE())
      AND DATEPART(DAY, fecha_nacimiento) = DATEPART(DAY, GETDATE())
      AND edad <>
          IIF(DATEADD(YEAR, DATEDIFF(YEAR, fecha_nacimiento, GETDATE()), fecha_nacimiento) > GETDATE(),
              DATEDIFF(YEAR, fecha_nacimiento, GETDATE()) - 1,
              DATEDIFF(YEAR, fecha_nacimiento, GETDATE()));
    SET @result = @result + @@ROWCOUNT;
END;
GO

CREATE PROCEDURE sp_vacunas_insert_sede(
    @nombre NVARCHAR(100),
    @dependencia NVARCHAR(13),
    @correo VARCHAR(254) = NULL,
    @telefono VARCHAR(15) = NULL,
    @estado NVARCHAR(50) = NULL,
    @direccion NVARCHAR(150) = NULL,
    @distrito NVARCHAR(100) = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        DECLARE @id_direccion UNIQUEIDENTIFIER;
        DECLARE @id_entidad UNIQUEIDENTIFIER;

        -- Validar que dirección y distrito estén ambos campos o ninguno
        IF (@direccion IS NOT NULL AND @distrito IS NULL) OR
           (@direccion IS NULL AND @distrito IS NOT NULL)
            BEGIN
                RAISERROR (N'Debe especificar ambos campos: dirección y distrito o ninguno.', 16, 1);
            END

        IF @estado IS NULL
            BEGIN
                SET @estado = 'NO_VALIDADO';
            END

        BEGIN TRANSACTION
            -- Insertar la dirección si no existe
            IF @direccion IS NOT NULL AND @distrito IS NOT NULL
                BEGIN
                    -- Verificar si la dirección ya existe
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = @direccion;

                    IF @id_direccion IS NULL
                        BEGIN
                            SET @id_direccion = NEWID();
                            -- Insertar nueva dirección
                            INSERT INTO direcciones (id, direccion, distrito)
                            VALUES (@id_direccion, @direccion,
                                    (SELECT id FROM distritos WHERE nombre = @distrito))
                            SET @result = @result + @@ROWCOUNT;
                        END
                END
            ELSE
                BEGIN
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = 'Por registrar'
                      AND distrito = 0
                END

            SET @id_entidad = NEWID();

            -- Insertar la entidad para la sede
            INSERT INTO entidades (id, nombre, dependencia, correo, telefono, direccion, estado)
            VALUES (@id_entidad, @nombre, @dependencia, @correo, @telefono, @id_direccion, @estado);
            SET @result = @result + @@ROWCOUNT;

            -- Insertar la sede
            INSERT INTO sedes (id)
            VALUES (@id_entidad)

            SET @result = @result + @@ROWCOUNT;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE sp_vacunas_insert_almacen(
    @nombre NVARCHAR(100),
    @dependencia NVARCHAR(13),
    @correo VARCHAR(254) = NULL,
    @telefono VARCHAR(15) = NULL,
    @estado NVARCHAR(50) = NULL,
    @direccion NVARCHAR(150) = NULL,
    @distrito NVARCHAR(100) = NULL,
    @encargado_nombre NVARCHAR(50) = NULL,
    @encargado_cedula VARCHAR(15) = NULL,
    @encargado_pasaporte VARCHAR(20) = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        DECLARE @id_direccion UNIQUEIDENTIFIER;
        DECLARE @id_entidad UNIQUEIDENTIFIER;
        DECLARE @id_encargado UNIQUEIDENTIFIER = NULL;

        -- Validar que dirección y distrito estén ambos campos o ninguno
        IF (@direccion IS NOT NULL AND @distrito IS NULL) OR
           (@direccion IS NULL AND @distrito IS NOT NULL)
            BEGIN
                RAISERROR (N'Debe especificar ambos campos: dirección y distrito o ninguno.', 16, 1);
            END

        IF @estado IS NULL
            BEGIN
                SET @estado = 'NO_VALIDADO'
            END

        BEGIN TRANSACTION

            -- Insertar la dirección si no existe
            IF @direccion IS NOT NULL AND @distrito IS NOT NULL
                BEGIN
                    -- Verificar si la dirección ya existe
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = @direccion;

                    IF @id_direccion IS NULL
                        BEGIN
                            SET @id_direccion = NEWID();
                            -- Insertar nueva dirección
                            INSERT INTO direcciones (id, direccion, distrito)
                            VALUES (@id_direccion, @direccion,
                                    (SELECT id FROM distritos WHERE nombre = @distrito))
                            SET @result = @result + @@ROWCOUNT;
                        END
                END
            ELSE
                BEGIN
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = 'Por registrar'
                      AND distrito = 0
                END

            SET @id_entidad = NEWID();
            -- Insertar entidad para el Almacén
            INSERT INTO entidades (id, nombre, dependencia, correo, telefono, direccion, estado)
            VALUES (@id_entidad, @nombre, @dependencia, @correo, @telefono, @id_direccion, @estado)
            SET @result = @result + @@ROWCOUNT;

            IF @encargado_nombre IS NOT NULL AND (@encargado_cedula IS NOT NULL OR @encargado_pasaporte IS NOT NULL)
                BEGIN
                    SET @id_encargado = NEWID();
                    INSERT INTO personas (id, nombre, cedula, pasaporte, direccion)
                    VALUES (@id_encargado, @encargado_nombre, @encargado_cedula, @encargado_pasaporte,
                            (SELECT id FROM direcciones WHERE direccion = 'Por registrar' AND distrito = 0))
                    SET @result = @result + @@ROWCOUNT;
                END
            ELSE
                BEGIN
                    RAISERROR (N'los datos del encargado están incompletos, debe proveer una identificación válida', 16, 1);
                END
            -- Insertar el Almacén
            INSERT INTO almacenes (id, encargado)
            VALUES (@id_entidad, @id_encargado);

            SET @result = @result + @@ROWCOUNT;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE sp_vacunas_insert_fabricante(
    @licencia NVARCHAR(50),
    @nombre NVARCHAR(100),
    @correo VARCHAR(254) = NULL,
    @telefono VARCHAR(15) = NULL,
    @estado NVARCHAR(50) = NULL,
    @direccion NVARCHAR(150) = NULL,
    @distrito NVARCHAR(100) = NULL,
    @contacto_nombre NVARCHAR(100) = NULL,
    @contacto_correo VARCHAR(254) = NULL,
    @contacto_telefono VARCHAR(15) = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        DECLARE @id_direccion UNIQUEIDENTIFIER;
        DECLARE @id_entidad UNIQUEIDENTIFIER;

        -- Validar que dirección y distrito estén ambos campos o ninguno
        IF (@direccion IS NOT NULL AND @distrito IS NULL) OR
           (@direccion IS NULL AND @distrito IS NOT NULL)
            BEGIN
                RAISERROR (N'Debe especificar ambos campos: dirección y distrito o ninguno.', 16, 1);
            END

        IF @estado IS NULL
            BEGIN
                SET @estado = 'NO_VALIDADO'
            END

        BEGIN TRANSACTION

            -- Insertar la dirección si no existe
            IF @direccion IS NOT NULL AND @distrito IS NOT NULL
                BEGIN
                    -- Verificar si la dirección ya existe
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = @direccion;

                    IF @id_direccion IS NULL
                        BEGIN
                            SET @id_direccion = NEWID();
                            -- Insertar nueva dirección
                            INSERT INTO direcciones (id, direccion, distrito)
                            VALUES (@id_direccion, @direccion,
                                    (SELECT id FROM distritos WHERE nombre = @distrito))
                            SET @result = @result + @@ROWCOUNT;
                        END
                END
            ELSE
                BEGIN
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = 'Por registrar'
                      AND distrito = 0
                END

            SET @id_entidad = NEWID();
            -- Insertar entidad para el fabricante
            INSERT INTO entidades (id, nombre, correo, telefono, direccion, estado)
            VALUES (@id_entidad, @nombre, @correo, @telefono, @id_direccion, @estado)
            SET @result = @result + @@ROWCOUNT;

            -- Insertar el fabricante
            INSERT INTO fabricantes (id, licencia, contacto_nombre, contacto_correo, contacto_telefono)
            VALUES (@id_entidad, @licencia, @contacto_nombre, @contacto_correo, @contacto_telefono);

            SET @result = @result + @@ROWCOUNT;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE sp_vacunas_gestionar_paciente(
    @cedula VARCHAR(15) = NULL,
    @pasaporte VARCHAR(20) = NULL,
    @id_temporal VARCHAR(50) = NULL,
    @nombre NVARCHAR(100),
    @apellido1 NVARCHAR(100),
    @apellido2 NVARCHAR(100) = NULL,
    @fecha_nacimiento SMALLDATETIME,
    @sexo CHAR(1),
    @telefono VARCHAR(15) = NULL,
    @correo VARCHAR(254) = NULL,
    @estado NVARCHAR(50) = NULL,
    @direccion_residencial NVARCHAR(150) = NULL,
    @distrito_reside NVARCHAR(100) = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        DECLARE @id_paciente UNIQUEIDENTIFIER;
        DECLARE @id_direccion UNIQUEIDENTIFIER;
        DECLARE @inicio VARCHAR(4);
        DECLARE @libro VARCHAR(4);
        DECLARE @tomo VARCHAR(6);
        -- Validar que dirección y distrito estén ambos campos o ninguno
        IF (@direccion_residencial IS NOT NULL AND @distrito_reside IS NULL) OR
           (@direccion_residencial IS NULL AND @distrito_reside IS NOT NULL)
            BEGIN
                RAISERROR (N'Debe especificar ambos campos dirección y distrito o ninguno.', 16, 1);
            END

        IF (@cedula IS NULL AND @pasaporte IS NULL AND @id_temporal IS NULL)
            BEGIN
                RAISERROR (N'Debe especificar una identificación como cédula o pasaporte o temporal', 16, 1);
            END

        IF @estado IS NULL
            SET @estado = 'NO_VALIDADO'

        -- Se hace el formato correcto de las cédulas si ya existe y es necesario para actualizar
        IF @cedula IS NOT NULL AND NOT (
            (@cedula LIKE 'PE-%' OR @cedula LIKE 'E-%' OR @cedula LIKE 'N-%' OR @cedula LIKE '[1-9]-%' OR
             @cedula LIKE '[10-13]-%'
                OR @cedula LIKE '[1-9]AV-%' OR @cedula LIKE '[10-13]AV-%' OR @cedula LIKE '[1-9]PI-%' OR
             @cedula LIKE '[10-13]PI-%')
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9]-%'
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
            BEGIN
                IF @cedula NOT LIKE 'PE-%'
                    AND @cedula NOT LIKE 'E-%'
                    AND @cedula NOT LIKE 'N-%'
                    AND @cedula NOT LIKE '[1-9]-%'
                    AND @cedula NOT LIKE '[10-13]-%'
                    AND @cedula NOT LIKE '[1-9]AV-%'
                    AND @cedula NOT LIKE '[10-13]AV-%'
                    AND @cedula NOT LIKE '[1-9]PI-%'
                    AND @cedula NOT LIKE '[10-13]PI-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato en el principio', 16, 1);
                    END

                IF @cedula NOT LIKE '%-%-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato', 16, 1);
                    END

                SET @inicio = SUBSTRING(@cedula, 1, CHARINDEX('-', @cedula) - 1);
                SET @libro = SUBSTRING(@cedula, CHARINDEX('-', @cedula) + 1,
                                       CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) - CHARINDEX('-', @cedula) -
                                       1);
                SET @tomo = SUBSTRING(@cedula, CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) + 1, LEN(@cedula));

                SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                SET @cedula = CONCAT(@inicio, '-', @libro, '-', @tomo);
            END

        IF @id_temporal IS NOT NULL AND (@id_temporal LIKE 'RN-%' OR @id_temporal LIKE 'RN[1-15]-%')
            BEGIN
                DECLARE @cedula_madre VARCHAR(15);
                DECLARE @recien_nacido VARCHAR(5);
                -- Extraer la parte del recién nacido y la cédula de la madre
                SET @recien_nacido = SUBSTRING(@id_temporal, 1, CHARINDEX('-', @id_temporal) - 1);
                SET @cedula_madre = SUBSTRING(@id_temporal, CHARINDEX('-', @id_temporal) + 1, LEN(@id_temporal));

                -- Verificar si la cédula de la madre ya está en el formato correcto
                IF NOT ((@cedula_madre LIKE 'PE-%' OR @cedula_madre LIKE 'E-%' OR @cedula_madre LIKE 'N-%' OR
                         @cedula_madre LIKE '[1-9]-%'
                    OR @cedula_madre LIKE '[10-13]-%' OR @cedula_madre LIKE '[1-9]AV-%' OR
                         @cedula_madre LIKE '[10-13]AV-%' OR @cedula_madre LIKE '[1-9]PI-%'
                    OR @cedula_madre LIKE '[10-13]PI-%')
                    AND @cedula_madre LIKE '%-[0-9][0-9][0-9][0-9]-%'
                    AND @cedula_madre LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
                    BEGIN
                        IF @cedula_madre NOT LIKE 'PE-%'
                            AND @cedula_madre NOT LIKE 'E-%'
                            AND @cedula_madre NOT LIKE 'N-%'
                            AND @cedula_madre NOT LIKE '[1-9]-%'
                            AND @cedula_madre NOT LIKE '[10-13]-%'
                            AND @cedula_madre NOT LIKE '[1-9]AV-%'
                            AND @cedula_madre NOT LIKE '[10-13]AV-%'
                            AND @cedula_madre NOT LIKE '[1-9]PI-%'
                            AND @cedula_madre NOT LIKE '[10-13]PI-%'
                            BEGIN
                                RAISERROR (N'cédula de la madre del recién nacido no cumple el formato en el principio', 16, 1);
                            END

                        IF @cedula_madre NOT LIKE '%-%-%'
                            BEGIN
                                RAISERROR (N'cédula de la madre del recién nacido no cumple el formato', 16, 1);
                            END

                        SET @inicio = SUBSTRING(@cedula_madre, 1, CHARINDEX('-', @cedula_madre) - 1);
                        SET @libro = SUBSTRING(@cedula_madre, CHARINDEX('-', @cedula_madre) + 1,
                                               CHARINDEX('-', @cedula_madre, CHARINDEX('-', @cedula_madre) + 1) -
                                               CHARINDEX('-', @cedula_madre) - 1);
                        SET @tomo = SUBSTRING(@cedula_madre,
                                              CHARINDEX('-', @cedula_madre, CHARINDEX('-', @cedula_madre) + 1) + 1,
                                              LEN(@cedula_madre));

                        SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                        SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                        SET @cedula_madre = CONCAT(@inicio, @libro, @tomo);
                    END
                SET @id_temporal = CONCAT(@recien_nacido, '-', @cedula_madre);
            END

        BEGIN TRANSACTION
            -- Insertar la dirección si no existe
            IF @direccion_residencial IS NOT NULL AND @distrito_reside IS NOT NULL
                BEGIN
                    -- Verificar si la dirección ya existe
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = @direccion_residencial;

                    IF @id_direccion IS NULL
                        BEGIN
                            SET @id_direccion = NEWID();
                            -- Insertar nueva dirección
                            INSERT INTO direcciones (id, direccion, distrito)
                            VALUES (@id_direccion, @direccion_residencial, (SELECT id
                                                                            FROM distritos
                                                                            WHERE nombre = @distrito_reside));
                            SET @result = @result + @@ROWCOUNT;
                        END
                END
            ELSE
                BEGIN
                    -- Obtener el uuid de la dirección por defecto si es null
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = 'Por registrar'
                      AND distrito = 0
                END

            -- Verificar si el paciente ya existe
            IF EXISTS (SELECT 1
                       FROM personas
                                LEFT JOIN pacientes ON personas.id = pacientes.id
                       WHERE (@cedula IS NULL OR cedula = @cedula)
                         AND (@pasaporte IS NULL OR pasaporte = @pasaporte)
                         AND (@id_temporal IS NULL OR identificacion_temporal = @id_temporal))
                BEGIN
                    SELECT @id_paciente = pacientes.id
                    FROM personas
                             LEFT JOIN pacientes ON personas.id = pacientes.id
                    WHERE (@cedula IS NULL OR cedula = @cedula)
                      AND (@pasaporte IS NULL OR pasaporte = @pasaporte)
                      AND (@id_temporal IS NULL OR identificacion_temporal = @id_temporal)

                    -- Actualizar el paciente si ya existe y los datos son diferentes, exceptuando el estado y usuario
                    UPDATE personas
                    SET cedula           = COALESCE(@cedula, cedula),
                        pasaporte        = COALESCE(@pasaporte, pasaporte),
                        nombre           = COALESCE(@nombre, nombre),
                        apellido1        = COALESCE(@apellido1, apellido1),
                        apellido2        = COALESCE(@apellido2, apellido2),
                        fecha_nacimiento = COALESCE(@fecha_nacimiento, fecha_nacimiento),
                        sexo             = COALESCE(@sexo, sexo),
                        telefono         = COALESCE(@telefono, telefono),
                        correo           = COALESCE(@correo, correo),
                        direccion        = COALESCE(@id_direccion, direccion)
                    WHERE id = @id_paciente
                      AND (ISNULL(cedula, '') != ISNULL(@cedula, '') OR
                           ISNULL(pasaporte, '') != ISNULL(@pasaporte, '') OR
                           ISNULL(nombre, '') != ISNULL(@nombre, '') OR
                           ISNULL(apellido1, '') != ISNULL(@apellido1, '') OR
                           ISNULL(apellido2, '') != ISNULL(@apellido2, '') OR
                           ISNULL(fecha_nacimiento, '') != ISNULL(@fecha_nacimiento, '') OR
                           ISNULL(sexo, '') != ISNULL(@sexo, '') OR
                           ISNULL(telefono, '') != ISNULL(@telefono, '') OR
                           ISNULL(correo, '') != ISNULL(@correo, '') OR
                           ISNULL(direccion, '') != ISNULL(@id_direccion, ''));

                    SET @result = @result + @@ROWCOUNT;

                    UPDATE pacientes
                    SET identificacion_temporal = COALESCE(@id_temporal, identificacion_temporal)
                    WHERE id = @id_paciente
                      AND ISNULL(identificacion_temporal, '') != ISNULL(@id_temporal, '')

                    SET @result = @result + @@ROWCOUNT;
                END
            ELSE
                BEGIN
                    -- Insertar a la persona para el paciente si no existe
                    SET @id_paciente = NEWID();
                    INSERT INTO personas (id, cedula, pasaporte, nombre, apellido1, apellido2,
                                          fecha_nacimiento, sexo, telefono, correo, estado, direccion)
                    VALUES (@id_paciente, @cedula, @pasaporte, @nombre, @apellido1, @apellido2, @fecha_nacimiento,
                            @sexo, @telefono, @correo, @estado, @id_direccion);

                    SET @result = @result + @@ROWCOUNT;

                    -- Insertar el paciente
                    INSERT INTO pacientes (id, identificacion_temporal)
                    VALUES (@id_paciente, @id_temporal)
                    SET @result = @result + @@ROWCOUNT;
                END
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE sp_vacunas_gestionar_persona(
    @cedula VARCHAR(15) = NULL,
    @pasaporte VARCHAR(20) = NULL,
    @nombre NVARCHAR(100),
    @nombre2 NVARCHAR(100) = NULL,
    @apellido1 NVARCHAR(100) = NULL,
    @apellido2 NVARCHAR(100) = NULL,
    @fecha_nacimiento SMALLDATETIME,
    @sexo CHAR(1),
    @telefono VARCHAR(15) = NULL,
    @correo VARCHAR(254) = NULL,
    @estado NVARCHAR(50) = NULL,
    @direccion_residencial NVARCHAR(150) = NULL,
    @distrito_reside NVARCHAR(100) = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        DECLARE @id_persona UNIQUEIDENTIFIER;
        DECLARE @id_direccion UNIQUEIDENTIFIER;
        DECLARE @inicio VARCHAR(4);
        DECLARE @libro VARCHAR(4);
        DECLARE @tomo VARCHAR(6);
        -- Validar que dirección y distrito estén ambos campos o ninguno
        IF (@direccion_residencial IS NOT NULL AND @distrito_reside IS NULL) OR
           (@direccion_residencial IS NULL AND @distrito_reside IS NOT NULL)
            BEGIN
                RAISERROR (N'Debe especificar ambos campos dirección y distrito o ninguno.', 16, 1);
            END

        IF (@cedula IS NULL AND @pasaporte IS NULL)
            BEGIN
                RAISERROR (N'Debe especificar una identificación como cédula o pasaporte', 16, 1);
            END

        IF @estado IS NULL
            SET @estado = 'NO_VALIDADO'

        -- Se hace el formato correcto de las cédulas si ya existe y es necesario para actualizar
        IF @cedula IS NOT NULL AND NOT (
            (@cedula LIKE 'PE-%' OR @cedula LIKE 'E-%' OR @cedula LIKE 'N-%' OR @cedula LIKE '[1-9]-%' OR
             @cedula LIKE '[10-13]-%'
                OR @cedula LIKE '[1-9]AV-%' OR @cedula LIKE '[10-13]AV-%' OR @cedula LIKE '[1-9]PI-%' OR
             @cedula LIKE '[10-13]PI-%')
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9]-%'
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
            BEGIN
                IF @cedula NOT LIKE 'PE-%'
                    AND @cedula NOT LIKE 'E-%'
                    AND @cedula NOT LIKE 'N-%'
                    AND @cedula NOT LIKE '[1-9]-%'
                    AND @cedula NOT LIKE '[10-13]-%'
                    AND @cedula NOT LIKE '[1-9]AV-%'
                    AND @cedula NOT LIKE '[10-13]AV-%'
                    AND @cedula NOT LIKE '[1-9]PI-%'
                    AND @cedula NOT LIKE '[10-13]PI-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato en el principio', 16, 1);
                    END

                IF @cedula NOT LIKE '%-%-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato', 16, 1);
                    END

                SET @inicio = SUBSTRING(@cedula, 1, CHARINDEX('-', @cedula) - 1);
                SET @libro = SUBSTRING(@cedula, CHARINDEX('-', @cedula) + 1,
                                       CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) - CHARINDEX('-', @cedula) -
                                       1);
                SET @tomo = SUBSTRING(@cedula, CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) + 1, LEN(@cedula));

                SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                SET @cedula = CONCAT(@inicio, '-', @libro, '-', @tomo);
            END

        BEGIN TRANSACTION
            -- Insertar la dirección si no existe
            IF @direccion_residencial IS NOT NULL AND @distrito_reside IS NOT NULL
                BEGIN
                    -- Verificar si la dirección ya existe
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = @direccion_residencial;

                    IF @id_direccion IS NULL
                        BEGIN
                            SET @id_direccion = NEWID();
                            -- Insertar nueva dirección
                            INSERT INTO direcciones (id, direccion, distrito)
                            VALUES (@id_direccion, @direccion_residencial,
                                    (SELECT id FROM distritos WHERE nombre = @distrito_reside));
                            SET @result = @result + @@ROWCOUNT;
                        END
                END
            ELSE
                BEGIN
                    -- Obtener el uuid de la dirección por defecto si es null
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = 'Por registrar'
                      AND distrito = 0
                END

            -- Verificar si la persona ya existe
            IF EXISTS (SELECT 1
                       FROM personas
                       WHERE (@cedula IS NULL OR cedula = @cedula)
                         AND (@pasaporte IS NULL OR pasaporte = @pasaporte))
                BEGIN
                    SELECT @id_persona = personas.id
                    FROM personas
                    WHERE (@cedula IS NULL OR cedula = @cedula)
                      AND (@pasaporte IS NULL OR pasaporte = @pasaporte)

                    -- Actualizar persona si ya existe y los datos son diferentes, exceptuando el estado y usuario
                    UPDATE personas
                    SET cedula           = COALESCE(@cedula, cedula),
                        pasaporte        = COALESCE(@pasaporte, pasaporte),
                        nombre           = COALESCE(@nombre, nombre),
                        nombre2          = COALESCE(@nombre2, nombre2),
                        apellido1        = COALESCE(@apellido1, apellido1),
                        apellido2        = COALESCE(@apellido2, apellido2),
                        fecha_nacimiento = COALESCE(@fecha_nacimiento, fecha_nacimiento),
                        sexo             = COALESCE(@sexo, sexo),
                        telefono         = COALESCE(@telefono, telefono),
                        correo           = COALESCE(@correo, correo),
                        direccion        = COALESCE(@id_direccion, direccion)
                    WHERE id = @id_persona
                      AND (ISNULL(cedula, '') != ISNULL(@cedula, '') OR
                           ISNULL(pasaporte, '') != ISNULL(@pasaporte, '') OR
                           ISNULL(nombre, '') != ISNULL(@nombre, '') OR
                           ISNULL(nombre2, '') != ISNULL(@nombre2, '') OR
                           ISNULL(apellido1, '') != ISNULL(@apellido1, '') OR
                           ISNULL(apellido2, '') != ISNULL(@apellido2, '') OR
                           ISNULL(fecha_nacimiento, '') != ISNULL(@fecha_nacimiento, '') OR
                           ISNULL(sexo, '') != ISNULL(@sexo, '') OR
                           ISNULL(telefono, '') != ISNULL(@telefono, '') OR
                           ISNULL(correo, '') != ISNULL(@correo, '') OR
                           ISNULL(direccion, '') != ISNULL(@id_direccion, ''));

                    SET @result = @result + @@ROWCOUNT;
                END
            ELSE
                BEGIN
                    -- Insertar a la persona
                    INSERT INTO personas (cedula, pasaporte, nombre, nombre2, apellido1, apellido2,
                                          fecha_nacimiento, sexo, telefono, correo, estado, direccion)
                    VALUES (@cedula, @pasaporte, @nombre, @nombre2, @apellido1, @apellido2, @fecha_nacimiento,
                            @sexo, @telefono, @correo, @estado, @id_direccion);

                    SET @result = @result + @@ROWCOUNT;
                END
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE sp_vacunas_gestionar_doctor(
    @cedula VARCHAR(15) = NULL,
    @pasaporte VARCHAR(20) = NULL,
    @idoneidad VARCHAR(20),
    @nombre NVARCHAR(50),
    @apellido1 NVARCHAR(50),
    @apellido2 NVARCHAR(50) = NULL,
    @fecha_nacimiento SMALLDATETIME,
    @sexo CHAR(1),
    @telefono VARCHAR(15) = NULL,
    @correo VARCHAR(254) = NULL,
    @estado NVARCHAR(50) = NULL,
    @direccion_residencial NVARCHAR(150) = NULL,
    @distrito_reside NVARCHAR(100) = NULL,
    @categoria_medico NVARCHAR(50),
    @uuid_sede UNIQUEIDENTIFIER = NULL,
    @nombre_sede NVARCHAR(100) = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        DECLARE @id_paciente UNIQUEIDENTIFIER;
        DECLARE @id_direccion UNIQUEIDENTIFIER;
        DECLARE @inicio VARCHAR(4);
        DECLARE @libro VARCHAR(4);
        DECLARE @tomo VARCHAR(6);
        -- Validar que dirección y distrito estén ambos campos o ninguno
        IF (@direccion_residencial IS NOT NULL AND @distrito_reside IS NULL) OR
           (@direccion_residencial IS NULL AND @distrito_reside IS NOT NULL)
            BEGIN
                RAISERROR (N'Debe especificar ambos campos dirección y distrito o ninguno.', 16, 1);
            END

        IF (@cedula IS NULL AND @pasaporte IS NULL)
            BEGIN
                RAISERROR (N'Debe especificar una identificación como cédula o pasaporte', 16, 1);
            END

        IF @estado IS NULL
            SET @estado = 'NO_VALIDADO'

        -- Se hace el formato correcto de las cédulas si ya existe y es necesario para actualizar
        IF @cedula IS NOT NULL AND NOT (
            (@cedula LIKE 'PE-%' OR @cedula LIKE 'E-%' OR @cedula LIKE 'N-%' OR @cedula LIKE '[1-9]-%' OR
             @cedula LIKE '[10-13]-%'
                OR @cedula LIKE '[1-9]AV-%' OR @cedula LIKE '[10-13]AV-%' OR @cedula LIKE '[1-9]PI-%' OR
             @cedula LIKE '[10-13]PI-%')
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9]-%'
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
            BEGIN
                IF @cedula NOT LIKE 'PE-%'
                    AND @cedula NOT LIKE 'E-%'
                    AND @cedula NOT LIKE 'N-%'
                    AND @cedula NOT LIKE '[1-9]-%'
                    AND @cedula NOT LIKE '[10-13]-%'
                    AND @cedula NOT LIKE '[1-9]AV-%'
                    AND @cedula NOT LIKE '[10-13]AV-%'
                    AND @cedula NOT LIKE '[1-9]PI-%'
                    AND @cedula NOT LIKE '[10-13]PI-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato en el principio', 16, 1);
                    END

                IF @cedula NOT LIKE '%-%-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato', 16, 1);
                    END

                SET @inicio = SUBSTRING(@cedula, 1, CHARINDEX('-', @cedula) - 1);
                SET @libro = SUBSTRING(@cedula, CHARINDEX('-', @cedula) + 1,
                                       CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) - CHARINDEX('-', @cedula) -
                                       1);
                SET @tomo = SUBSTRING(@cedula, CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) + 1, LEN(@cedula));

                SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                SET @cedula = CONCAT(@inicio, '-', @libro, '-', @tomo);
            END

        IF @nombre_sede IS NOT NULL AND @uuid_sede IS NULL
            BEGIN
                SELECT @uuid_sede = sedes.id
                FROM entidades
                         INNER JOIN sedes ON entidades.id = sedes.id
                WHERE nombre LIKE @nombre_sede
                IF @uuid_sede IS NULL
                    BEGIN
                        RAISERROR ('No se ha encontrado la sede especificada', 16, 1);
                    END
            END

        BEGIN TRANSACTION
            -- Insertar la dirección si no existe
            IF @direccion_residencial IS NOT NULL AND @distrito_reside IS NOT NULL
                BEGIN
                    -- Verificar si la dirección ya existe
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = @direccion_residencial;

                    IF @id_direccion IS NULL
                        BEGIN
                            SET @id_direccion = NEWID();
                            -- Insertar nueva dirección
                            INSERT INTO direcciones (id, direccion, distrito)
                            VALUES (@id_direccion, @direccion_residencial,
                                    (SELECT id FROM distritos WHERE nombre = @distrito_reside));
                            SET @result = @result + @@ROWCOUNT;
                        END
                END
            ELSE
                BEGIN
                    -- Obtener el uuid de la dirección por defecto si es null
                    SELECT @id_direccion = id
                    FROM direcciones
                    WHERE direccion = 'Por registrar'
                      AND distrito = 0
                END

            -- Verificar si el doctor ya existe
            IF EXISTS (SELECT 1
                       FROM personas
                                LEFT JOIN doctores ON personas.id = doctores.id
                       WHERE (@cedula IS NULL OR cedula = @cedula)
                         AND (@pasaporte IS NULL OR pasaporte = @pasaporte)
                         AND idoneidad = @idoneidad)
                BEGIN
                    SELECT @id_paciente = doctores.id
                    FROM personas
                             LEFT JOIN doctores ON personas.id = doctores.id
                    WHERE (@cedula IS NULL OR cedula = @cedula)
                      AND (@pasaporte IS NULL OR pasaporte = @pasaporte)
                      AND idoneidad = @idoneidad

                    -- Actualizar el doctor si ya existe y los datos son diferentes, exceptuando el estado y usuario
                    UPDATE personas
                    SET cedula           = COALESCE(@cedula, cedula),
                        pasaporte        = COALESCE(@pasaporte, pasaporte),
                        nombre           = COALESCE(@nombre, nombre),
                        apellido1        = COALESCE(@apellido1, apellido1),
                        apellido2        = COALESCE(@apellido2, apellido2),
                        fecha_nacimiento = COALESCE(@fecha_nacimiento, fecha_nacimiento),
                        sexo             = COALESCE(@sexo, sexo),
                        telefono         = COALESCE(@telefono, telefono),
                        correo           = COALESCE(@correo, correo),
                        direccion        = COALESCE(@id_direccion, direccion)
                    WHERE id = @id_paciente
                      AND (ISNULL(cedula, '') != ISNULL(@cedula, '') OR
                           ISNULL(pasaporte, '') != ISNULL(@pasaporte, '') OR
                           ISNULL(nombre, '') != ISNULL(@nombre, '') OR
                           ISNULL(apellido1, '') != ISNULL(@apellido1, '') OR
                           ISNULL(apellido2, '') != ISNULL(@apellido2, '') OR
                           ISNULL(fecha_nacimiento, '') != ISNULL(@fecha_nacimiento, '') OR
                           ISNULL(sexo, '') != ISNULL(@sexo, '') OR
                           ISNULL(telefono, '') != ISNULL(@telefono, '') OR
                           ISNULL(correo, '') != ISNULL(@correo, '') OR
                           ISNULL(direccion, '') != ISNULL(@id_direccion, ''));

                    SET @result = @result + @@ROWCOUNT;
                END
            ELSE
                BEGIN
                    -- Insertar a la persona para el doctor si no existe
                    SET @id_paciente = NEWID();
                    INSERT INTO personas (id, cedula, pasaporte, nombre, apellido1, apellido2,
                                          fecha_nacimiento, sexo, telefono, correo, estado, direccion)
                    VALUES (@id_paciente, @cedula, @pasaporte, @nombre, @apellido1, @apellido2, @fecha_nacimiento,
                            @sexo, @telefono, @correo, @estado, @id_direccion);

                    SET @result = @result + @@ROWCOUNT;

                    -- Insertar el doctor
                    INSERT INTO doctores (id, idoneidad, categoria, sede)
                    VALUES (@id_paciente, @idoneidad, @categoria_medico, @uuid_sede)
                    SET @result = @result + @@ROWCOUNT;
                END
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE sp_vacunas_gestionar_usuario(
    @uuid_persona UNIQUEIDENTIFIER = NULL,
    @cedula VARCHAR(15) = NULL,
    @pasaporte VARCHAR(20) = NULL,
    @uuid_entidad UNIQUEIDENTIFIER = NULL,
    @licencia NVARCHAR(50) = NULL,
    @usuario NVARCHAR(50) = NULL,
    @clave_hash NVARCHAR(60),
    @estado NVARCHAR(50) = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        BEGIN TRANSACTION
            SET NOCOUNT ON;
            SET @result = 0;
            DECLARE @id_usuario UNIQUEIDENTIFIER;
            DECLARE @inicio VARCHAR(4);
            DECLARE @libro VARCHAR(4);
            DECLARE @tomo VARCHAR(6);

            IF @cedula IS NULL AND @pasaporte IS NULL AND @licencia IS NULL AND @uuid_persona IS NULL AND
               @uuid_entidad IS NULL
                BEGIN
                    RAISERROR (N'Debe especificar una identificación como cédula o pasaporte o licencia_fabricante de fabricante', 16, 1);
                END

            IF @estado IS NULL
                SET @estado = 'NO_VALIDADO'

            -- Darle formato correcto a la cédula
            IF @cedula IS NOT NULL AND NOT (
                (@cedula LIKE 'PE-%' OR @cedula LIKE 'E-%' OR @cedula LIKE 'N-%' OR @cedula LIKE '[1-9]-%' OR
                 @cedula LIKE '[10-13]-%'
                    OR @cedula LIKE '[1-9]AV-%' OR @cedula LIKE '[10-13]AV-%' OR @cedula LIKE '[1-9]PI-%' OR
                 @cedula LIKE '[10-13]PI-%')
                    AND @cedula LIKE '%-[0-9][0-9][0-9][0-9]-%'
                    AND @cedula LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
                BEGIN
                    IF @cedula NOT LIKE 'PE-%'
                        AND @cedula NOT LIKE 'E-%'
                        AND @cedula NOT LIKE 'N-%'
                        AND @cedula NOT LIKE '[1-9]-%'
                        AND @cedula NOT LIKE '[10-13]-%'
                        AND @cedula NOT LIKE '[1-9]AV-%'
                        AND @cedula NOT LIKE '[10-13]AV-%'
                        AND @cedula NOT LIKE '[1-9]PI-%'
                        AND @cedula NOT LIKE '[10-13]PI-%'
                        BEGIN
                            RAISERROR (N'cédula no cumple el formato en el principio', 16, 1);
                        END

                    IF @cedula NOT LIKE '%-%-%'
                        BEGIN
                            RAISERROR (N'cédula no cumple el formato', 16, 1);
                        END

                    SET @inicio = SUBSTRING(@cedula, 1, CHARINDEX('-', @cedula) - 1);
                    SET @libro = SUBSTRING(@cedula, CHARINDEX('-', @cedula) + 1,
                                           CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) -
                                           CHARINDEX('-', @cedula) - 1);
                    SET @tomo =
                            SUBSTRING(@cedula, CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) + 1, LEN(@cedula));

                    SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                    SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                    SET @cedula = CONCAT(@inicio, '-', @libro, '-', @tomo);
                END


            IF (@uuid_persona IS NULL AND (@cedula IS NOT NULL OR @pasaporte IS NOT NULL)) AND
               (@uuid_entidad IS NULL AND @licencia IS NULL)
                BEGIN
                    SELECT @uuid_persona = id
                    FROM personas
                    WHERE (@cedula IS NULL OR cedula = @cedula)
                      AND (@pasaporte IS NULL OR pasaporte = @pasaporte)

                    IF @uuid_persona IS NULL
                        BEGIN
                            RAISERROR (N'No se ha encontrado persona con la cédula y/o pasaporte proporcionado', 16, 1);
                        END
                END

            IF (@uuid_entidad IS NULL AND @licencia IS NOT NULL) AND
               (@uuid_persona IS NULL AND @cedula IS NULL AND @pasaporte IS NULL)
                BEGIN
                    SELECT @uuid_entidad = fabricantes.id
                    FROM entidades
                             INNER JOIN fabricantes ON entidades.id = fabricantes.id
                    WHERE licencia LIKE @licencia

                    IF @uuid_entidad IS NULL
                        BEGIN
                            RAISERROR ('No se ha encontrado entidad con la licencia_fabricante de fabricante proporcionada', 16, 1);
                        END
                END

            IF @uuid_entidad IS NULL AND @uuid_persona IS NULL
                BEGIN
                    RAISERROR (N'No se ha podido identificar la persona o entidad a gestionar. Debe proporcionar información sobre una persona o entidad, no ambas', 16, 1);
                END

            -- Verificar si el usuario ya existe
            IF EXISTS (SELECT 1
                       FROM usuarios
                                LEFT JOIN personas ON personas.usuario = usuarios.id
                                LEFT JOIN fabricantes ON fabricantes.usuario = usuarios.id
                       WHERE (@cedula IS NULL OR cedula = @cedula)
                         AND (@pasaporte IS NULL OR pasaporte = @pasaporte)
                         AND (@licencia IS NULL OR licencia = @licencia))
                BEGIN
                    SELECT @id_usuario = usuarios.id
                    FROM usuarios
                             LEFT JOIN personas ON personas.usuario = usuarios.id
                             LEFT JOIN fabricantes ON fabricantes.usuario = usuarios.id
                    WHERE (@cedula IS NULL OR cedula = @cedula)
                      AND (@pasaporte IS NULL OR pasaporte = @pasaporte)
                      AND (@licencia IS NULL OR licencia = @licencia)

                    -- Se hace el formato correcto de la cédula si ya existe y es necesario para actualizar
                    IF @cedula IS NOT NULL AND NOT (
                        (@cedula LIKE 'PE-%' OR @cedula LIKE 'E-%' OR @cedula LIKE 'N-%' OR @cedula LIKE '[1-9]-%' OR
                         @cedula LIKE '[10-13]-%'
                            OR @cedula LIKE '[1-9]AV-%' OR @cedula LIKE '[10-13]AV-%' OR @cedula LIKE '[1-9]PI-%' OR
                         @cedula LIKE '[10-13]PI-%')
                            AND @cedula LIKE '%-[0-9][0-9][0-9][0-9]-%'
                            AND @cedula LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
                        BEGIN
                            IF @cedula NOT LIKE 'PE-%'
                                AND @cedula NOT LIKE 'E-%'
                                AND @cedula NOT LIKE 'N-%'
                                AND @cedula NOT LIKE '[1-9]-%'
                                AND @cedula NOT LIKE '[10-13]-%'
                                AND @cedula NOT LIKE '[1-9]AV-%'
                                AND @cedula NOT LIKE '[10-13]AV-%'
                                AND @cedula NOT LIKE '[1-9]PI-%'
                                AND @cedula NOT LIKE '[10-13]PI-%'
                                BEGIN
                                    RAISERROR (N'cédula no cumple el formato en el principio', 16, 1);
                                END

                            IF @cedula NOT LIKE '%-%-%'
                                BEGIN
                                    RAISERROR (N'cédula no cumple el formato', 16, 1);
                                END

                            SET @inicio = SUBSTRING(@cedula, 1, CHARINDEX('-', @cedula) - 1);
                            SET @libro = SUBSTRING(@cedula, CHARINDEX('-', @cedula) + 1,
                                                   CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) -
                                                   CHARINDEX('-', @cedula) - 1);
                            SET @tomo = SUBSTRING(@cedula, CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) + 1,
                                                  LEN(@cedula));

                            SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                            SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                            SET @cedula = CONCAT(@inicio, '-', @libro, '-', @tomo);
                        END

                    -- Si existe, actualizar el usuario si cambio
                    UPDATE usuarios
                    SET usuario = ISNULL(@usuario, usuario)
                    WHERE id = @id_usuario
                      AND (ISNULL(usuario, '') != ISNULL(@usuario, ''));

                    SET @result = @result + @@ROWCOUNT;

                    -- Actualizar la clave, no podemos hacer verificaciones de seguridad dentro de la BD
                    UPDATE usuarios SET clave = @clave_hash WHERE id = @id_usuario
                    SET @result = @result + @@ROWCOUNT;
                END
            ELSE
                BEGIN
                    SET @id_usuario = NEWID();
                    -- Si no existe, insertar un nuevo registro
                    INSERT INTO Usuarios (id, usuario, clave)
                    VALUES (@id_usuario, @usuario, @clave_hash);

                    SET @result = @result + @@ROWCOUNT;

                    IF @uuid_entidad IS NOT NULL
                        BEGIN
                            UPDATE fabricantes
                            SET usuario = @id_usuario
                            WHERE id = @uuid_entidad
                            SET @result = @result + @@ROWCOUNT;
                        END
                    IF @uuid_persona IS NOT NULL
                        BEGIN
                            UPDATE personas
                            SET usuario = @id_usuario
                            WHERE id = @uuid_persona
                            SET @result = @result + @@ROWCOUNT;
                        END
                END
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH
END
GO

CREATE PROCEDURE sp_vacunas_insert_dosis(
    @uuid_paciente UNIQUEIDENTIFIER = NULL,
    @cedula VARCHAR(15) = NULL,
    @pasaporte VARCHAR(20) = NULL,
    @id_temporal VARCHAR(50) = NULL,
    @fecha_aplicacion DATETIME,
    @numero_dosis CHAR(2),
    @uuid_vacuna UNIQUEIDENTIFIER = NULL,
    @nombre_vacuna NVARCHAR(100) = NULL,
    @uuid_sede UNIQUEIDENTIFIER = NULL,
    @nombre_sede NVARCHAR(100) = NULL,
    @lote NVARCHAR(10) = NULL,
    @uuid_doctor UNIQUEIDENTIFIER = NULL,
    @idoneidad NVARCHAR(20) = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        DECLARE @inicio VARCHAR(5);
        DECLARE @libro VARCHAR(5);
        DECLARE @tomo VARCHAR(10);
        -- validar los datos opcionales tengan mínimo 1 dato para cada tabla
        IF @nombre_vacuna IS NULL AND @uuid_vacuna IS NULL
            BEGIN
                RAISERROR ('Debe especificar la vacuna por uuid o nombre.', 16, 1);
            END

        IF @uuid_sede IS NULL AND @nombre_sede IS NULL
            BEGIN
                RAISERROR ('Debe especificar la sede por uuid o nombre.', 16, 1);
            END

        IF (@cedula IS NULL AND @pasaporte IS NULL AND @id_temporal IS NULL)
            BEGIN
                RAISERROR (N'Debe especificar una identificación como cédula o pasaporte o temporal', 16, 1);
            END

        IF @nombre_vacuna IS NOT NULL AND @uuid_vacuna IS NULL
            BEGIN
                SELECT @uuid_vacuna = id
                FROM vacunas
                WHERE nombre = @nombre_vacuna;

                -- Verificar si se encontró la vacuna
                IF @uuid_vacuna IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ninguna vacuna con el nombre proporcionado.', 16, 1);
                    END
            END

        IF @nombre_sede IS NOT NULL AND @uuid_sede IS NULL
            BEGIN
                SELECT @uuid_sede = sedes.id
                FROM entidades
                         INNER JOIN sedes ON entidades.id = sedes.id
                WHERE nombre = @nombre_sede;

                -- Verificar si se encontró la sede
                IF @uuid_sede IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ninguna sede con el nombre proporcionado.', 16, 1);
                    END
            END

        -- Verificar si la vacuna existe
        IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = @uuid_vacuna)
            BEGIN
                RAISERROR ('La vacuna especificada no existe.', 16, 1);
            END

        -- Verificar si la sede existe
        IF NOT EXISTS (SELECT 1 FROM sedes WHERE id = @uuid_sede)
            BEGIN
                RAISERROR ('La sede especificada no existe.', 16, 1);
            END

        IF @cedula IS NOT NULL AND NOT (
            (@cedula LIKE 'PE-%' OR @cedula LIKE 'E-%' OR @cedula LIKE 'N-%' OR @cedula LIKE '[1-9]-%' OR
             @cedula LIKE '[10-13]-%'
                OR @cedula LIKE '[1-9]AV-%' OR @cedula LIKE '[10-13]AV-%' OR @cedula LIKE '[1-9]PI-%' OR
             @cedula LIKE '[10-13]PI-%')
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9]-%'
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
            BEGIN
                IF @cedula NOT LIKE 'PE-%'
                    AND @cedula NOT LIKE 'E-%'
                    AND @cedula NOT LIKE 'N-%'
                    AND @cedula NOT LIKE '[1-9]-%'
                    AND @cedula NOT LIKE '[10-13]-%'
                    AND @cedula NOT LIKE '[1-9]AV-%'
                    AND @cedula NOT LIKE '[10-13]AV-%'
                    AND @cedula NOT LIKE '[1-9]PI-%'
                    AND @cedula NOT LIKE '[10-13]PI-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato en el principio', 16, 1);
                    END

                IF @cedula NOT LIKE '%-%-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato', 16, 1);
                    END

                SET @inicio = SUBSTRING(@cedula, 1, CHARINDEX('-', @cedula) - 1);
                SET @libro = SUBSTRING(@cedula, CHARINDEX('-', @cedula) + 1,
                                       CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) - CHARINDEX('-', @cedula) -
                                       1);
                SET @tomo = SUBSTRING(@cedula, CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) + 1, LEN(@cedula));

                SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                SET @cedula = CONCAT(@inicio, '-', @libro, '-', @tomo);
            END

        IF @id_temporal IS NOT NULL AND (@id_temporal LIKE 'RN-%' OR @id_temporal LIKE 'RN[1-15]-%')
            BEGIN
                DECLARE @cedula_madre VARCHAR(15);
                DECLARE @recien_nacido VARCHAR(5);
                -- Extraer la parte del recién nacido y la cédula de la madre
                SET @recien_nacido = SUBSTRING(@id_temporal, 1, CHARINDEX('-', @id_temporal) - 1);
                SET @cedula_madre = SUBSTRING(@id_temporal, CHARINDEX('-', @id_temporal) + 1, LEN(@id_temporal));

                -- Verificar si la cédula de la madre ya está en el formato correcto
                IF NOT ((@cedula_madre LIKE 'PE-%' OR @cedula_madre LIKE 'E-%' OR @cedula_madre LIKE 'N-%' OR
                         @cedula_madre LIKE '[1-9]-%'
                    OR @cedula_madre LIKE '[10-13]-%' OR @cedula_madre LIKE '[1-9]AV-%' OR
                         @cedula_madre LIKE '[10-13]AV-%' OR @cedula_madre LIKE '[1-9]PI-%'
                    OR @cedula_madre LIKE '[10-13]PI-%')
                    AND @cedula_madre LIKE '%-[0-9][0-9][0-9][0-9]-%'
                    AND @cedula_madre LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
                    BEGIN
                        IF @cedula_madre NOT LIKE 'PE-%'
                            AND @cedula_madre NOT LIKE 'E-%'
                            AND @cedula_madre NOT LIKE 'N-%'
                            AND @cedula_madre NOT LIKE '[1-9]-%'
                            AND @cedula_madre NOT LIKE '[10-13]-%'
                            AND @cedula_madre NOT LIKE '[1-9]AV-%'
                            AND @cedula_madre NOT LIKE '[10-13]AV-%'
                            AND @cedula_madre NOT LIKE '[1-9]PI-%'
                            AND @cedula_madre NOT LIKE '[10-13]PI-%'
                            BEGIN
                                RAISERROR (N'cédula de la madre del recién nacido no cumple el formato en el principio', 16, 1);
                            END

                        IF @cedula_madre NOT LIKE '%-%-%'
                            BEGIN
                                RAISERROR (N'cédula de la madre del recién nacido no cumple el formato', 16, 1);
                            END

                        SET @inicio = SUBSTRING(@cedula_madre, 1, CHARINDEX('-', @cedula_madre) - 1);
                        SET @libro = SUBSTRING(@cedula_madre, CHARINDEX('-', @cedula_madre) + 1,
                                               CHARINDEX('-', @cedula_madre, CHARINDEX('-', @cedula_madre) + 1) -
                                               CHARINDEX('-', @cedula_madre) - 1);
                        SET @tomo = SUBSTRING(@cedula_madre,
                                              CHARINDEX('-', @cedula_madre, CHARINDEX('-', @cedula_madre) + 1) + 1,
                                              LEN(@cedula_madre));

                        SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                        SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                        SET @cedula_madre = CONCAT(@inicio, @libro, @tomo);
                    END
                SET @id_temporal = CONCAT(@recien_nacido, '-', @cedula_madre);
            END

        -- Verificar si el paciente existe
        IF NOT EXISTS (SELECT 1
                       FROM personas
                                INNER JOIN pacientes ON personas.id = pacientes.id
                       WHERE (@uuid_paciente IS NULL OR pacientes.id = @uuid_paciente)
                         AND (@cedula IS NULL OR cedula = @cedula)
                         AND (@pasaporte IS NULL OR pasaporte = @pasaporte)
                         AND (@id_temporal IS NULL OR identificacion_temporal = @id_temporal))
            BEGIN
                RAISERROR ('El paciente no existe', 16, 1);
            END
        ELSE
            IF @uuid_paciente IS NULL
                BEGIN
                    SELECT @uuid_paciente = pacientes.id
                    FROM personas
                             INNER JOIN pacientes ON personas.id = pacientes.id
                    WHERE (@uuid_paciente IS NULL OR pacientes.id = @uuid_paciente)
                      AND (@cedula IS NULL OR cedula = @cedula)
                      AND (@pasaporte IS NULL OR pasaporte = @pasaporte)
                      AND (@id_temporal IS NULL OR identificacion_temporal = @id_temporal);
                END

        IF EXISTS (SELECT 1
                   FROM doctores
                   WHERE (@uuid_doctor IS NULL OR id = @uuid_doctor)
                     AND (@idoneidad IS NULL OR idoneidad = @idoneidad))
            BEGIN
                IF @uuid_doctor IS NULL
                    BEGIN
                        SELECT @uuid_doctor = id
                        FROM doctores
                        WHERE (@uuid_doctor IS NULL OR id = @uuid_doctor)
                          AND (@idoneidad IS NULL OR idoneidad = @idoneidad)
                    END
            END
        ELSE
            BEGIN
                SET @uuid_doctor = NULL;
            END

        BEGIN TRANSACTION
            INSERT INTO Dosis (paciente, fecha_aplicacion, numero_dosis, vacuna, sede, lote, doctor)
            VALUES (@uuid_paciente, @fecha_aplicacion, @numero_dosis, @uuid_vacuna, @uuid_sede, @lote, @uuid_doctor);
            SET @result = @result + @@ROWCOUNT;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE sp_vacunas_distribuir_vacunas(
    @uuid_almacen UNIQUEIDENTIFIER = NULL,
    @nombre_almacen NVARCHAR(100) = NULL,
    @id_sede UNIQUEIDENTIFIER = NULL,
    @nombre_sede NVARCHAR(100) = NULL,
    @uuid_vacuna UNIQUEIDENTIFIER = NULL,
    @nombre_vacuna NVARCHAR(100) = NULL,
    @cantidad INT,
    @lote NVARCHAR(10),
    @fecha_distribucion DATETIME = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        -- Validar que se ingresó al menos 1 valor requerido para cada tabla
        IF @uuid_almacen IS NULL AND @nombre_almacen IS NULL
            BEGIN
                RAISERROR (N'Debe especificar el Almacén por su id o nombre', 16, 1);
            END

        IF @nombre_vacuna IS NULL AND @uuid_vacuna IS NULL
            BEGIN
                RAISERROR ('Debe especificar la vacuna por uuid o nombre', 16, 1);
            END

        IF @id_sede IS NULL AND @nombre_sede IS NULL
            BEGIN
                RAISERROR ('Debe especificar la sede por uuid o nombre', 16, 1);
            END

        IF @nombre_almacen IS NOT NULL AND @uuid_almacen IS NULL
            BEGIN
                SELECT @uuid_almacen = almacenes.id
                FROM entidades
                         INNER JOIN almacenes ON entidades.id = almacenes.id
                WHERE nombre = @nombre_almacen;

                -- Verificar si se encontró la vacuna
                IF @uuid_almacen IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ningún Almacén con el nombre proporcionado', 16, 1);
                    END
            END

        IF @nombre_vacuna IS NOT NULL AND @uuid_vacuna IS NULL
            BEGIN
                SELECT @uuid_vacuna = id
                FROM vacunas
                WHERE nombre = @nombre_vacuna;

                -- Verificar si se encontró la vacuna
                IF @uuid_vacuna IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ninguna vacuna con el nombre proporcionado', 16, 1);
                    END
            END

        IF @nombre_sede IS NOT NULL AND @id_sede IS NULL
            BEGIN
                SELECT @id_sede = sedes.id
                FROM entidades
                         INNER JOIN sedes ON entidades.id = sedes.id
                WHERE nombre = @nombre_sede;

                -- Verificar si se encontró la vacuna
                IF @id_sede IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ninguna sede con el nombre proporcionado', 16, 1);
                    END
            END

        IF @fecha_distribucion IS NULL
            SET @fecha_distribucion = CURRENT_TIMESTAMP;

        INSERT INTO distribuciones_vacunas (almacen, sede, vacuna, cantidad, lote, fecha_distribucion)
        VALUES (@uuid_almacen, @id_sede, @uuid_vacuna, @cantidad, @lote, @fecha_distribucion);

        SET @result = @result + @@ROWCOUNT;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH
END;
GO

CREATE PROCEDURE sp_vacunas_insert_roles_usuario(
    @cedula VARCHAR(15) = NULL,
    @pasaporte VARCHAR(20) = NULL,
    @licencia NVARCHAR(50) = NULL,
    @usuario NVARCHAR(50) = NULL,
    @correo VARCHAR(254) = NULL,
    @telefono VARCHAR(15) = NULL,
    @roles NVARCHAR(MAX), -- cadena delimitada por comas los roles
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        DECLARE @id_usuario UNIQUEIDENTIFIER;
        DECLARE @roles_tabla TABLE
                             (
                                 id_rol INT
                             ); -- tabla temporal para almacenar la cadena de roles
        DECLARE @inicio VARCHAR(4);
        DECLARE @libro VARCHAR(4);
        DECLARE @tomo VARCHAR(6);

        -- Se hace el formato correcto de la cédula
        IF @cedula IS NOT NULL AND NOT (
            (@cedula LIKE 'PE-%' OR @cedula LIKE 'E-%' OR @cedula LIKE 'N-%' OR @cedula LIKE '[1-9]-%' OR
             @cedula LIKE '[10-13]-%'
                OR @cedula LIKE '[1-9]AV-%' OR @cedula LIKE '[10-13]AV-%' OR @cedula LIKE '[1-9]PI-%' OR
             @cedula LIKE '[10-13]PI-%')
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9]-%'
                AND @cedula LIKE '%-[0-9][0-9][0-9][0-9][0-9][0-9]')
            BEGIN
                IF @cedula NOT LIKE 'PE-%'
                    AND @cedula NOT LIKE 'E-%'
                    AND @cedula NOT LIKE 'N-%'
                    AND @cedula NOT LIKE '[1-9]-%'
                    AND @cedula NOT LIKE '[10-13]-%'
                    AND @cedula NOT LIKE '[1-9]AV-%'
                    AND @cedula NOT LIKE '[10-13]AV-%'
                    AND @cedula NOT LIKE '[1-9]PI-%'
                    AND @cedula NOT LIKE '[10-13]PI-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato en el principio', 16, 1);
                    END

                IF @cedula NOT LIKE '%-%-%'
                    BEGIN
                        RAISERROR (N'cédula no cumple el formato', 16, 1);
                    END

                SET @inicio = SUBSTRING(@cedula, 1, CHARINDEX('-', @cedula) - 1);
                SET @libro = SUBSTRING(@cedula, CHARINDEX('-', @cedula) + 1,
                                       CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) - CHARINDEX('-', @cedula) -
                                       1);
                SET @tomo = SUBSTRING(@cedula, CHARINDEX('-', @cedula, CHARINDEX('-', @cedula) + 1) + 1, LEN(@cedula));

                SET @libro = RIGHT(CONCAT('0000', @libro), 4);
                SET @tomo = RIGHT(CONCAT('000000', @tomo), 6);

                SET @cedula = CONCAT(@inicio, '-', @libro, '-', @tomo);
            END

        -- Se busca el uuid del usuario a darle roles
        SELECT @id_usuario = usuarios.id
        FROM personas
                 LEFT JOIN usuarios ON personas.usuario = usuarios.id
        WHERE (@cedula IS NULL OR cedula = @cedula)
          AND (@pasaporte IS NULL OR pasaporte = @pasaporte)
          AND (@usuario IS NULL OR usuarios.usuario = @usuario)
          AND (@correo IS NULL OR correo = @correo)
          AND (@telefono IS NULL OR telefono = @telefono)

        IF @id_usuario IS NULL
            BEGIN
                SELECT @id_usuario = fabricantes.id
                FROM entidades
                         INNER JOIN fabricantes ON entidades.id = fabricantes.id
                WHERE (@licencia IS NULL OR licencia = @licencia)
            END

        IF @id_usuario IS NULL
            BEGIN
                RAISERROR (N'No se ha encontrado el usuario con la identificación proporcionada', 16, 1);
            END

        BEGIN TRANSACTION
            -- Convertir la cadena delimitada en la tabla temporal
            INSERT INTO @roles_tabla (id_rol)
            SELECT value
            FROM string_split(@roles, ',')

            -- Eliminar los roles existentes
            DELETE FROM usuarios_roles WHERE usuario = @id_usuario
            SET @result = @result + @@ROWCOUNT;

            -- Insertar los roles nuevos
            INSERT INTO usuarios_roles (usuario, rol)
            SELECT @id_usuario, id_rol
            FROM @roles_tabla
            SET @result = @result + @@ROWCOUNT;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH
END
GO

CREATE PROCEDURE sp_vacunas_insert_vacuna_enfermedad(
    @uuid_vacuna UNIQUEIDENTIFIER = NULL,
    @nombre_vacuna NVARCHAR(100) = NULL,
    @id_enfermedad INT = NULL,
    @nombre_enfermedad NVARCHAR(100) = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        -- Validar que se ingresó al menos 1 valor requerido para cada tabla
        IF @nombre_vacuna IS NULL AND @uuid_vacuna IS NULL
            BEGIN
                RAISERROR ('Debe especificar la vacuna por uuid o nombre', 16, 1);
            END

        IF @nombre_enfermedad IS NULL AND @id_enfermedad IS NULL
            BEGIN
                RAISERROR ('Debe especificar la enfermedad por id o nombre', 16, 1);
            END

        -- Obtener uuid_vacuna si se proporcionó nombre_vacuna
        IF @nombre_vacuna IS NOT NULL AND @uuid_vacuna IS NULL
            BEGIN
                SELECT @uuid_vacuna = id
                FROM vacunas
                WHERE nombre = @nombre_vacuna;

                -- Verificar si se encontró la vacuna
                IF @uuid_vacuna IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ninguna vacuna con el nombre proporcionado', 16, 1);
                    END
            END

        -- Obtener id_enfermedad si se proporcionó nombre_enfermedad
        IF @nombre_enfermedad IS NOT NULL AND @id_enfermedad IS NULL
            BEGIN
                SELECT @id_enfermedad = id FROM enfermedadeS WHERE nombre = @nombre_enfermedad

                IF @id_enfermedad IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ninguna enfermedad con el nombre proporcionado', 16, 1);
                    END
            END

        BEGIN TRANSACTION
            INSERT INTO vacunas_enfermedades (vacuna, enfermedad)
            VALUES (@uuid_vacuna, @id_enfermedad);
            SET @result = @result + @@ROWCOUNT;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END
GO

CREATE PROCEDURE sp_vacunas_insert_fabricante_vacuna(
    @uuid_fabricante UNIQUEIDENTIFIER = NULL,
    @nombre_fabricante NVARCHAR(100) = NULL,
    @uuid_vacuna UNIQUEIDENTIFIER = NULL,
    @nombre_vacuna NVARCHAR(100) = NULL,
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        -- validar que se ingresó al menos 1 valor requerido para cada tabla
        IF @uuid_fabricante IS NULL AND @nombre_fabricante IS NULL
            BEGIN
                RAISERROR ('Debe especificar un fabricante por su id o nombre.', 16, 1);
            END

        IF @nombre_vacuna IS NULL AND @uuid_vacuna IS NULL
            BEGIN
                RAISERROR ('Debe especificar la vacuna por uuid o nombre.', 16, 1);
            END

        -- Obtener id_fabricante si se proporcionó nombre_fabricante
        IF @nombre_fabricante IS NOT NULL
            BEGIN
                SELECT @uuid_fabricante = fabricantes.id
                FROM entidades
                         INNER JOIN fabricantes ON entidades.id = fabricantes.id
                WHERE nombre = @nombre_fabricante;

                IF @uuid_fabricante IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ningún fabricante con el nombre proporcionado.', 16, 1);
                    END
            END

        -- Obtener uuid_vacuna si se proporcionó nombre_vacuna
        IF @nombre_vacuna IS NOT NULL
            BEGIN
                SELECT @uuid_vacuna = id
                FROM vacunas
                WHERE nombre = @nombre_vacuna;

                -- Verificar si se encontró la vacuna
                IF @uuid_vacuna IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ninguna vacuna con el nombre proporcionado.', 16, 1);
                    END
            END

        BEGIN TRANSACTION
            INSERT INTO fabricantes_vacunas(fabricante, vacuna)
            VALUES (@uuid_fabricante, @uuid_vacuna);
            SET @result = @result + @@ROWCOUNT;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END
GO

CREATE PROCEDURE sp_vacunas_insert_almacen_inventario(
    @uuid_almacen UNIQUEIDENTIFIER = NULL,
    @nombre_almacen NVARCHAR(100) = NULL,
    @uuid_vacuna UNIQUEIDENTIFIER = NULL,
    @nombre_vacuna NVARCHAR(100) = NULL,
    @cantidad INT,
    @fecha_expiracion_lote DATETIME,
    @lote_almacen NVARCHAR(10),
    @result INT OUTPUT
)
AS
BEGIN
    BEGIN TRY
        SET NOCOUNT ON;
        SET @result = 0;
        -- Validar que se ingresó al menos 1 valor requerido para cada tabla
        IF @uuid_almacen IS NULL AND @nombre_almacen IS NULL
            BEGIN
                RAISERROR (N'Debe especificar el almacén por su id o nombre', 16, 1);
            END

        IF @nombre_vacuna IS NULL AND @uuid_vacuna IS NULL
            BEGIN
                RAISERROR ('Debe especificar la vacuna por uuid o nombre.', 16, 1);
            END

        IF @nombre_almacen IS NOT NULL AND @uuid_almacen IS NULL
            BEGIN
                SELECT @uuid_almacen = almacenes.id
                FROM entidades
                         INNER JOIN almacenes ON entidades.id = almacenes.id
                WHERE nombre = @nombre_almacen;

                -- Verificar si se encontró la vacuna
                IF @uuid_almacen IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ningún almacén con el nombre proporcionado.', 16, 1);
                    END
            END

        IF @nombre_vacuna IS NOT NULL AND @uuid_vacuna IS NULL
            BEGIN
                SELECT @uuid_vacuna = id
                FROM vacunas
                WHERE nombre = @nombre_vacuna;

                -- Verificar si se encontró la vacuna
                IF @uuid_vacuna IS NULL
                    BEGIN
                        RAISERROR (N'No se encontró ninguna vacuna con el nombre proporcionado.', 16, 1);
                    END
            END

        IF @fecha_expiracion_lote <= CURRENT_TIMESTAMP
            BEGIN
                RAISERROR ('La fecha de vencimiento del lote de la vacuna no puede ser pasada.', 16, 1);
            END

        BEGIN TRANSACTION
            INSERT INTO almacenes_inventarios (almacen, vacuna, cantidad, fecha_expiracion, lote)
            VALUES (@uuid_almacen, @uuid_vacuna, @cantidad, @fecha_expiracion_lote, @lote_almacen);
            SET @result = @result + @@ROWCOUNT;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END
GO

PRINT (N'Creando funciones');
GO
-- Funciones
CREATE FUNCTION fn_vacunas_get_persona_by_full_name(
    @nombre_completo NVARCHAR(100)
)
    RETURNS TABLE
        AS
        RETURN(SELECT *
               FROM personas
               WHERE CONCAT(nombre, SPACE(1), apellido1, IIF(apellido2 IS NOT NULL, CONCAT(SPACE(1), apellido2), ''))
                         LIKE RTRIM(@nombre_completo) + '%');
GO

CREATE FUNCTION fn_vacunas_find_dosis(
    @uuid_vacuna UNIQUEIDENTIFIER = NULL,
    @uuid_paciente UNIQUEIDENTIFIER = NULL,
    @cedula VARCHAR(15) = NULL,
    @pasaporte VARCHAR(20) = NULL,
    @id_temporal VARCHAR(50) = NULL,
    @numero_dosis CHAR(2) = NULL,
    @fecha_aplicacion DATETIME = NULL,
    @uuid_sede UNIQUEIDENTIFIER = NULL
)
    RETURNS TABLE
        AS
        RETURN(SELECT d.id,
                      d.fecha_aplicacion,
                      d.numero_dosis,
                      d.vacuna,
                      d.sede
               FROM dosis d
                        LEFT JOIN personas ON d.paciente = personas.id
               WHERE (d.vacuna = @uuid_vacuna OR @uuid_vacuna IS NULL)
                 AND (d.paciente = @uuid_paciente OR d.paciente = (SELECT id
                                                                   FROM pacientes
                                                                   WHERE cedula LIKE @cedula
                                                                      OR pasaporte = @pasaporte
                                                                      OR identificacion_temporal = @id_temporal))
                 AND (d.numero_dosis = @numero_dosis OR @numero_dosis IS NULL)
                 AND (d.fecha_aplicacion = @fecha_aplicacion OR @fecha_aplicacion IS NULL)
                 AND (d.sede = @uuid_sede OR @uuid_sede IS NULL));
GO

CREATE FUNCTION fn_vacunas_get_usuarios_by_identificacion(
    @cedula VARCHAR(15) = NULL,
    @pasaporte VARCHAR(20) = NULL,
    @licencia VARCHAR(50)
)
    RETURNS TABLE
        AS
        RETURN(SELECT u.id,
                      u.created_at,
                      ur.rol,
                      r.nombre
               FROM usuarios u
                        LEFT JOIN personas p ON u.id = p.usuario
                        LEFT JOIN fabricantes f ON u.id = f.usuario
                        INNER JOIN usuarios_roles ur ON u.id = ur.usuario
                        INNER JOIN roles r ON ur.rol = r.id
               WHERE cedula = @cedula
                  OR pasaporte = @pasaporte
                  OR licencia = @licencia);
GO

PRINT (N'Creación de objetos finalizada!');
