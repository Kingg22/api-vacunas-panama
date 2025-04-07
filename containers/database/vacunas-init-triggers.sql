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
