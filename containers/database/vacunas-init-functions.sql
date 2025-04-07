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
