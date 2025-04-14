package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RestoreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import org.springframework.security.core.Authentication
import java.util.Optional
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
     * @return Optional containing the user if found.
     */
    fun getUsuarioByIdentifier(identifier: String): Optional<UsuarioDto>

    /**
     * Retrieves a user by a given unique ID.
     *
     * @param id User ID.
     * @return Optional containing the user if found.
     */
    fun getUsuarioById(id: UUID): Optional<UsuarioDto>

    /**
     * Retrieves the complete profile data for a given user.
     *
     * @param id UUID of the user.
     * @return [ApiContentResponse] with data Map with profile fields and values.
     */
    fun getProfile(id: UUID): ApiContentResponse

    /**
     * Sets and returns essential login data when a user signs in.
     *
     * @param id UUID of the user.
     * @return [ApiContentResponse] with Map containing login-related data (e.g., tokens, roles).
     */
    fun getLogin(id: UUID): ApiContentResponse

    /**
     * Registers a new user using the provided DTO.
     *
     * @param registerUserDto Registration details.
     * @param authentication Optional if the user is authenticated to verify permissions.
     * @return API response indicating a result and any errors.
     */
    suspend fun createUser(registerUserDto: RegisterUserDto, authentication: Authentication? = null): ApiContentResponse

    fun createUser(usuarioDto: UsuarioDto, block: (Usuario) -> Unit = {})

    /**
     * Changes the password of a user using the provided restore DTO.
     *
     * @param restoreDto DTO containing current and new password data.
     * @return API response with result or validation errors.
     */
    suspend fun changePassword(restoreDto: RestoreDto): ApiContentResponse

    /**
     * Updates the timestamp of the user's last activity or usage.
     *
     * @param id UUID of the user.
     */
    fun updateLastUsed(id: UUID)
}
