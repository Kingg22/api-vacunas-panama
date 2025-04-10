package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Rol
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.PermisoRepository
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.RolRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class RolPermisoServiceImpl(
    private val rolRepository: RolRepository,
    private val permisoRepository: PermisoRepository,
) : RolPermisoService {
    @Cacheable(cacheNames = ["massive"], key = "'permisos'")
    override fun getIdNombrePermisos() = permisoRepository.findAllIdNombre()

    @Cacheable(cacheNames = ["massive"], key = "'roles'")
    override fun getIdNombreRoles() = rolRepository.findAllIdNombre()

    override fun convertToRole(rolDto: RolDto): Rol? =
        rolRepository.findByNombreOrId(rolDto.nombre, rolDto.id).orElse(null)
}
