ALTER TABLE distritos
    ADD CONSTRAINT ck_distritos_provincias CHECK (
        (provincia = 0 AND nombre = 'Por registrar') OR
        (provincia = 1 AND nombre IN ('Almirante', 'Bocas del Toro', 'Changuinola', 'Chiriquí Grande')) OR
        (provincia = 2 AND nombre IN ('Aguadulce', 'Antón', 'La Pintada', 'Natá', 'Olá', 'Penonomé')) OR
        (provincia = 3 AND
         nombre IN ('Chagres', 'Colón', 'Donoso', 'Portobelo', 'Santa Isabel', 'Omar Torrijos Herrera')) OR
        (provincia = 4 AND nombre IN
                           ('Alanje', 'Barú', 'Boquerón', 'Boquete', 'Bugaba', 'David', 'Dolega', 'Gualaca', 'Remedios',
                            'Renacimiento', 'San Félix', 'San Lorenzo', 'Tierras Altas', 'Tolé')) OR
        (provincia = 5 AND nombre IN ('Chepigana', 'Pinogana', 'Santa Fe', 'Guna de Wargandí')) OR
        (provincia = 6 AND nombre IN ('Chitré', 'Las Minas', 'Los Pozos', 'Ocú', 'Parita', 'Pesé', 'Santa María')) OR
        (provincia = 7 AND
         nombre IN ('Guararé', 'Las Tablas', 'Los Santos', 'Macaracas', 'Pedasí', 'Pocrí', 'Tonosí')) OR
        (provincia = 8 AND nombre IN ('Balboa', 'Chepo', 'Chimán', 'Panamá', 'San Miguelito', 'Taboga')) OR
        (provincia = 9 AND nombre IN ('Atalaya', 'Calobre', 'Cañazas', 'La Mesa', 'Las Palmas', 'Mariato', 'Montijo',
                                      'Río de Jesús', 'San Francisco', 'Santa Fe', 'Santiago', 'Soná')) OR
        (provincia = 11 AND nombre IN ('Cémaco', 'Sambú')) OR
        (provincia = 12 AND nombre IN
                            ('Besikó', 'Jirondai', 'Kankintú', 'Kusapín', 'Mironó', 'Müna', 'Nole Duima', 'Ñürüm',
                             'Santa Catalina', 'Calovébora')) OR
        (provincia = 13 AND nombre IN ('Arraiján', 'Capira', 'Chame', 'La Chorrera', 'San Carlos')) OR
        (provincia = 14 AND nombre IN ('Naso Tjër Di')) OR
        (provincia = 17 AND nombre IN ('Extranjero')) OR
        (provincia IS NULL AND nombre IS NULL)
        );
ALTER TABLE dosis
    ADD CONSTRAINT ck_dosis_numero_dosis CHECK (numero_dosis IN ('1', '2', '3', 'R', 'R1', 'R2', 'P'));

ALTER TABLE entidades
    -- Validación básica de correo
    ADD CONSTRAINT ck_entidades_correo CHECK (
        correo IS NULL OR
        (correo ~* '^[^@]+@[^@]+\.[a-z]{2,}$' AND length(correo) >= 5)),

    -- Teléfono con formato internacional, ejemplo: +50760001234
    ADD CONSTRAINT ck_entidades_telefono CHECK (
        telefono IS NULL OR
            -- Al menos 4 dígitos luego del +
        (telefono ~ '^\+[0-9]{4,}$')),

    -- Dependencia controlada por catálogo fijo
    ADD CONSTRAINT ck_entidades_dependencia CHECK (
        dependencia IS NULL OR
        dependencia IN ('CSS', 'MINSA', 'PRIVADA', 'POR_REGISTRAR')),

    -- Estado restringido
    ADD CONSTRAINT ck_entidades_estado CHECK (estado IN ('ACTIVO', 'NO_VALIDADO', 'INACTIVO', 'DESACTIVADO'));

ALTER TABLE fabricantes
    ADD CONSTRAINT ck_fabricantes_licencia CHECK (licencia LIKE '%/DNFD'),
    -- Validación básica de correo
    ADD CONSTRAINT ck_fabricantes_contacto_correo CHECK (
        contacto_correo IS NULL OR
        (contacto_correo ~* '^[^@]+@[^@]+\.[a-z]{2,}$' AND length(contacto_correo) >= 5)),

    -- Teléfono con formato internacional, ejemplo: +50760001234
    ADD CONSTRAINT ck_fabricantes_contacto_telefono CHECK (
        contacto_telefono IS NULL OR
            -- Al menos 4 dígitos luego del +
        (contacto_telefono ~ '^\+[0-9]{4,}$'));

