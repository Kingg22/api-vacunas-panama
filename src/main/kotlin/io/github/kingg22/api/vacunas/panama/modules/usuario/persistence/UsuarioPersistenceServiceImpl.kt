package io.github.kingg22.api.vacunas.panama.modules.usuario.persistence

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.toRol
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.UsuarioRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.persistence.EntityManager
import java.util.UUID

/**
 * Implementation of the UsuarioPersistenceService interface.
 *
 * This service provides operations related to the persistence of users,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@ApplicationScoped
class UsuarioPersistenceServiceImpl(
    private val entityManager: EntityManager,
    private val transactionTemplate: TransactionTemplate,
    private val usuarioRepository: UsuarioRepository,
) : UsuarioPersistenceService {

    /**
     * Finds a user by its ID.
     *
     * @param id The UUID of the user.
     * @return The user entity if found, null otherwise.
     */
    override suspend fun findUsuarioById(id: UUID) = usuarioRepository.findByIdOrNull(id)

    /**
     * Finds a user by its username.
     *
     * @param username The username of the user.
     * @return The user entity if found, null otherwise.
     */
    override suspend fun findByUsername(username: String?) = usuarioRepository.findByUsername(username)

    /**
     * Finds a user by its cedula, pasaporte, or correo.
     *
     * @param cedula The cedula of the user.
     * @param pasaporte The pasaporte of the user.
     * @param correo The correo of the user.
     * @return The user entity if found, null otherwise.
     */
    override suspend fun findByCedulaOrPasaporteOrCorreo(cedula: String?, pasaporte: String?, correo: String?) =
        usuarioRepository.findByCedulaOrPasaporteOrCorreo(cedula, pasaporte, correo)

    /**
     * Finds a user by its licencia or correo.
     *
     * @param licencia The licencia of the user.
     * @param correo The correo of the user.
     * @return The user entity if found, null otherwise.
     */
    override suspend fun findByLicenciaOrCorreo(licencia: String?, correo: String?) =
        usuarioRepository.findByLicenciaOrCorreo(licencia, correo)

    /**
     * Saves a user entity.
     *
     * @param usuario The user entity to save.
     * @return The saved user entity.
     */
    override suspend fun saveUsuario(usuario: Usuario): Usuario {
        var user: Usuario? = null
        transactionTemplate.execute {
            entityManager.merge(usuario)
            user = usuarioRepository.save(usuario)
        }
        checkNotNull(user) { "User was not saved" }
        return user
    }

    /**
     * Creates a new user entity with the given DTO and associates it with the given persona or fabricante.
     *
     * @param usuarioDto The DTO containing the user information.
     * @param personaId The persona entity to associate with the user (optional).
     * @param fabricanteId The fabricante entity to associate with the user (optional).
     * @param encodedPassword The encoded password for the user.
     * @param roles The set of roles for the user.
     * @return The created user entity.
     */
    override suspend fun createUser(
        usuarioDto: UsuarioDto,
        personaId: UUID?,
        fabricanteId: UUID?,
        encodedPassword: String,
        roles: Set<RolDto>,
    ): Usuario {
        var savedUsuario: Usuario? = null
        transactionTemplate.execute {
            // Find entities using entityManager.find instead of merge
            val managedPersona = personaId?.let {
                entityManager.find(Persona::class.java, it)
            }
            val managedFabricante = fabricanteId?.let {
                entityManager.find(Fabricante::class.java, it)
            }

            val usuario = Usuario(
                username = usuarioDto.username,
                clave = encodedPassword,
                createdAt = usuarioDto.createdAt,
                roles = roles.map { it.toRol() }.toMutableSet(),
                persona = managedPersona,
                fabricante = managedFabricante,
                id = usuarioDto.id,
                disabled = usuarioDto.disabled,
            )
            managedPersona?.usuario = usuario
            managedFabricante?.usuario = usuario

            entityManager.persist(usuario)
            savedUsuario = usuario
        }
        checkNotNull(savedUsuario) { "User was not created" }
        return savedUsuario
    }
}
