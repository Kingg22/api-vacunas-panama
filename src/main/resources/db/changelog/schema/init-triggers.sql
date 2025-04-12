-- Trigger para asignar automáticamente la región a las sedes cuando coincide con la provincia y/o distrito
CREATE OR REPLACE FUNCTION tr_sedes_insert_region_fn()
    RETURNS TRIGGER AS
$$
DECLARE
prov_id     INTEGER;
    distrito_id INTEGER;
BEGIN
SELECT dd.provincia, d.distrito
INTO prov_id, distrito_id
FROM entidades e
         INNER JOIN direcciones d ON e.direccion = d.id
         INNER JOIN distritos dd ON d.distrito = dd.id
WHERE e.id = NEW.id;

NEW.region := CASE
                      WHEN prov_id = 1 THEN 'Bocas del Toro'
                      WHEN prov_id = 2 THEN 'Coclé'
                      WHEN prov_id = 3 THEN 'Colón'
                      WHEN prov_id = 4 THEN 'Chiriquí'
                      WHEN prov_id = 5 OR prov_id = 11 OR prov_id = 16
                          THEN 'Darién y la comarca Embera Waunán y Wargandí'
                      WHEN prov_id = 6 THEN 'Herrera'
                      WHEN prov_id = 7 THEN 'Los Santos'
                      WHEN prov_id = 8 AND distrito_id = 53 THEN 'San Miguelito'
                      WHEN prov_id = 8 AND distrito_id <> 53 THEN 'Panamá Norte/Este/Metro'
                      WHEN prov_id = 9 THEN 'Veraguas'
                      WHEN prov_id = 10 THEN 'Kuna Yala'
                      WHEN prov_id = 12 THEN 'Ngabe Buglé'
                      WHEN prov_id = 13 AND distrito_id <> 79 THEN 'Panamá Oeste'
                      WHEN prov_id = 13 AND distrito_id = 79 THEN 'Arraiján'
                      ELSE 'Por registrar'
END;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_sedes_insert_region
    BEFORE INSERT OR UPDATE
                                   ON sedes
                                   FOR EACH ROW
                                   EXECUTE FUNCTION tr_sedes_insert_region_fn();

-- trigger para mantener registro de cambios
CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW IS DISTINCT FROM OLD THEN
        NEW.updated_at := CURRENT_TIMESTAMP;
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_usuarios_updated
    BEFORE UPDATE
                         ON usuarios
                         FOR EACH ROW
                         EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_doctores_updated
    BEFORE UPDATE
                      ON doctores
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_pacientes_updated
    BEFORE UPDATE
                      ON pacientes
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_sedes_updated
    BEFORE UPDATE
                      ON sedes
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_rol_updated
    BEFORE UPDATE
                      ON roles
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_permisos_updated
    BEFORE UPDATE
                      ON permisos
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_rol_permisos_updated
    BEFORE UPDATE
                      ON roles_permisos
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_vacunas_updated
    BEFORE UPDATE
                      ON vacunas
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_usuario_rol_updated
    BEFORE UPDATE
                      ON usuarios_roles
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_direcciones_updated
    BEFORE UPDATE
                      ON direcciones
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_dosis_updated
    BEFORE UPDATE
                      ON dosis
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE TRIGGER tr_fabricantes_updated
    BEFORE UPDATE
                      ON fabricantes
                      FOR EACH ROW
                      EXECUTE FUNCTION set_updated_at();

-- Trigger que le da formato a la cédula panameña al insertar y asigna la edad de la persona
CREATE OR REPLACE FUNCTION fn_personas_format_insert()
    RETURNS TRIGGER AS
$$
DECLARE
inicio TEXT;
    libro  TEXT;
    tomo   TEXT;
BEGIN
    IF NEW.fecha_nacimiento IS NOT NULL THEN
        NEW.edad := DATE_PART('year', AGE(CURRENT_DATE, NEW.fecha_nacimiento));
END IF;

    IF NEW.cedula IS NOT NULL THEN
        -- Validar formato general
        IF NOT (
            NEW.cedula ~ '^(PE|E|N|[2-9](AV|PI)?|1[0-3]?(AV|PI)?)-\d{1,4}-\d{1,6}$'
            ) THEN
            RAISE EXCEPTION 'Cédula no cumple el formato: %', NEW.cedula;