ALTER TABLE pacientes
    ADD CONSTRAINT ck_pacientes_id_temporal CHECK (
        -- Opción 1: No identificado
        identificacion_temporal ~ '^NI-\w+$'
            OR
            -- Opción 2: Recién nacidos
        (
            (
                -- Debe empezar con RN-, RN1-, RN2-, ..., RN99-
                identificacion_temporal ~ '^RN([1-9][0-9]?)?-'

                    -- Y contener uno de los tipos válidos: -PE-, -E-, -N-, -1-, ... -13-
                    AND identificacion_temporal ~ '-(PE|E|N|[1-13])-'

                    -- y tener un libro de 4 dígitos
                    AND identificacion_temporal ~ '-\d{4}-'

                    -- y terminar con tomo de 6 dígitos (ej.: 000123)
                    AND identificacion_temporal ~ '-\d{6}$'
                )
            )
        );

ALTER TABLE personas
    ADD CONSTRAINT ck_personas_sexo CHECK (sexo IN ('M', 'F', 'X')),

    ADD CONSTRAINT ck_personas_telefono CHECK (
        telefono IS NULL OR (
            telefono ~ '^\+[0-9]{4,}$')),

    ADD CONSTRAINT ck_fabricantes_contacto_correo CHECK (
        correo IS NULL OR
        (correo ~* '^[^@]+@[^@]+\.[a-z]{2,}$' AND length(correo) >= 5)),

    ADD CONSTRAINT ck_personas_cedula CHECK (
        cedula IS NULL OR (
        cedula ~ '^(PE|E|N|[2-9](AV|PI)?|1[0-3](AV|PI)?)-\d{1,4}-\d{1,6}$')),

    ADD CONSTRAINT ck_personas_pasaporte CHECK (
        pasaporte IS NULL OR (pasaporte ~ '^[A-Z0-9]{5,}$')),

    ADD CONSTRAINT ck_personas_fecha_nacimiento CHECK (
        fecha_nacimiento IS NULL OR fecha_nacimiento <= CURRENT_DATE),

    ADD CONSTRAINT ck_personas_estado CHECK (estado IN
                                             ('ACTIVO', 'NO_VALIDADO', 'FALLECIDO', 'INACTIVO', 'DESACTIVADO'));

ALTER TABLE provincias
    ADD CONSTRAINT ck_provincia_existe CHECK (
        nombre IN (
                   'Por registrar', -- Problemas o nueva provincia sin registrar aún
                   'Bocas del Toro', -- 1
                   'Coclé', -- 2
                   'Colón', -- 3
                   'Chiriquí', -- 4
                   'Darién', -- 5
                   'Herrera', -- 6
                   'Los Santos', -- 7
                   'Panamá', -- 8
                   'Veraguas', -- 9
                   'Guna Yala', -- 10
                   'Emberá-Wounaan', -- 11
                   'Ngäbe-Buglé', -- 12
                   'Panamá Oeste', -- 13
                   'Naso Tjër Di', -- 14
                   'Guna de Madugandí', -- 15
                   'Guna de Wargandí', -- 16
                   'Extranjero' -- 17
            )
        );

ALTER TABLE usuarios
    ADD CONSTRAINT ck_usuarios_clave CHECK (clave ~ '^\\$2[ayb]\\$[0-9]{2}\\$[A-Za-z0-9./]{22}$');

ALTER TABLE vacunas
    ADD CONSTRAINT ck_vacunas_dosis_maxima CHECK (dosis_maxima IN ('1', '2', '3', 'R', 'R1', 'R2', 'P')),
    ADD CONSTRAINT ck_vacunas_edad_minima CHECK (edad_minima_dias >= 0);

ALTER TABLE esquemas_vacunacion
    ADD CONSTRAINT ck_esquemas_vacunacion_dosis_maxima CHECK (numero_dosis IN ('1', '2', '3', 'R', 'R1', 'R2', 'P'));
