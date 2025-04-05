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
                                          ('Por registrar', -- Problemas o nueva provincia sin registrar aún
                                           'Bocas del Toro', -- 1
                                           N'Coclé', -- 2
                                           N'Colón', -- 3
                                           N'Chiriquí', -- 4
                                           N'Darién', -- 5
                                           'Herrera', -- 6
                                           'Los Santos', -- 7
                                           N'Panamá', -- 8
                                           'Veraguas', -- 9
                                           'Guna Yala', -- 10
                                           N'Emberá-Wounaan', -- 11
                                           N'Ngäbe-Buglé', -- 12
                                           N'Panamá Oeste', -- 13
                                           N'Naso Tjër Di', -- 14
                                              -- No Fijas, pueden moverse por nuevas provincias o comarcas nivel provincia oficiales. Orden dado por número de cédula panameña
                                           N'Guna de Madugandí', -- 15 comarca nivel corregimiento
                                           N'Guna de Wargandí', -- 16 comarca nivel corregimiento
                                           'Extranjero' -- 17
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
            -- comarca Guna Yala, Madugandí, Wargandí no tiene distrito, provincia 10
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
