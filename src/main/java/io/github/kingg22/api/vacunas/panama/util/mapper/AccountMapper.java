package io.github.kingg22.api.vacunas.panama.util.mapper;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Permiso;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Rol;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Usuario;
import io.github.kingg22.api.vacunas.panama.web.dto.PermisoDto;
import io.github.kingg22.api.vacunas.panama.web.dto.RolDto;
import io.github.kingg22.api.vacunas.panama.web.dto.UsuarioDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface AccountMapper {
    @Mapping(target = "rolesPermisos", ignore = true)
    @Mapping(target = "roles", ignore = true)
    Permiso permisoDtoToPermiso(PermisoDto permisoDto);

    PermisoDto permisoToDto(Permiso permiso);

    @Mapping(target = "usuariosRoles", ignore = true)
    @Mapping(target = "rolesPermisos", ignore = true)
    @Mapping(target = "usuarios", ignore = true)
    Rol rolDtoToRol(RolDto rolDto);

    RolDto rolToDto(Rol rol);

    UsuarioDto usuarioToDto(Usuario usuario);

    @Mapping(target = "usuariosRoles", ignore = true)
    @Mapping(target = "persona", ignore = true)
    @Mapping(target = "fabricante", ignore = true)
    @Mapping(target = "disabled", ignore = true)
    Usuario usuarioDtoToUsuario(UsuarioDto usuarioDto);

    List<RolDto> rolListToDtoList(List<Rol> roles);

    List<PermisoDto> permisoListToDtoList(List<Permiso> permisos);
}
