package io.github.kingg22.api.vacunas.panama.service

import io.github.kingg22.api.vacunas.panama.web.dto.UsuarioDto
import jakarta.validation.constraints.NotNull
import java.io.Serializable

interface ITokenService {
    fun generateTokens(
        @NotNull usuarioDto: UsuarioDto,
        idsAdicionales: Map<String, Serializable>,
    ): Map<String, Serializable>

    fun isAccessTokenValid(@NotNull userId: String, @NotNull tokenId: String): Boolean

    fun isRefreshTokenValid(@NotNull userId: String, @NotNull tokenId: String): Boolean
}
