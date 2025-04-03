package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante;
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona;
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto;
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto;
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Rol;
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario;
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.RolRepository;
import io.github.kingg22.api.vacunas.panama.modules.usuario.repository.UsuarioRepository;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for {@link UsuarioManagementService} transaction. Public methods required by {@link Transactional} */
@Service
class UsuarioTransactionService {
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    public UsuarioTransactionService(
            PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository, RolRepository rolRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
    }

    @Transactional
    public Usuario createUser(
            @NotNull UsuarioDto usuarioDto, @Nullable Persona persona, @Nullable Fabricante fabricante) {
        if (persona != null && fabricante != null) {
            throw new IllegalArgumentException(
                    "No se puede crear un usuario sin relacionarse a una persona o fabricante");
        }
        Set<Rol> role =
                usuarioDto.roles().stream().map(this::convertToRoleExisting).collect(Collectors.toSet());

        Usuario usuario = Usuario.builder()
                .username(usuarioDto.username())
                .password(passwordEncoder.encode(usuarioDto.password()))
                .createdAt(usuarioDto.createdAt() != null ? usuarioDto.createdAt() : LocalDateTime.now(ZoneOffset.UTC))
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

    @Modifying
    public void updateLastUsed(UUID id) {
        Usuario usuario = this.usuarioRepository.findById(id).orElseThrow();
        usuario.setLastUsed(LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
        usuarioRepository.save(usuario);
    }

    @Transactional
    @Modifying
    public void changePasswordPersonas(@org.jetbrains.annotations.NotNull Persona persona, String newPassword) {
        Usuario usuario = persona.getUsuario();
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    Rol convertToRoleExisting(@org.jetbrains.annotations.NotNull RolDto rolDto) {
        return rolRepository.findByNombreOrId(rolDto.nombre(), rolDto.id()).orElse(null);
    }
}
