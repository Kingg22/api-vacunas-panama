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
IF EXISTS (SELECT *
           FROM sys.database_principals
           WHERE name = 'SpringAPI')
BEGIN
BEGIN TRY
PRINT (N'Otorgando permisos al user');
EXEC sp_addrolemember 'db_datareader', 'SpringAPI';
EXEC sp_addrolemember 'db_datawriter', 'SpringAPI';
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
