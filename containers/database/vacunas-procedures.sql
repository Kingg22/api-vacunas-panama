-- Procedimientos de ayuda para insertar
CREATE OR REPLACE FUNCTION obtener_o_insertar_direccion(
    p_direccion VARCHAR,
    p_distrito_nombre VARCHAR
)
    RETURNS UUID
    LANGUAGE plpgsql
AS $$
DECLARE
v_id_direccion UUID;
BEGIN
    -- Caso cuando no se proporciona dirección ni distrito
    IF p_direccion IS NULL AND p_distrito_nombre IS NULL THEN
SELECT id INTO v_id_direccion
FROM direcciones
WHERE direccion = 'Por registrar' AND distrito = 0
    LIMIT 1;

IF NOT FOUND THEN
            v_id_direccion := gen_random_uuid();
INSERT INTO direcciones(id, direccion, distrito)
VALUES (v_id_direccion, 'Por registrar', 0);
END IF;

RETURN v_id_direccion;
END IF;

    -- Verificar si ya existe la dirección
SELECT d.id INTO v_id_direccion
FROM direcciones d
         JOIN distritos dis ON d.distrito = dis.id
WHERE d.direccion = p_direccion AND dis.nombre = p_distrito_nombre
    LIMIT 1;

IF FOUND THEN
        RETURN v_id_direccion;
END IF;

    -- Insertar si no existe
    v_id_direccion := gen_random_uuid();

INSERT INTO direcciones(id, direccion, distrito)
VALUES (
           v_id_direccion,
           p_direccion,
           (SELECT id FROM distritos WHERE nombre = p_distrito_nombre LIMIT 1)
    );

RETURN v_id_direccion;
END;
$$;

CREATE OR REPLACE FUNCTION sp_vacunas_gestionar_paciente(
    p_nombre VARCHAR,
    p_apellido1 VARCHAR,
    p_fecha_nacimiento DATE,
    p_sexo CHAR(1),
    p_cedula VARCHAR DEFAULT NULL,
    p_pasaporte VARCHAR DEFAULT NULL,
    p_id_temporal VARCHAR DEFAULT NULL,
    p_apellido2 VARCHAR DEFAULT NULL,
    p_telefono VARCHAR DEFAULT NULL,
    p_correo VARCHAR DEFAULT NULL,
    p_estado VARCHAR DEFAULT NULL,
    p_direccion_residencial VARCHAR DEFAULT NULL,
    p_distrito_reside VARCHAR DEFAULT NULL
)
    RETURNS UUID
    LANGUAGE plpgsql
AS $$
DECLARE
v_id_paciente UUID;
    v_id_direccion UUID;
BEGIN
    -- Validaciones previas
    IF (p_direccion_residencial IS NOT NULL AND p_distrito_reside IS NULL) OR
       (p_direccion_residencial IS NULL AND p_distrito_reside IS NOT NULL) THEN
        RAISE EXCEPTION 'Debe especificar ambos campos (dirección y distrito) o ninguno.';
END IF;

    IF p_cedula IS NULL AND p_pasaporte IS NULL AND p_id_temporal IS NULL THEN
        RAISE EXCEPTION 'Debe especificar una identificación como cédula o pasaporte o id temporal.';
END IF;

    IF p_estado IS NULL THEN
        p_estado := 'NO_VALIDADO';
END IF;

    -- Inicio de transacción
BEGIN
        -- Manejo de dirección
        v_id_direccion := obtener_o_insertar_direccion(p_direccion_residencial, p_distrito_reside);

        -- Buscar si ya existe
SELECT personas.id INTO v_id_paciente
FROM personas
         LEFT JOIN pacientes ON personas.id = pacientes.id
WHERE (p_cedula IS NULL OR personas.cedula = p_cedula)
  AND (p_pasaporte IS NULL OR personas.pasaporte = p_pasaporte)
  AND (p_id_temporal IS NULL OR pacientes.identificacion_temporal = p_id_temporal)
    LIMIT 1;

IF FOUND THEN
            -- Actualizar datos si hay cambios
UPDATE personas
SET cedula = COALESCE(p_cedula, cedula),
    pasaporte = COALESCE(p_pasaporte, pasaporte),
    nombre = COALESCE(p_nombre, nombre),
    apellido1 = COALESCE(p_apellido1, apellido1),
    apellido2 = COALESCE(p_apellido2, apellido2),
    fecha_nacimiento = COALESCE(p_fecha_nacimiento, fecha_nacimiento),
    sexo = COALESCE(p_sexo, sexo),
    telefono = COALESCE(p_telefono, telefono),
    correo = COALESCE(p_correo, correo),
    direccion = COALESCE(v_id_direccion, direccion)
