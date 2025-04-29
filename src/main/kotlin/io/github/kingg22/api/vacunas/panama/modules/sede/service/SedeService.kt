package io.github.kingg22.api.vacunas.panama.modules.sede.service

import io.github.kingg22.api.vacunas.panama.modules.common.dto.UUIDNombreDto
import io.github.kingg22.api.vacunas.panama.modules.sede.domain.SedeModel
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
     * Retrieves the details of a `Sede` model based on its unique identifier.
     *
     * @param id the unique identifier of the `Sede` to retrieve
     * @return a [SedeModel] containing the details of the `Sede`, or null if not found.
     */
    suspend fun getSedeById(id: UUID): SedeModel?
}
