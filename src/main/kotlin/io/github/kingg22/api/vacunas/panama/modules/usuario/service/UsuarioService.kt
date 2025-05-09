package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RestoreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.response.ActualApiResponse
import java.util.UUID

/**
 * Service interface for managing users and their associated authentication and profile data.
 *
 * This service provides user lifecycle operations such as registration, password management,
 * and profile access.
 */
interface UsuarioService {
    /**
     * Retrieves a user by a given identifier (could be username, email, or ID depending on logic).
     *
     * @param identifier User identifier.
     * @return The user if found, otherwise null.
     */
    suspend fun getUsuarioByIdentifier(identifier: String): UsuarioDto?

    /**
     * Retrieves a user by a given unique ID.
     *
     * @param id User ID.
     * @return The user if found, otherwise null.
     */
    suspend fun getUsuarioById(id: UUID): UsuarioDto?

    /**
     * Retrieves the complete profile data for a given user.
     *
     * @param id UUID of the user.
     * @return [ActualApiResponse] with data Map with profile fields and values.
     */
    suspend fun getProfile(id: UUID): ActualApiResponse

    /**
     * Sets and returns essential login data when a user signs in.
     *
     * @param id UUID of the user.
     * @return [ActualApiResponse] with Map containing login-related data (e.g., tokens, roles).
     */
    suspend fun getLogin(id: UUID): ActualApiResponse

    /**
     * Registers a new user using the provided DTO.
     *
     * @param registerUserDto Registration details.
     * @param scope Optional if the user is authenticated to verify permissions and roles.
     * @return API response indicating a result and any errors.
     */
    suspend fun createUser(registerUserDto: RegisterUserDto, scope: Set<String> = emptySet()): ActualApiResponse

    /**
     * Registers a new user.
     * _This is an internal function to [RegistrationStrategy]._
     *
     * @param usuarioDto Usuario DTO containing the user information.
     * @param personaId Optional if the user is linked to a persona.
     * @param fabricanteId Optional if the user is linked to fabricante.
     */
    suspend fun createUser(usuarioDto: UsuarioDto, personaId: UUID?, fabricanteId: UUID?): UsuarioDto

    /**
     * Changes the password of a user using the provided restore DTO.
     *
     * @param restoreDto DTO containing current and new password data.
     * @return API response with result or validation errors.
     */
    suspend fun changePassword(restoreDto: RestoreDto): ActualApiResponse

    /**
     * Updates the timestamp of the user's last activity or usage.
     *
     * @param id UUID of the user.
     */
    suspend fun updateLastUsed(id: UUID)
}
