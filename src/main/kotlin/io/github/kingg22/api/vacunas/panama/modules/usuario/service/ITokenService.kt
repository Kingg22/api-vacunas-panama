package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import jakarta.validation.constraints.NotNull
import reactor.core.publisher.Mono
import java.io.Serializable

/**
 * Service for managing JWT tokens.
 *
 * This service is responsible for encoding information from DTOs entities previously fetched by
 * [IUsuarioManagementService], Its sets the issuer and time values based on application.properties.
 *
 * The service now supports both access tokens and refresh tokens, ensuring the correct usage of each token type.
 * Tokens are stored in Redis cache to facilitate efficient validation and retrieval.
 *
 * This service provides methods for validating the existence of tokens in the cache. It does not handler the
 * decoding of tokens; this is managed by the [org.springframework.security.oauth2.jwt.JwtDecoder]
 */
interface ITokenService {
    fun generateTokens(
        @NotNull usuarioDto: UsuarioDto,
        idsAdicionales: Map<String, Serializable>,
    ): Map<String, Serializable>

    fun isAccessTokenValid(@NotNull userId: String, @NotNull tokenId: String): Mono<Boolean>

    fun isRefreshTokenValid(@NotNull userId: String, @NotNull tokenId: String): Mono<Boolean>
}
