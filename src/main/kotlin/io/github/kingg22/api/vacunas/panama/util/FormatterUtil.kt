package io.github.kingg22.api.vacunas.panama.util

import jakarta.validation.constraints.NotNull

object FormatterUtil {
    private val log = logger()
    private val INCORRECT_PATTERN =
        "^(PE|E|N|[2-9](?:AV|PI)?|1[0-3]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$".toPattern()
    private val CORRECT_PATTERN =
        "^(PE|E|N|[2-9](?:AV|PI)?|1[0-3]?(?:AV|PI)?)-(\\d{4})-(\\d{6})$".toPattern()
    private val INCORRECT_PATTERN_RI =
        "^(RN(\\d{1,2}?)?)-(PE|E|N|[2-9](?:AV|PI)?|1[0-3]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$".toPattern()
    private val CORRECT_PATTERN_RI =
        "^(RN(\\d{1,2}?)?)-(PE|E|N|[2-9](?:AV|PI)?|1[0-3]?(?:AV|PI)?)-(\\d{4})-(\\d{6})$".toPattern()
    private val CORREO_REGEX =
        @Suppress("ktlint:standard:max-line-length", "kotlin:S5843")
        "^[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\$".toRegex()

    /**
     * Formats a given cédula to the correct format if needed.
     *
     * @param cedula The cédula string to be formatted.
     * @return The formatted cédula as a string.
     * @throws IllegalArgumentException if the cédula does not match any expected pattern.
     */
    @JvmStatic
    fun formatCedula(cedula: String): String {
        log.debug("Trying format cedula: {}", cedula)
        val incorrectMatcher = INCORRECT_PATTERN.matcher(cedula)
        val correctMatcher = CORRECT_PATTERN.matcher(cedula)

        if (!correctMatcher.matches() && incorrectMatcher.matches()) {
            val inicio = incorrectMatcher.group(1)
            val libro = String.format("%04d", incorrectMatcher.group(2).toInt())
            val tomo = String.format("%06d", incorrectMatcher.group(3).toInt())

            val cedulaFormat = "$inicio-$libro-$tomo"
            log.debug("cedula format successfully: {}", cedulaFormat)
            return cedulaFormat.trim()
        } else if (correctMatcher.matches()) {
            log.debug("cedula already in correct format: {}", cedula)
            return cedula.trim()
        }
        log.debug("cedula no match with any expected pattern: {}", cedula)
        throw IllegalArgumentException("cedula no match to expected pattern to format")
    }

    /**
     * Formats a temporary ID if needed.
     *
     * @param idTemporal The temporary ID string to be formatted.
     * @return The formatted ID as a string.
     */
    @JvmStatic
    fun formatIdTemporal(idTemporal: String): String {
        log.debug("Check if idTemporal need format: {}", idTemporal)
        val temporalPattern = "^NI-.+$".toPattern()
        val temporalMatcher = temporalPattern.matcher(idTemporal)

        return if (!temporalMatcher.matches()) {
            formatRI(idTemporal)
        } else {
            log.debug(
                "idTemporal don't need format because is NI: {}",
                idTemporal,
            )
            idTemporal.trim()
        }
    }

    /**
     * Identifies if the given identifier is a cédula, pasaporte, or correo.
     *
     * @param identifier The string to be identified.
     * @return [FormatterResult] containing the identified data type.
     */
    @JvmStatic
    fun formatToSearch(identifier: @NotNull String): FormatterResult {
        log.debug("Received a data to identifier type. Data: {}", identifier)
        var cedula: String? = null
        val pasaporte = if (identifier.matches("^[A-Z0-9]{5,20}$".toRegex())) identifier else null
        val correo = if (identifier.matches(CORREO_REGEX)) identifier else null

        try {
            cedula = formatCedula(identifier)
        } catch (e: IllegalArgumentException) {
            log.debug("identifier is not a cedula", e)
        }
        log.debug(
            "Results: (Cédula: {}, pasaporte: {}, correo: {})",
            cedula,
            pasaporte,
            correo,
        )
        return FormatterResult(cedula, pasaporte, correo)
    }

    /**
     * Formats a maternal cédula (RI) if needed.
     *
     * @param idRI The maternal cédula string to be formatted.
     * @return The formatted RI as a string.
     */
    @JvmStatic
    private fun formatRI(idRI: String): String {
        log.info("Trying format cedula of mother: {}", idRI)
        val incorrectRIMatcher = INCORRECT_PATTERN_RI.matcher(idRI)
        val correctRIMatcher = CORRECT_PATTERN_RI.matcher(idRI)

        return if (!correctRIMatcher.matches() && incorrectRIMatcher.matches()) {
            val prefix = incorrectRIMatcher.group(1)
            val libro = String.format("%04d", incorrectRIMatcher.group(4).toInt())
            val tomo = String.format("%04d", incorrectRIMatcher.group(5).toInt())

            val idRiFormat = "$prefix-${incorrectRIMatcher.group(3)}-$libro-$tomo"
            log.debug("cedula of mother format successfully: {}", idRiFormat)
            idRiFormat.trim()
        } else if (correctRIMatcher.matches()) {
            log.debug("cedula of mother already in correct format: {}", idRI)
            idRI.trim()
        } else {
            log.debug("id of RI no match with any expected pattern: {}", idRI)
            throw IllegalArgumentException("id of RI no match to expected pattern to format")
        }
    }

    /**
     * Data class that holds the result of a formatted identifier.
     * Only one of the fields (cedula, pasaporte, correo) can be non-empty.
     *
     * @property cedula The formatted cédula, if available.
     * @property pasaporte The formatted passport, if available.
     * @property correo The formatted email address, if available.
     * @throws IllegalArgumentException if more than one field is non-empty.
     */
    @JvmRecord
    data class FormatterResult(val cedula: String? = null, val pasaporte: String? = null, val correo: String? = null) {
        init {
            val filledCount = listOf(cedula, pasaporte, correo).count { !it.isNullOrBlank() }

            require(filledCount <= 1) {
                "Solo uno de los campos (cedula, pasaporte, correo) puede estar lleno."
            }
        }

        /**
         * Retrieves the identifier value from the available field.
         *
         * @return The non-null identifier.
         * @throws IllegalArgumentException if no valid identifier is found.
         */
        fun getIdentifier() = when {
            !cedula.isNullOrBlank() -> cedula
            !pasaporte.isNullOrBlank() -> pasaporte
            !correo.isNullOrBlank() -> correo
            else -> throw IllegalArgumentException("Unknown identifier")
        }

        /**
         * Determines the type of the identifier (CÉDULA, PASAPORTE, CORREO).
         *
         * @return The [ResultType] corresponding to the identifier.
         * @throws IllegalArgumentException if the identifier type is unknown.
         */
        fun getTypeIdentifier() = when {
            !pasaporte.isNullOrBlank() -> ResultType.PASAPORTE
            !correo.isNullOrBlank() -> ResultType.CORREO
            !cedula.isNullOrBlank() -> ResultType.CEDULA
            else -> throw IllegalArgumentException("Unknown identifier type")
        }

        /** Enumeration representing the possible types of identifiers. */
        enum class ResultType { CEDULA, PASAPORTE, CORREO }
    }
}
