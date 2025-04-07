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
