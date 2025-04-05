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
