package io.github.kingg22.api.vacunas.panama.modules.usuario.persistence

import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.UsuarioRepository
import jakarta.persistence.EntityManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate
import java.util.UUID

/**
 * Implementation of the UsuarioPersistenceService interface.
 *
 * This service provides operations related to the persistence of users,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@Service
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
}
