package io.github.kingg22.api.vacunas.panama.configuration.security

import io.github.kingg22.api.vacunas.panama.modules.usuario.service.ITokenService
import io.github.kingg22.api.vacunas.panama.util.logger
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

/**
 * Custom validator for [Jwt] tokens using Redis for validation.
 *
 * Validate JWT tokens against stored values in a Redis cache. The validation process ensures that revoked tokens are
 * not accepted.
 */
@Component
class CustomRedisJwtValidator(private val tokenService: ITokenService) : OAuth2TokenValidator<Jwt> {
    private val log = logger()

    override fun validate(token: Jwt): OAuth2TokenValidatorResult = try {
        val userId = token.subject
        val tokenId = token.id

        val accessTokenValid = tokenService.isAccessTokenValid(userId, tokenId)
        val refreshTokenValid = tokenService.isRefreshTokenValid(userId, tokenId)

        if (accessTokenValid || refreshTokenValid) {
            OAuth2TokenValidatorResult.success()
        } else {
            OAuth2TokenValidatorResult.failure(OAuth2Error("invalid_token", "Tokens has been revoked", null))
        }
    } catch (exception: IllegalStateException) {
        log.error("Error while validating jwt token in cache", exception)
        OAuth2TokenValidatorResult.failure(
            OAuth2Error(
                "server_error",
                "Token validation failed due to server issue",
                null,
            ),
        )
    }
}
