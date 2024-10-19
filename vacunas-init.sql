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

PRINT ('Intentando crear el login si no existe');
GO
-- Únicamente las aplicaciones deben tener un login y su user con permisos
-- PUEDE CAMBIAR EL NOMBRE LOGIN_NAME Y COLOCARLO COMO DB_USER para spring
IF NOT EXISTS (SELECT *
               FROM sys.server_principals
               WHERE name = 'LOGIN_NAME')
    BEGIN
        PRINT (N'Creando el login');
        CREATE LOGIN LOGIN_NAME
            -- COLOCAR AQUÍ CONTRASEÑA EN TEXTO PLANO. CUIDADO AL SUBIR ESTE ARCHIVO
            -- COLOCAR LUEGO COMO DB_PASSWORD para spring
            WITH PASSWORD = '',
            DEFAULT_DATABASE = vacunas
    END
ELSE
BEGIN
    PRINT ('El usuario ya existe');
END
-- crear usuario de la base de datos
-- COLOCAR EL MISMO LOGIN_NAME DE ARRIBA
PRINT (N'Creando el usuario');
BEGIN TRY
    CREATE USER SpringAPI FOR LOGIN LOGIN_NAME;
END TRY
BEGIN CATCH
    PRINT ('Error al crear el usuario. Detalles del error: ' + ERROR_MESSAGE());
END CATCH;
-- Verificar si el usuario existe antes de asignar permisos
IF EXISTS (SELECT * FROM sys.database_principals WHERE name = 'SpringAPI')
BEGIN
    BEGIN TRY
        PRINT (N'Otorgando permisos al user');
        EXEC sp_addrolemember 'db_datareader', 'SpringApi';
        EXEC sp_addrolemember 'db_datawriter', 'SpringApi';
        GRANT EXECUTE ON SCHEMA::dbo TO SpringAPI;
        -- dependiendo la aplicación se le puede asignar más o menos permisos**
    END TRY
    BEGIN CATCH
        PRINT ('Error al otorgar permisos. Detalles del error: ' + ERROR_MESSAGE());
    END CATCH;
END
ELSE
BEGIN
    PRINT ('El usuario SpringAPI no existe, no se pueden otorgar permisos.');
END
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
    disabled BIT
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
    disabled BIT
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
    id                        UNIQUEIDENTIFIER PRIMARY KEY
        CONSTRAINT df_vacunas_id DEFAULT NEWID(),
    nombre                    NVARCHAR(100) NOT NULL,
    --fabricante UNIQUEIDENTIFIER NOT NULL,
    edad_minima_dias          INT,
    intervalo_dosis_1_2_dias  INT,
    dosis_maxima              CHAR(2),
    created_at                DATETIME      NOT NULL
        CONSTRAINT df_vacunas_created DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME,
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
    paciente UNIQUEIDENTIFIER NOT NULL,
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
    usuario UNIQUEIDENTIFIER,
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
       v.intervalo_dosis_1_2_dias           'Intervalo entre Dosis 1 y 2 Recomendado en Meses',
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
SELECT v.nombre                    AS 'Nombre Vacuna',
       d.numero_dosis              AS N'Número de dosis',
       STRING_AGG(e.nombre, ', ')  AS N'Enfermedades Prevenidas',
       v.edad_minima_dias          AS N'Edad Mínima Recomendada en Meses',
       d.fecha_aplicacion          AS N'Fecha de Aplicación',
       v.intervalo_dosis_1_2_dias AS 'Intervalo Recomendado entre Dosis 1 y 2 en Meses',
       DATEDIFF(DAY, d.fecha_aplicacion,
                (SELECT MAX(d2.fecha_aplicacion)
                 FROM dosis d2
                 WHERE d2.paciente = p.id
                   AND d2.vacuna = d.vacuna
                   AND d2.numero_dosis > d.numero_dosis))
                                   AS N'Intervalo Real en Días',
       ee.nombre                   AS 'Sede',
       ee.dependencia              AS 'Dependencia',
       p.id,
       v.id                        AS 'id_vacuna',
       d.id                        AS 'id_dosis',
       STRING_AGG(e.id, ', ')      AS 'ids_enfermedades'
FROM pacientes p
         JOIN dosis d ON p.id = d.paciente
         JOIN vacunas v ON d.vacuna = v.id
         LEFT JOIN vacunas_enfermedades ve ON v.id = ve.vacuna
         LEFT JOIN enfermedades e ON ve.enfermedad = e.id
         LEFT JOIN sedes s ON d.sede = s.id
         LEFT JOIN entidades ee ON s.id = ee.id
GROUP BY p.id,
         v.nombre, v.edad_minima_dias, v.intervalo_dosis_1_2_dias,
         ee.nombre, ee.dependencia,
         v.id,
         d.id, d.vacuna, d.fecha_aplicacion, d.numero_dosis;
GO

CREATE VIEW view_distribuciones_almacenes_sedes_vacunas_fabricantes AS
SELECT ae.nombre      AS N'Nombre Almacén',
       ae.dependencia AS N'Dependencia Almacén',
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
SELECT ae.nombre      AS N'Nombre Almacén',
       ae.dependencia AS N'Dependencia Almacén',
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

        SELECT @numero_dosis = i.numero_dosis, @id_vacuna = i.vacuna, @id_sede = i.sede, @lote = i.lote, @id_paciente = paciente
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
EXEC sp_vacunas_gestionar_paciente '3PI-1-654', NULL, NULL, 'Elena', N'González', NULL, '1993-09-29', 'F', '+50760036666',
     'elena.nzalez@example.com', 'ACTIVO', 'Calle 11, Edificio C', 'La Chorrera', NULL;
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
