package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.toRolDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.PermisoRepository
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.RolRepository
import io.github.kingg22.api.vacunas.panama.util.logger
import jakarta.enterprise.context.ApplicationScoped

// TODO URGENT NEED TO DECOUPLE REPOSITORY WITH PERSISTENCE LAYER
@ApplicationScoped
class RolPermisoServiceImpl(
    private val rolRepository: RolRepository,
    private val permisoRepository: PermisoRepository,
) : RolPermisoService {
    private val log = logger()

    /*
    @Cacheable(
        cacheNames = [CacheDuration.MASSIVE_VALUE],
        key = "'permisos'",
        unless = "#result==null or #result.isEmpty()",
    )
    @CacheResult(cacheName = CacheDuration.MASSIVE_VALUE)
     */
    override suspend fun getIdNombrePermisos() = permisoRepository.findAllIdNombre()

    /*
    @Cacheable(
        cacheNames = [CacheDuration.MASSIVE_VALUE],
        key = "'roles'",
        unless = "#result==null or #result.isEmpty()",
    )
    @CacheResult(cacheName = CacheDuration.MASSIVE_VALUE)
     */
    override suspend fun getIdNombreRoles() = rolRepository.findAllIdNombre()

    override suspend fun convertToExistRol(setRolDto: Set<RolDto>) = setRolDto
        .mapNotNull {
            rolRepository.findByNombreOrId(it.nombre, it.id)?.toRolDto().also { found ->
                if (found == null) log.warn("Rol no encontrado: ${it.nombre ?: it.id}")
            }
        }
        .toSet()
}
