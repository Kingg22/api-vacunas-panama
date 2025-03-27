package io.github.kingg22.api.vacunas.panama.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Interface used to transport information between layers of the API. This interface extends {@link Serializable} to
 * ensure that implementations can be easily serialized. Additionally, it's designed to be inherited by
 * {@link IApiResponse}, allowing addition information directly to the final API response. The purpose is flexibility
 * and decoupling within the application architecture.
 */
public interface IApiContentResponse extends Serializable {
    /**
     * Adds a data entry to the response with a specified key-value pair.
     *
     * @param key data key.
     * @param value data value.
     */
    void addData(String key, Serializable value);

    /**
     * Adds an error message with a specific code to the response.
     *
     * @param code the error code.
     * @param message the error message for the client or end user.
     */
    void addError(String code, String message);

    /**
     * Adds an error with a predefined code from {@link ApiResponseCode}.
     *
     * @param code predefined error code.
     * @param message the error message for the client or end user.
     */
    void addError(ApiResponseCode code, String message);

    /**
     * Adds an error with a specific property and message.
     *
     * @param code the error code.
     * @param property property associated with the error.
     * @param message message providing additional details.
     */
    void addError(String code, String property, String message);

    /**
     * Adds an error with a predefined {@link ApiResponseCode}, a property, and a message.
     *
     * @param code predefined error code.
     * @param property property associated with the error.
     * @param message message providing additional details.
     */
    void addError(ApiResponseCode code, String property, String message);

    /**
     * Adds a warning with a specific code and message.
     *
     * @param code the warning code.
     * @param message the warning message.
     */
    void addWarning(String code, String message);

    /**
     * Adds a warning with a predefined {@link ApiResponseCode} and message.
     *
     * @param code predefined warning code.
     * @param message the warning message.
     */
    void addWarning(ApiResponseCode code, String message);

    /**
     * Adds a warning with a predefined {@link ApiResponseCode}, a property, and a message.
     *
     * @param apiResponseCode predefined warning code.
     * @param property property associated with the warning.
     * @param message additional warning details.
     */
    void addWarning(ApiResponseCode apiResponseCode, String property, String message);

    /**
     * Checks if the response contains any errors.
     *
     * @return true if there are errors in the response, false otherwise.
     */
    boolean hasErrors();

    /**
     * Checks if the response contains any warnings.
     *
     * @return true if there are warnings in the response, false otherwise.
     */
    boolean hasWarnings();

    /**
     * Retrieves a list of warnings in the response.
     *
     * @return a list of warning objects.
     */
    List<? extends Serializable> getWarnings();

    /**
     * Retrieves a list of error objects in the response.
     *
     * @return a list of error objects.
     */
    List<? extends Serializable> getErrors();

    /**
     * Retrieves data associated with the response.
     *
     * @return a map containing data key-value pairs.
     */
    Map<String, Serializable> getData();
}
