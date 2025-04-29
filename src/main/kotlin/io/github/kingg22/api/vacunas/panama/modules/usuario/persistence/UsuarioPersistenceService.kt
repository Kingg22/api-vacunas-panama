package io.github.kingg22.api.vacunas.panama.modules.usuario.persistence

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Rol
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import java.util.UUID

/**
 * Persistence service interface for managing users.
 *
 * This service provides operations related to the persistence of users,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
interface UsuarioPersistenceService {

    /**
     * Finds a user by its ID.
     *
     * @param id The UUID of the user.
     * @return The user entity if found, null otherwise.
     */
    suspend fun findUsuarioById(id: UUID): Usuario?

    /**
     * Finds a user by its username.
     *
     * @param username The username of the user.
     * @return The user entity if found, null otherwise.
     */
    suspend fun findByUsername(username: String?): Usuario?

    /**
     * Finds a user by its cedula, pasaporte, or correo.
     *
     * @param cedula The cedula of the user.
     * @param pasaporte The pasaporte of the user.
     * @param correo The correo of the user.
     * @return The user entity if found, null otherwise.
     */
    suspend fun findByCedulaOrPasaporteOrCorreo(cedula: String?, pasaporte: String?, correo: String?): Usuario?

    /**
     * Finds a user by its licencia or correo.
     *
     * @param licencia The licencia of the user.
     * @param correo The correo of the user.
     * @return The user entity if found, null otherwise.
     */
    suspend fun findByLicenciaOrCorreo(licencia: String?, correo: String?): Usuario?

    /**
     * Saves a user entity.
     *
     * @param usuario The user entity to save.
     * @return The saved user entity.
     */
    suspend fun saveUsuario(usuario: Usuario): Usuario

    /**
     * Creates a new user entity with the given DTO and associates it with the given persona or fabricante.
     *
     * @param usuarioDto The DTO containing the user information.
     * @param persona The persona entity to associate with the user (optional).
     * @param fabricante The fabricante entity to associate with the user (optional).
     * @param encodedPassword The encoded password for the user.
     * @param roles The set of roles for the user.
     * @return The created user entity.
     */
    suspend fun createUser(
        usuarioDto: UsuarioDto,
        persona: Persona? = null,
        fabricante: Fabricante? = null,
        encodedPassword: String,
        roles: MutableSet<Rol>,
    ): Usuario
}
