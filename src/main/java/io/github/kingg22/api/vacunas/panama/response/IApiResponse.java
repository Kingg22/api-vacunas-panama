package io.github.kingg22.api.vacunas.panama.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * Generic interface for the response format of the API
 * Extends {@link Serializable} to ensure serialization of object.
 * Designed for error codes or status or warnings are for the programmer (client) and the messages for the end user.
 *
 * @param <S> data type for codes or keys (e.g. status code, error code).
 * @param <T> data type for complex messages (e.g. status message, data value). Recommend using serializable data type
 */
public interface IApiResponse<S extends Serializable, T extends Serializable>
        extends Serializable, IApiContentResponse {
    /**
     * Adds an HTTP status code to the response.
     * @param httpStatus {@link HttpStatus} object representing the HTTP status.
     */
    void addStatusCode(HttpStatus httpStatus);

    /**
     * Adds a status to the response with a key-value pair.
     * @param key   the status code or key.
     * @param value the status message or additional information.
     */
    void addStatus(S key, T value);

    /**
     * Adds a status with a code and message in string format.
     * @param key     the status code or key.
     * @param message the message associated with the status.
     */
    void addStatus(S key, String message);

    /**
     * Adds a status message directly, without an associated code.
     * @param message the message to add.
     */
    void addStatus(String message);

    /**
     * Adds metadata to the response with a specified key-value pair.
     * @param key   metadata key.
     * @param value metadata value.
     */
    void addMetadata(String key, T value);

    /**
     * Adds a map of data entries to the response.
     * @param data a map of key-value pairs to add to the response data.
     */
    void addData(Map<S, T> data);

    /**
     * Adds multiple errors to the response.
     * @param errors a list of error messages or error objects.
     */
    void addErrors(List<?> errors);

    /**
     * Adds multiple warnings to the response.
     * @param warnings a list of warning messages or warning objects.
     */
    void addWarnings(List<?> warnings);

    /**
     * Gets the HTTP status code for the response.
     * @return an integer representing the HTTP status code.
     * @throws NullPointerException if the status code has not been set.
     */
    @JsonIgnore
    int getStatusCode() throws NullPointerException;
}
