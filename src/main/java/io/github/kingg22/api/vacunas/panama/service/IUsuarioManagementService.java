package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Usuario;
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiError;
import io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto;
import io.github.kingg22.api.vacunas.panama.web.dto.RegisterUser;
import io.github.kingg22.api.vacunas.panama.web.dto.RestoreDto;
import io.github.kingg22.api.vacunas.panama.web.dto.UsuarioDto;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.core.Authentication;

public interface IUsuarioManagementService {
    Optional<Usuario> getUsuario(String identifier);

    ApiContentResponse createUser(RegisterUser registerUser);

    ApiContentResponse changePassword(RestoreDto restoreDto);

    List<? extends ApiError> validateAuthoritiesRegister(
            @org.jetbrains.annotations.NotNull @NotNull UsuarioDto usuarioDto,
            @org.jetbrains.annotations.NotNull @NotNull Authentication authentication);

    Map<String, Serializable> generateTokens(UUID id);

    List<IdNombreDto> getIdNombrePermisos();

    List<IdNombreDto> getIdNombreRoles();

    Map<String, Serializable> getProfile(UUID id);

    Map<String, Serializable> setLoginData(UUID id);

    void updateLastUsed(UUID id);
}
