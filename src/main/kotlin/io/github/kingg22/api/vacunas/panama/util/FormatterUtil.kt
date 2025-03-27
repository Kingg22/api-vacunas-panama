package io.github.kingg22.api.vacunas.panama.util

import jakarta.validation.constraints.NotNull

class FormatterUtil private constructor() {
    companion object {
        private val log = logger()
        private val INCORRECT_PATTERN =
            "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$".toPattern()
        private val CORRECT_PATTERN =
            "^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{4})-(\\d{6})$".toPattern()
        private val INCORRECT_PATTERN_RI =
            "^(RN(\\d{1,2}?)?)-(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$".toPattern()
        private val CORRECT_PATTERN_RI =
            "^(RN(\\d{1,2}?)?)-(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{4})-(\\d{6})$".toPattern()
        private val CORREO_REGEX =
            @Suppress("ktlint:standard:max-line-length")
            "^[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\$".toRegex()

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
         * Identifies if the data given is a cédula, pasaporte or correo
         *
         * @param identifier data to be identified.
         * @return [FormatterResult]
         */
        @JvmStatic
        fun formatToSearch(identifier: @NotNull String): FormatterResult {
            log.debug("Received a data to identifier type. Data: {}", identifier)
            var cedula: String? = null
            val pasaporte = if (identifier.matches("^[A-Z0-9]{5,20}$".toRegex())) identifier else null
            val correo = if (identifier.matches(CORREO_REGEX)) identifier else null

            try {
                cedula = formatCedula(identifier)
            } catch (exception: IllegalArgumentException) {
                log.debug("identifier is not a cedula")
            }
            log.debug(
                "Results: (Cédula: {}, pasaporte: {}, correo: {})",
                cedula,
                pasaporte,
                correo,
            )
            return FormatterResult(cedula, pasaporte, correo)
        }

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
    }

    @JvmRecord
    data class FormatterResult @JvmOverloads constructor(
        val cedula: String? = null,
        val pasaporte: String? = null,
        val correo: String? = null,
    ) {
        fun getIdentifier() = when {
            cedula != null -> cedula
            pasaporte != null -> pasaporte
            correo != null -> correo
            else -> throw IllegalArgumentException("Unknown identifier")
        }

        fun getTypeIdentifier() = when {
            pasaporte != null -> ResultType.PASAPORTE
            correo != null -> ResultType.CORREO
            cedula != null -> ResultType.CEDULA
            else -> throw IllegalArgumentException("Unknown identifier type")
        }

        enum class ResultType { CEDULA, PASAPORTE, CORREO }
    }
}