END IF;

        -- Formateo individual
        inicio := split_part(NEW.cedula, '-', 1);
        libro := lpad(split_part(NEW.cedula, '-', 2), 4, '0');
        tomo := lpad(split_part(NEW.cedula, '-', 3), 6, '0');

        NEW.cedula := inicio || '-' || libro || '-' || tomo;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_personas_format_insert
    BEFORE INSERT
    ON personas
    FOR EACH ROW
EXECUTE FUNCTION fn_personas_format_insert();

-- Trigger que le da formato a identificación temporal con cédulas de la madre
CREATE OR REPLACE FUNCTION fn_pacientes_format_insert()
    RETURNS TRIGGER AS
$$
DECLARE
inicio        TEXT;
    libro         TEXT;
    tomo          TEXT;
    cedula_madre  TEXT;
    recien_nacido TEXT;
BEGIN
    IF NEW.identificacion_temporal IS NOT NULL AND NEW.identificacion_temporal ~ '^RN[0-9]*-' THEN
        recien_nacido := split_part(NEW.identificacion_temporal, '-', 1);
        cedula_madre := split_part(NEW.identificacion_temporal, '-', 2);

        -- Validación de formato
        IF NOT (cedula_madre ~ '^(PE|E|N|[2-9](AV|PI)?|1[0-3]?(AV|PI)?)-\d{1,4}-\d{1,6}$') THEN
            RAISE EXCEPTION 'Cédula madre no cumple el formato: %', cedula_madre;
END IF;

        inicio := split_part(cedula_madre, '-', 1);
        libro := lpad(split_part(cedula_madre, '-', 2), 4, '0');
        tomo := lpad(split_part(cedula_madre, '-', 3), 6, '0');

        NEW.identificacion_temporal := recien_nacido || '-' || inicio || '-' || libro || '-' || tomo;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_pacientes_format_insert
    BEFORE INSERT
    ON pacientes
    FOR EACH ROW
EXECUTE FUNCTION fn_pacientes_format_insert();

-- Trigger para evaluar la secuencia de dosis
CREATE OR REPLACE FUNCTION trg_validate_dosis_sequence()
    RETURNS TRIGGER AS
$$
DECLARE
num_dosis   TEXT;
    id_paciente UUID;
    id_vacuna   UUID;
BEGIN
    num_dosis := UPPER(TRIM(NEW.numero_dosis));
    id_paciente := NEW.paciente;
    id_vacuna := NEW.vacuna;

    -- Validar dosis previas en secuencia
    IF num_dosis = 'P' AND EXISTS (SELECT 1
                                   FROM dosis
                                   WHERE paciente = id_paciente
                                     AND vacuna = id_vacuna) THEN
        RAISE EXCEPTION 'La dosis P solo puede aplicarse antes de cualquier otra dosis de esa vacuna';
END IF;

    IF num_dosis = '2' AND NOT EXISTS (SELECT 1
                                       FROM dosis
                                       WHERE paciente = id_paciente
                                         AND vacuna = id_vacuna
                                         AND numero_dosis = '1') THEN
        RAISE EXCEPTION 'Debe existir la dosis 1 antes de aplicar la dosis 2';
END IF;

    IF num_dosis = '3' AND NOT EXISTS (SELECT 1
                                       FROM dosis
                                       WHERE paciente = id_paciente
                                         AND vacuna = id_vacuna
                                         AND numero_dosis = '2') THEN
        RAISE EXCEPTION 'Debe existir la dosis 2 antes de aplicar la dosis 3';
END IF;

    IF num_dosis = 'R1' AND NOT EXISTS (SELECT 1
                                        FROM dosis
                                        WHERE paciente = id_paciente
                                          AND vacuna = id_vacuna
                                          AND numero_dosis = '1') THEN
        RAISE EXCEPTION 'Debe existir la dosis 1 antes de aplicar la dosis R1';
END IF;

    IF num_dosis = 'R2' AND NOT EXISTS (SELECT 1
                                        FROM dosis
                                        WHERE paciente = id_paciente
                                          AND vacuna = id_vacuna
                                          AND numero_dosis IN ('R1', '1')) THEN
        RAISE EXCEPTION 'Debe existir la dosis R1 o 1 antes de aplicar la dosis R2';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_validate_dosis_sequence
    BEFORE INSERT
    ON dosis
    FOR EACH ROW
EXECUTE FUNCTION trg_validate_dosis_sequence();
