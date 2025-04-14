package io.github.kingg22.api.vacunas.panama.modules.fabricante.service

import io.github.kingg22.api.vacunas.panama.modules.fabricante.dto.FabricanteDto
import java.util.Optional
import java.util.UUID

/** Service interface for managing and retrieving fabricante-related information. */
interface FabricanteService {
    /**
     * Retrieves a [FabricanteDto] associated with the specific licencia.
     *
     * @param licencia String with the licencia linked to the fabricante.
     * @return [Optional] containing the fabricante if found or empty if not.
     */
    fun getFabricante(licencia: String): Optional<FabricanteDto>

    /**
     * Retrieves a [FabricanteDto] associated with the specified user ID.
     *
     * @param idUser UUID of the user linked to the doctor.
     * @return [Optional] containing the fabricante if found or empty if not.
     */
    fun getFabricanteByUserID(idUser: UUID): Optional<FabricanteDto>
}
