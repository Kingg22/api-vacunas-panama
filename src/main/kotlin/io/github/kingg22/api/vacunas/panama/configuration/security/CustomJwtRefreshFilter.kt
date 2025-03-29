package io.github.kingg22.api.vacunas.panama.configuration.security

import io.github.kingg22.api.vacunas.panama.modules.usuario.service.ITokenService
import io.github.kingg22.api.vacunas.panama.util.logger
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class CustomJwtRefreshFilter(private val tokenService: ITokenService, private val jwtDecoder: ReactiveJwtDecoder) :
    WebFilter {
    companion object {
        private const val REFRESH_TOKEN_ENDPOINT = "/vacunacion/v1/token/refresh"
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private val log = logger()

        private fun setWWWHeader(response: ServerHttpResponse, errorCode: String, description: String) {
            response.headers[HttpHeaders.WWW_AUTHENTICATE] = buildString {
                append("Bearer")
                if (errorCode.isNotBlank() && description.isNotBlank()) {
                    append(" error=\"$errorCode\", error_description=\"$description\"")
                }
            }
        }
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authorization = exchange.request.headers.getFirst(AUTHORIZATION_HEADER)
        if (authorization.isNullOrBlank() || !authorization.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange)
        }
        val token = authorization.removePrefix(BEARER_PREFIX)

        return jwtDecoder.decode(token)
            .flatMap { jwt ->
                val userId = jwt.subject
                val tokenId = jwt.id

                // Comprobación de validez del token
                val accessTokenValid = tokenService.isAccessTokenValid(userId, tokenId)
                val refreshTokenValid = tokenService.isRefreshTokenValid(userId, tokenId)

                when {
                    !accessTokenValid &&
                        refreshTokenValid &&
                        exchange.request.uri.path != REFRESH_TOKEN_ENDPOINT -> {
                        setWWWHeader(exchange.response, "invalid_token", "Refresh token is only for refresh tokens")
                        exchange.response.statusCode = HttpStatus.FORBIDDEN
                        exchange.response.setComplete()
                    }

                    accessTokenValid &&
                        !refreshTokenValid &&
                        exchange.request.uri.path == REFRESH_TOKEN_ENDPOINT -> {
                        setWWWHeader(
                            exchange.response,
                            "invalid_token",
                            "Access token cannot be used to refresh tokens",
                        )
                        exchange.response.statusCode = HttpStatus.FORBIDDEN
                        exchange.response.setComplete()
                    }

                    else -> chain.filter(exchange)
                }
            }
            .onErrorResume { ex ->
                log.error("Error occurred during JWT decoding in JwtRefreshFilter", ex)
                setWWWHeader(exchange.response, "server_error", "Token validation failed due to server issue")
                exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                exchange.response.setComplete()
            }
    }
}