WHERE id = v_id_paciente
  AND (coalesce(cedula, '') IS DISTINCT FROM coalesce(p_cedula, '')
    OR coalesce(pasaporte, '') IS DISTINCT FROM coalesce(p_pasaporte, '')
    OR coalesce(nombre, '') IS DISTINCT FROM coalesce(p_nombre, '')
    OR coalesce(apellido1, '') IS DISTINCT FROM coalesce(p_apellido1, '')
    OR coalesce(apellido2, '') IS DISTINCT FROM coalesce(p_apellido2, '')
    OR coalesce(fecha_nacimiento::TEXT, '') IS DISTINCT FROM coalesce(p_fecha_nacimiento::TEXT, '')
    OR coalesce(sexo, '') IS DISTINCT FROM coalesce(p_sexo, '')
    OR coalesce(telefono, '') IS DISTINCT FROM coalesce(p_telefono, '')
    OR coalesce(correo, '') IS DISTINCT FROM coalesce(p_correo, '')
    OR coalesce(direccion::TEXT, '') IS DISTINCT FROM coalesce(v_id_direccion::TEXT, ''));

UPDATE pacientes
SET identificacion_temporal = COALESCE(p_id_temporal, identificacion_temporal)
WHERE id = v_id_paciente
  AND coalesce(identificacion_temporal, '') IS DISTINCT FROM coalesce(p_id_temporal, '');
ELSE
            -- Insertar nueva persona y paciente
SELECT gen_random_uuid() INTO v_id_paciente;

INSERT INTO personas(id, cedula, pasaporte, nombre, apellido1, apellido2,
                     fecha_nacimiento, sexo, telefono, correo, estado, direccion)
VALUES (v_id_paciente, p_cedula, p_pasaporte, p_nombre, p_apellido1, p_apellido2,
        p_fecha_nacimiento, p_sexo, p_telefono, p_correo, p_estado, v_id_direccion);

INSERT INTO pacientes(id, identificacion_temporal)
VALUES (v_id_paciente, p_id_temporal);

END IF;

EXCEPTION WHEN OTHERS THEN
        ROLLBACK;
END;
RETURN v_id_paciente;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_vacunas_insertar_entidad_sede(
    p_nombre TEXT,
    p_dependencia TEXT,
    p_correo TEXT DEFAULT NULL,
    p_telefono TEXT DEFAULT NULL,
    p_estado TEXT DEFAULT NULL,
    p_direccion TEXT DEFAULT NULL,
    p_distrito TEXT DEFAULT NULL
)
    LANGUAGE plpgsql
AS $$
DECLARE
v_id_direccion UUID;
    v_id_entidad UUID;
BEGIN
    -- Validación: si uno de los dos (dirección o distrito) viene sin el otro
    IF (p_direccion IS NOT NULL AND p_distrito IS NULL) OR
       (p_direccion IS NULL AND p_distrito IS NOT NULL) THEN
        RAISE EXCEPTION 'Debe especificar ambos campos: dirección y distrito o ninguno.';
END IF;

    -- Estado por defecto
    IF p_estado IS NULL THEN
        p_estado := 'NO_VALIDADO';
END IF;

    -- Obtener o insertar dirección
    v_id_direccion := obtener_o_insertar_direccion(p_direccion, p_distrito);

    -- Generar ID entidad
    v_id_entidad := gen_random_uuid();

    -- Insertar en entidades
INSERT INTO entidades (
    id, nombre, dependencia, correo, telefono, direccion, estado
) VALUES (v_id_entidad, p_nombre, p_dependencia, p_correo, p_telefono, v_id_direccion, p_estado);

-- Insertar en sedes
INSERT INTO sedes (id)
VALUES (v_id_entidad);
END;
$$;

CREATE OR REPLACE FUNCTION sp_vacunas_insert_dosis(
    IN fecha_aplicacion_dosis TIMESTAMP,
    IN numero_dosis_dosis CHAR(2),
    IN uuid_paciente UUID DEFAULT NULL,
    IN cedula_paciente VARCHAR(15) DEFAULT NULL,
    IN pasaporte_paciente VARCHAR(20) DEFAULT NULL,
    IN id_temporal VARCHAR(50) DEFAULT NULL,
    IN uuid_vacuna UUID DEFAULT NULL,
    IN nombre_vacuna VARCHAR(100) DEFAULT NULL,
    IN uuid_sede UUID DEFAULT NULL,
    IN nombre_sede VARCHAR(100) DEFAULT NULL,
    IN lote_dosis VARCHAR(10) DEFAULT NULL,
    IN uuid_doctor UUID DEFAULT NULL,
    IN idoneidad_doctor VARCHAR(20) DEFAULT NULL
)
    RETURNS UUID
    LANGUAGE plpgsql
