package io.github.kingg22.api.vacunas.panama.modules.sede.service

import io.github.kingg22.api.vacunas.panama.modules.common.dto.UUIDNombreDto
import io.github.kingg22.api.vacunas.panama.modules.sede.dto.SedeDto
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import java.util.UUID

/**
 * Service interface for managing entities related to `Sede`.
 * Provides methods to retrieve data about `Sedes` and perform related operations.
 */
interface SedeService {
    /**
     * Retrieves a list of `Sede` with their IDs and corresponding names.
     *
     * @return a list of [UUIDNombreDto] containing the UUID and name of each `Sede`.
     */
    suspend fun getIdNombreSedes(): List<UUIDNombreDto>

    /**
     * Retrieves the details of a `Sede` entity based on its unique identifier.
     *
     * @param id the unique identifier of the `Sede` to retrieve
     * @return a [SedeDto] containing the details of the `Sede`, or null if no found.
     */
    @Deprecated("Use DTO instead")
    suspend fun getSedeById(id: UUID): Sede?
}
