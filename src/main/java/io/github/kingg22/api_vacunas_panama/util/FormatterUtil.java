package io.github.kingg22.api_vacunas_panama.util;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FormatterUtil {
    private static final Pattern INCORRECT_PATTERN = Pattern.compile("^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$");
    private static final Pattern CORRECT_PATTERN = Pattern.compile("^(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{4})-(\\d{6})$");
    private static final Pattern INCORRECT_PATTERN_RI = Pattern.compile("^(RN(\\d{1,2}?)?)-(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{1,4})-(\\d{1,6})$");
    private static final Pattern CORRECT_PATTERN_RI = Pattern.compile("^(RN(\\d{1,2}?)?)-(PE|E|N|[23456789](?:AV|PI)?|1[0123]?(?:AV|PI)?)-(\\d{4})-(\\d{6})$");

    private FormatterUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String formatCedula(String cedula) {
        log.debug("Trying format cedula: {}", cedula);
        Matcher incorrectMatcher = INCORRECT_PATTERN.matcher(cedula);
        Matcher correctMatcher = CORRECT_PATTERN.matcher(cedula);

        if (!correctMatcher.matches() && incorrectMatcher.matches()) {
            String inicio = incorrectMatcher.group(1);
            String libro = String.format("%04d", Integer.parseInt(incorrectMatcher.group(2)));
            String tomo = String.format("%06d", Integer.parseInt(incorrectMatcher.group(3)));

            String cedulaFormat = String.format("%s-%s-%s", inicio, libro, tomo);
            log.debug("cedula format successfully: {}", cedulaFormat);
            return cedulaFormat.trim();
        } else if (correctMatcher.matches()) {
            log.debug("cedula already in correct format: {}", cedula);
            return cedula.trim();
        }
        log.debug("cedula no match with any expected pattern: {}", cedula);
        throw new IllegalArgumentException("cedula no match to expected pattern to format");
    }

    public static String formatIdTemporal(String idTemporal) {
        log.debug("Check if idTemporal need format: {}", idTemporal);
        Pattern temporalPattern = Pattern.compile("^NI-.+$");
        Matcher temporalMatcher = temporalPattern.matcher(idTemporal);

        if (!temporalMatcher.matches()) {
            return formatRI(idTemporal);
        } else {
            log.debug("idTemporal don't need format because is NI: {}", idTemporal);
            return idTemporal.trim();
        }
    }

    /**
     * Identifies if the data given is a cédula, pasaporte or correo
     *
     * @param identifier data to be identified.
     * @return array with cédula, pasaporte and correo
     */
    public static String[] formatToSearch(@NotNull String identifier) {
        log.debug("Received a data to identifier type. Data: {}", identifier);
        String cedula = null;
        String pasaporte = identifier.matches("^[A-Z0-9]{5,20}$") ? identifier : null;
        String correo = identifier.matches("^_@_.__$") ? identifier : null;

        try {
            cedula = formatCedula(identifier);
        } catch (IllegalArgumentException exception) {
            log.debug("identifier is not a cedula");
        }
        log.debug("Results: (Cédula: {}, pasaporte: {}, correo: {})", cedula, pasaporte, correo);
        return new String[]{cedula, pasaporte, correo};
    }

    private static String formatRI(String idRI) {
        log.info("Trying format cedula of mother: {}", idRI);
        Matcher incorrectRIMatcher = INCORRECT_PATTERN_RI.matcher(idRI);
        Matcher correctRIMatcher = CORRECT_PATTERN_RI.matcher(idRI);

        if (!correctRIMatcher.matches() && incorrectRIMatcher.matches()) {
            String prefix = incorrectRIMatcher.group(1);
            String libro = String.format("%04d", Integer.parseInt(incorrectRIMatcher.group(4)));
            String tomo = String.format("%04d", Integer.parseInt(incorrectRIMatcher.group(5)));

            String idRiFormat = String.format("%s-%s-%s-%s", prefix, incorrectRIMatcher.group(3), libro, tomo);
            log.debug("cedula of mother format successfully: {}", idRiFormat);
            return idRiFormat.trim();
        } else if (correctRIMatcher.matches()) {
            log.debug("cedula of mother already in correct format: {}", idRI);
            return idRI.trim();
        }
        log.debug("id of RI no match with any expected pattern: {}", idRI);
        throw new IllegalArgumentException("id of RI no match to expected pattern to format");
    }

}
