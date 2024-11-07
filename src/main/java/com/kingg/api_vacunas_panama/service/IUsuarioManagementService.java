package com.kingg.api_vacunas_panama.service;

import com.kingg.api_vacunas_panama.response.IApiContentResponse;
import com.kingg.api_vacunas_panama.web.dto.IdNombreDto;
import com.kingg.api_vacunas_panama.web.dto.RegisterUser;
import com.kingg.api_vacunas_panama.web.dto.RestoreDto;
import com.kingg.api_vacunas_panama.web.dto.UsuarioDto;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IUsuarioManagementService {
    IApiContentResponse createUser(RegisterUser registerUser);

    IApiContentResponse changePassword(RestoreDto restoreDto);

    List<? extends Serializable> validateAuthoritiesRegister(@NotNull UsuarioDto usuarioDto, @NotNull Authentication authentication);

    Map<String, Serializable> generateTokens(UUID id);

    List<IdNombreDto> getIdNombrePermisos();

    List<IdNombreDto> getIdNombreRoles();

    Map<String, Serializable> getProfile(UUID id);

    Map<String, Serializable> setLoginData(UUID id);
}
