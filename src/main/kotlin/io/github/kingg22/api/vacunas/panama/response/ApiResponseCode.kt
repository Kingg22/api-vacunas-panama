package io.github.kingg22.api.vacunas.panama.response

/**
 * Enumeration of standardized API response codes for consistent error and status reporting.
 *
 * These codes provide a centralized way to categorize and communicate different types of
 * responses or errors across the application.
 * They help in:
 * - Standardizing error communication
 * - Providing machine-readable error identification
 * - Supporting internationalization of error messages
 */
enum class ApiResponseCode {
    /** Resource or entity not found in the system or could not be located */
    NOT_FOUND,

    /** Signals that input data failed validation checks */
    VALIDATION_FAILED,

    /** User's password has been compromised */
    COMPROMISED_PASSWORD,

    /** Required information is missing */
    MISSING_INFORMATION,

    /** Identifier takes precedence over name */
    NAME_IGNORED_FOR_ID,

    /** Resource is already in use */
    ALREADY_IN_USE,

    /** Resource already exists. Suggests a duplicate resource creation attempt */
    ALREADY_EXISTS,

    /** Resource is already taken */
    ALREADY_TAKEN,

    /** Indicates insufficient access rights */
    PERMISSION_DENIED,

    /** Insufficient role privileges */
    INSUFFICIENT_ROLE_PRIVILEGES,

    /** Missing required role or permission */
    MISSING_ROLE_OR_PERMISSION,

    /** Invalid permission operation */
    INVALID_PERMISSION_OPERATION,

    /** Violation in role hierarchy */
    ROL_HIERARCHY_VIOLATION,

    /** Some information was ignored during processing */
    INFORMATION_IGNORED,

    /** API update is not supported */
    API_UPDATE_UNSUPPORTED,

    /** Operation is not idempotent */
    NON_IDEMPOTENCE,

    /** Deprecation warning for an API endpoint or feature */
    DEPRECATION_WARNING,

    /** Only if not have a more useful message, Internal Server Error */
    INTERNAL_SERVER_ERROR,
}
