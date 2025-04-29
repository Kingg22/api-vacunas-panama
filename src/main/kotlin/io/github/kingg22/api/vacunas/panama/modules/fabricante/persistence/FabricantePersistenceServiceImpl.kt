package io.github.kingg22.api.vacunas.panama.modules.fabricante.persistence

import io.github.kingg22.api.vacunas.panama.modules.fabricante.repository.FabricanteRepository
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * Implementation of the FabricantePersistenceService interface.
 *
 * This service provides operations related to the persistence of fabricantes,
 * acting as an intermediate layer between the repositories and the service layer.
 * It encapsulates all JPA-related operations.
 */
@Service
class FabricantePersistenceServiceImpl(private val fabricanteRepository: FabricanteRepository) :
    FabricantePersistenceService {

    /**
     * Finds a fabricante by its licencia.
     *
     * @param licencia The licencia of the fabricante.
     * @return The fabricante entity if found, null otherwise.
     */
    override suspend fun findByLicencia(licencia: String) = fabricanteRepository.findByLicencia(licencia)

    /**
     * Finds a fabricante by its user ID.
     *
     * @param idUsuario The UUID of the user.
     * @return The fabricante entity if found, null otherwise.
     */
    override suspend fun findByUsuarioId(idUsuario: UUID) = fabricanteRepository.findByUsuario_Id(idUsuario)
}
