package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.web.dto.UsuarioDto;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

public interface ITokenService {
    Map<String, Serializable> generateTokens(
            @org.jetbrains.annotations.NotNull @NotNull UsuarioDto usuarioDto,
            Map<String, Serializable> idsAdicionales);

    boolean isAccessTokenValid(
            @org.jetbrains.annotations.NotNull @NotNull String userId,
            @org.jetbrains.annotations.NotNull @NotNull String tokenId);

    boolean isRefreshTokenValid(
            @org.jetbrains.annotations.NotNull @NotNull String userId,
            @org.jetbrains.annotations.NotNull @NotNull String tokenId);
}
