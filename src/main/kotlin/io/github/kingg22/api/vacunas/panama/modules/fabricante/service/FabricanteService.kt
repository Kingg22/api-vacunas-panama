package io.github.kingg22.api.vacunas.panama.modules.fabricante.service

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import jakarta.validation.constraints.NotNull
import java.util.Optional
import java.util.UUID

/** Service interface for managing and retrieving fabricante-related information. */
interface FabricanteService {
    /**
     * Retrieves a [Fabricante] entity associated with the specific licencia.
     *
     * @param licencia String with the licencia linked to the fabricante.
     * @return [Optional] containing the doctor if found or empty if not.
     */
    fun getFabricante(@NotNull licencia: String): Optional<Fabricante>

    /**
     * Retrieves a [Fabricante] entity associated with the specified user ID.
     *
     * @param idUser UUID of the user linked to the doctor.
     * @return [Optional] containing the doctor if found or empty if not.
     */
    fun getFabricanteByUserID(@NotNull idUser: UUID): Optional<Fabricante>
}
