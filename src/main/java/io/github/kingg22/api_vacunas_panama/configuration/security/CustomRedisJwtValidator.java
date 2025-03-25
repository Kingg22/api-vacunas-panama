package io.github.kingg22.api_vacunas_panama.configuration.security;

import io.github.kingg22.api_vacunas_panama.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Custom validator for {@link Jwt} tokens using Redis for validation.
 * <p>
 * Validate JWT tokens against stored values in a Redis cache.
 * The validation process ensures that revoked tokens are not accepted.
 * </p>
 */
@RequiredArgsConstructor
class CustomRedisJwtValidator implements OAuth2TokenValidator<Jwt> {
    private final TokenService tokenService;

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String userId = token.getSubject();
        String tokenId = token.getId();

        try {
            boolean accessTokenValid = tokenService.isAccessTokenValid(userId, tokenId);
            boolean refreshTokenValid = tokenService.isRefreshTokenValid(userId, tokenId);

            if (accessTokenValid || refreshTokenValid) {
                return OAuth2TokenValidatorResult.success();
            } else {
                return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Tokens has been revoked", null));
            }
        } catch (IllegalStateException exception) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("server_error", "Token validation failed due to server issue", null));
        }
    }

}
