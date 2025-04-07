package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.common.dto.IdNombreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RestoreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiError
import jakarta.validation.constraints.NotNull
import org.springframework.security.core.Authentication
import java.io.Serializable
import java.util.Optional
import java.util.UUID

/**
 * Service interface for managing users and their associated authentication and profile data.
 *
 * This service provides user lifecycle operations such as registration, password management,
 * role/authority validation, and profile access. It also coordinates with the token service
 * to establish login sessions and manage issued JWT tokens.
 */
interface UsuarioManagementService {

    /**
     * Retrieves a user by a given identifier (could be username, email, or ID depending on logic).
     *
     * @param identifier User identifier.
     * @return Optional containing the user if found.
     */
    fun getUsuario(identifier: String): Optional<Usuario>

    /**
     * Registers a new user using the provided DTO.
     *
     * @param registerUserDto Registration details.
     * @return API response indicating result and any errors.
     */
    fun createUser(registerUserDto: RegisterUserDto): ApiContentResponse

    /**
     * Changes the password of a user using the provided restore DTO.
     *
     * @param restoreDto DTO containing current and new password data.
     * @return API response with result or validation errors.
     */
    fun changePassword(restoreDto: RestoreDto): ApiContentResponse

    /**
     * Validates that the authenticated user has permission to assign the specified roles or authorities.
     *
     * @param usuarioDto DTO containing roles to assign.
     * @param authentication Authenticated user context.
     * @return List of API errors if validation fails.
     */
    fun validateAuthoritiesRegister(
        @NotNull usuarioDto: UsuarioDto,
        @NotNull authentication: Authentication,
    ): List<ApiError>

    /**
     * Generates access and refresh tokens for the specified user ID.
     *
     * @param id UUID of the user.
     * @return Map with the generated tokens.
     */
    fun generateTokens(id: UUID): Map<String, Serializable>

    /**
     * Retrieves all roles or authorities available, returning their IDs and names.
     *
     * @return List of ID-name DTOs representing permissions.
     */
    fun getIdNombrePermisos(): List<IdNombreDto>

    /**
     * Retrieves all defined roles within the system, returning their IDs and names.
     *
     * @return List of ID-name DTOs representing roles.
     */
    fun getIdNombreRoles(): List<IdNombreDto>

    /**
     * Retrieves the complete profile data for a given user.
     *
     * @param id UUID of the user.
     * @return Map with profile fields and values.
     */
    fun getProfile(id: UUID): Map<String, Serializable>

    /**
     * Sets and returns essential login data when a user signs in.
     *
     * @param id UUID of the user.
     * @return Map containing login-related data (e.g., tokens, roles).
     */
    fun setLoginData(id: UUID): Map<String, Serializable>

    /**
     * Updates the timestamp of the user's last activity or usage.
     *
     * @param id UUID of the user.
     */
    fun updateLastUsed(id: UUID)
}
