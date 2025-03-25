package io.github.kingg22.api_vacunas_panama.service;

import io.github.kingg22.api_vacunas_panama.persistence.entity.Fabricante;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Permiso;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Persona;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Rol;
import io.github.kingg22.api_vacunas_panama.persistence.entity.Usuario;
import io.github.kingg22.api_vacunas_panama.persistence.repository.PermisoRepository;
import io.github.kingg22.api_vacunas_panama.persistence.repository.RolRepository;
import io.github.kingg22.api_vacunas_panama.persistence.repository.UsuarioRepository;
import io.github.kingg22.api_vacunas_panama.util.mapper.AccountMapper;
import io.github.kingg22.api_vacunas_panama.web.dto.PermisoDto;
import io.github.kingg22.api_vacunas_panama.web.dto.RolDto;
import io.github.kingg22.api_vacunas_panama.web.dto.UsuarioDto;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for {@link UsuarioManagementService} transaction.
 * Public methods required by {@link Transactional}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioTransactionService {
    private final AccountMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final PermisoRepository permisoRepository;
    private final RolRepository rolRepository;

    @Transactional
    public Usuario createUser(@NotNull UsuarioDto usuarioDto, @Nullable Persona persona, @Nullable Fabricante fabricante) {
        if (persona != null && fabricante != null) {
            throw new IllegalArgumentException("No se puede crear un usuario sin relacionarse a una persona o fabricante");
        }
        Set<Rol> role = usuarioDto.roles().stream()
                .map(this::convertToRoleExisting)
                .collect(Collectors.toSet());

        Usuario usuario = Usuario.builder()
                .username(usuarioDto.username())
                .password(passwordEncoder.encode(usuarioDto.password()))
                .createdAt(usuarioDto.createdAt() != null ? usuarioDto.createdAt() : LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC))
                .roles(role)
                .build();
        if (persona != null) {
            usuario.setPersona(persona);
            persona.setUsuario(usuario);
        }
        if (fabricante != null) {
            usuario.setFabricante(fabricante);
            fabricante.setUsuario(usuario);
        }
        return usuarioRepository.save(usuario);
    }

    PermisoDto createPermiso(PermisoDto permisoDto) {
        return mapper.permisoToDto(permisoRepository.save(mapper.permisoDtoToPermiso(permisoDto)));
    }

    @Modifying
    public void updateLastUsed(UUID id) {
        Usuario usuario = this.usuarioRepository.findById(id).orElseThrow();
        usuario.setLastUsed(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        usuarioRepository.save(usuario);
    }

    @Transactional
    @Modifying
    public void changePasswordPersonas(Persona persona, String newPassword) {
        Usuario usuario = persona.getUsuario();
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    Rol convertToRoleExisting(RolDto rolDto) {
        return rolRepository.findByNombreOrId(rolDto.nombre(), rolDto.id()).orElse(null);
    }

    Permiso convertToPermisoExisting(PermisoDto permisoDto) {
        return permisoRepository.findByNombreOrId(permisoDto.nombre(), permisoDto.id()).orElse(null);
    }
}
