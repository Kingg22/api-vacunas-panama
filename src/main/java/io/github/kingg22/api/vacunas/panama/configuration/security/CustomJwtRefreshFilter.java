package io.github.kingg22.api.vacunas.panama.configuration.security;

import io.github.kingg22.api.vacunas.panama.service.ITokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
class CustomJwtRefreshFilter extends OncePerRequestFilter {
    private final ITokenService tokenService;
    private final JwtDecoder jwtDecoder;
    private static final String REFRESH_TOKEN_ENDPOINT = "/vacunacion/v1/token/refresh";

    /**
     * Filter to validate {@link Jwt} tokens on each request. This filter checks if the token present in the request is
     * a refresh token. If the request is made to the {@code REFRESH_TOKEN_ENDPOINT}, it allows access only if the
     * provided token is a valid refresh token. If a refresh token is used to access any other endpoint, it will be
     * rejected. Additionally, if an access token is used to access the {@code REFRESH_TOKEN_ENDPOINT} it will also be
     * rejected.
     *
     * @param request The {@link HttpServletRequest} containing the incoming request data.
     * @param response The {@link HttpServletResponse} used to send the response.
     * @param filterChain The {@link FilterChain} used to invoke the next filter in the chain.
     * @throws ServletException If an error occurs during the processing of the request.
     * @throws IOException If an input or output error occurs while handling the request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            var token = authorization.substring("Bearer ".length());

            try {
                var jwt = jwtDecoder.decode(token);
                var userId = jwt.getSubject();
                var tokenId = jwt.getId();

                var accessTokenValid = tokenService.isAccessTokenValid(userId, tokenId);
                var refreshTokenValid = tokenService.isRefreshTokenValid(userId, tokenId);

                if (!accessTokenValid
                        && refreshTokenValid
                        && !request.getRequestURI().equals(REFRESH_TOKEN_ENDPOINT)) {
                    setWWWHeader(
                            response,
                            "invalid_token",
                            "An error occurred while attempting to decode the Jwt: Refresh token is only for refresh tokens");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                if (accessTokenValid
                        && !refreshTokenValid
                        && request.getRequestURI().equals(REFRESH_TOKEN_ENDPOINT)) {
                    setWWWHeader(
                            response,
                            "invalid_token",
                            "An error occurred while attempting to decode the Jwt: Access token cannot be used to refresh tokens");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                // TODO add validation if the type is false to both type
            } catch (RuntimeException exception) {
                setWWWHeader(
                        response,
                        "server_error",
                        "An error occurred while attempting to decode the Jwt: Token validation failed due to server issue");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Set header {@code WWW-Authenticate} based on the format that
     * {@link org.springframework.security.oauth2.core.OAuth2TokenValidatorResult} does for validation errors
     *
     * @param response The {@link HttpServletResponse} used to send the response.
     * @param errorCode The code based on {@link org.springframework.security.oauth2.core.OAuth2Error}
     * @param description The description of error based on {@link org.springframework.security.oauth2.core.OAuth2Error}
     * @see org.springframework.security.oauth2.core.OAuth2AuthenticationException
     */
    private void setWWWHeader(
            @NotNull final HttpServletResponse response,
            @NotNull final String errorCode,
            @NotNull final String description) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!errorCode.isBlank() && !description.isBlank()) {
            wwwAuthenticate.append(" error=\"").append(errorCode).append("\", error_description=\"");
            wwwAuthenticate.append(description).append("\"");
        }
        response.setHeader("WWW-Authenticate", wwwAuthenticate.toString());
    }
}