AS $$
DECLARE
v_id_dosis UUID := gen_random_uuid();
BEGIN
    -- Validaciones básicas
    IF nombre_vacuna IS NULL AND uuid_vacuna IS NULL THEN
        RAISE EXCEPTION 'Debe especificar la vacuna por uuid o nombre.';
END IF;

    IF uuid_sede IS NULL AND nombre_sede IS NULL THEN
        RAISE EXCEPTION 'Debe especificar la sede por uuid o nombre.';
END IF;

    IF cedula_paciente IS NULL AND pasaporte_paciente IS NULL AND id_temporal IS NULL THEN
        RAISE EXCEPTION 'Debe especificar una identificación válida.';
END IF;

    -- Buscar uuid de vacuna si se proporciona el nombre
    IF nombre_vacuna IS NOT NULL AND uuid_vacuna IS NULL THEN
SELECT id INTO uuid_vacuna FROM vacunas WHERE nombre = nombre_vacuna;
IF uuid_vacuna IS NULL THEN
            RAISE EXCEPTION 'No se encontró ninguna vacuna con el nombre proporcionado.';
END IF;
END IF;

    -- Buscar uuid de sede si se proporciona el nombre
    IF nombre_sede IS NOT NULL AND uuid_sede IS NULL THEN
SELECT s.id INTO uuid_sede
FROM entidades e
         JOIN sedes s ON e.id = s.id
WHERE e.nombre = nombre_sede;

IF uuid_sede IS NULL THEN
            RAISE EXCEPTION 'No se encontró ninguna sede con el nombre proporcionado.';
END IF;
END IF;

    -- Verificación existencia vacuna y sede
    IF NOT EXISTS (SELECT 1 FROM vacunas WHERE id = uuid_vacuna) THEN
        RAISE EXCEPTION 'La vacuna especificada no existe.';
END IF;

    IF NOT EXISTS (SELECT 1 FROM sedes WHERE id = uuid_sede) THEN
        RAISE EXCEPTION 'La sede especificada no existe.';
END IF;

    -- Verificar si el paciente existe y extraer UUID si es necesario
    IF NOT EXISTS (
        SELECT 1
        FROM personas p
                 JOIN pacientes pa ON p.id = pa.id
        WHERE (uuid_paciente IS NULL OR pa.id = uuid_paciente)
          AND (cedula_paciente IS NULL OR cedula_paciente = p.cedula)
          AND (pasaporte_paciente IS NULL OR pasaporte_paciente = p.pasaporte)
          AND (id_temporal IS NULL OR identificacion_temporal = pa.identificacion_temporal)
    ) THEN
        RAISE EXCEPTION 'El paciente no existe.';
END IF;

    IF uuid_paciente IS NULL THEN
SELECT pa.id INTO uuid_paciente
FROM personas p
         JOIN pacientes pa ON p.id = pa.id
WHERE (cedula_paciente IS NULL OR cedula_paciente = p.cedula)
  AND (pasaporte_paciente IS NULL OR pasaporte_paciente = p.pasaporte)
  AND (id_temporal IS NULL OR identificacion_temporal = pa.identificacion_temporal);
END IF;

    -- Verificar doctor
    IF EXISTS (
        SELECT 1 FROM doctores
        WHERE (uuid_doctor IS NULL OR id = uuid_doctor)
          AND (idoneidad_doctor IS NULL OR idoneidad_doctor = doctores.idoneidad)
    ) THEN
        IF uuid_doctor IS NULL THEN
SELECT id INTO uuid_doctor
FROM doctores
WHERE (idoneidad_doctor IS NULL OR idoneidad_doctor = doctores.idoneidad);
END IF;
ELSE
        uuid_doctor := NULL;
END IF;

    -- Inserción
INSERT INTO dosis (id, paciente, fecha_aplicacion, numero_dosis, vacuna, sede, lote, doctor)
VALUES (v_id_dosis, uuid_paciente, fecha_aplicacion_dosis, numero_dosis_dosis, uuid_vacuna, uuid_sede, lote_dosis, uuid_doctor);
RETURN v_id_dosis;

EXCEPTION
    WHEN OTHERS THEN
        RAISE;
END;
$$;
