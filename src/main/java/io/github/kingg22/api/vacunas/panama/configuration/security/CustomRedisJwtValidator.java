package io.github.kingg22.api.vacunas.panama.configuration.security;

import io.github.kingg22.api.vacunas.panama.service.ITokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Custom validator for {@link Jwt} tokens using Redis for validation.
 *
 * <p>Validate JWT tokens against stored values in a Redis cache. The validation process ensures that revoked tokens are
 * not accepted.
 */
@RequiredArgsConstructor
class CustomRedisJwtValidator implements OAuth2TokenValidator<Jwt> {
    private final ITokenService tokenService;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        var userId = token.getSubject();
        var tokenId = token.getId();

        try {
            var accessTokenValid = tokenService.isAccessTokenValid(userId, tokenId);
            var refreshTokenValid = tokenService.isRefreshTokenValid(userId, tokenId);

            if (accessTokenValid || refreshTokenValid) {
                return OAuth2TokenValidatorResult.success();
            } else {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Tokens has been revoked", null));
            }
        } catch (IllegalStateException exception) {
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("server_error", "Token validation failed due to server issue", null));
        }
    }
}
