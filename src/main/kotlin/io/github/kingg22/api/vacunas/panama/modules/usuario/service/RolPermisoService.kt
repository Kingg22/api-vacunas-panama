package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.common.dto.IdNombreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto

/**
 * Service interface responsible for handling role and permission operations.
 *
 * Provides methods to retrieve role and permission identifiers (IDs and names),
 * as well as mapping functionality between data transfer objects (DTOs) and domain entities.
 *
 * This service is typically used in the business logic layer to:
 * - Fetch minimal representations of roles and permissions (ID + name).
 * - Support presentation layer needs such as dropdown population or access control.
 * - Convert between external-facing DTOs and internal domain models.
 *
 * Implementations of this interface should ensure adherence to system-level access control
 * and business rules for roles and permissions.
 */
interface RolPermisoService {
    /**
     * Retrieves all roles or authorities available, returning their IDs and names.
     *
     * @return List of ID-name DTOs representing permissions.
     */
    suspend fun getIdNombrePermisos(): List<IdNombreDto>

    /**
     * Retrieves all defined roles within the system, returning their IDs and names.
     *
     * @return List of ID-name DTOs representing roles.
     */
    suspend fun getIdNombreRoles(): List<IdNombreDto>

    /**
     * Converts a [RolDto] data transfer object into an existing entity.
     * Useful when mapping data received from the presentation layer to the domain model.
     *
     * @param setRolDto the data transfer object representing a role.
     * @return the corresponding [Set] of [RolDto] objects, or empty if the conversion cannot be performed.
     */
    suspend fun convertToExistRol(setRolDto: Set<RolDto>): Set<RolDto>
}
