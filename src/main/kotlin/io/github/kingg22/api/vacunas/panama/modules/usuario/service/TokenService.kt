package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import jakarta.validation.constraints.NotNull
import reactor.core.publisher.Mono
import java.io.Serializable

/**
 * Service for managing JWT tokens, including generation and validation.
 *
 * This service is responsible for creating, storing, and validating JWT tokens used in
 * the authentication and authorization processes. It interacts with the [UsuarioManagementService]
 * to fetch user-related data and create access or refresh tokens based on the user's profile and
 * associated data.
 *
 * The generated tokens include both access and refresh tokens, ensuring that the correct type
 * is used for each purpose. The tokens are encoded with data such as user information and expiration
 * times, using values set in the application's configuration (`application.properties`).
 *
 * Tokens are stored in Redis cache to facilitate fast access and efficient validation during
 * authentication. This ensures that the system can handle large-scale user bases while minimizing
 * redundant calculations.
 *
 * **Note:** The decoding of tokens is handled separately by the [org.springframework.security.oauth2.jwt.JwtDecoder].
 * This service is solely responsible for generating, storing, and validating tokens.
 */
interface TokenService {
    /**
     * Generates both access and refresh tokens for the given user, using data from the provided DTO.
     * The generated tokens are stored in Redis for fast access and validation.
     *
     * @param usuarioDto DTO containing the user information used to generate the tokens.
     * @param idsAdicionales Additional data (e.g., roles, permissions) to include in the token payload.
     * @return A map containing the generated tokens (access and refresh) as key-value pairs where the key
     *         is the token type (e.g., "access_token", "refresh_token") and the value is the encoded token string.
     */
    fun generateTokens(
        @NotNull usuarioDto: UsuarioDto,
        idsAdicionales: Map<String, Serializable>,
    ): Map<String, Serializable>

    /**
     * Validates the existence of a given access token in Redis cache.
     * This method checks if the provided token ID exists in the cache as a valid access token.
     *
     * @param userId The user ID associated with the token.
     * @param tokenId The unique token ID of the access token to validate.
     * @return A Mono that emits `true` if the token is valid (exists in cache), or `false` otherwise.
     */
    fun isAccessTokenValid(@NotNull userId: String, @NotNull tokenId: String): Mono<Boolean>

    /**
     * Validates the existence of a given refresh token in Redis cache.
     * This method checks if the provided token ID exists in the cache as a valid refresh token.
     *
     * @param userId The user ID associated with the token.
     * @param tokenId The unique token ID of the refresh token to validate.
     * @return A Mono that emits `true` if the token is valid (exists in cache), or `false` otherwise.
     */
    fun isRefreshTokenValid(@NotNull userId: String, @NotNull tokenId: String): Mono<Boolean>
}
