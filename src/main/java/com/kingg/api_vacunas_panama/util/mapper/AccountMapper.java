package com.kingg.api_vacunas_panama.util.mapper;

import com.kingg.api_vacunas_panama.persistence.entity.Permiso;
import com.kingg.api_vacunas_panama.persistence.entity.Rol;
import com.kingg.api_vacunas_panama.persistence.entity.Usuario;
import com.kingg.api_vacunas_panama.web.dto.PermisoDto;
import com.kingg.api_vacunas_panama.web.dto.RolDto;
import com.kingg.api_vacunas_panama.web.dto.UsuarioDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

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
