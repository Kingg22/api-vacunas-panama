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

        private fun setWWWHeader(response: ServerHttpResponse, errorCode: String, description: String) {
            response.headers[HttpHeaders.WWW_AUTHENTICATE] = buildString {
                append("Bearer")
                if (errorCode.isNotBlank() && description.isNotBlank()) {
                    append(" error=\"$errorCode\", error_description=\"$description\"")
                }
            }
        }
    }

    private val log = logger()

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val authorization = exchange.request.headers.getFirst(AUTHORIZATION_HEADER)
        if (authorization.isNullOrBlank() || !authorization.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange)
        }

        val token = authorization.removePrefix(BEARER_PREFIX)
        log.debug("Trying to decode Token: {}", token)

        return jwtDecoder.decode(token)
            .flatMap { jwt ->
                val userId = jwt.subject
                val tokenId = jwt.id
                log.debug("Verifying token for $userId with tokenId: $tokenId")

                Mono.zip(
                    tokenService.isAccessTokenValid(userId, tokenId),
                    tokenService.isRefreshTokenValid(userId, tokenId),
                ).flatMap {
                    val (accessTokenValid, refreshTokenValid) = it.t1 to it.t2
                    log.debug("IsAccessTokenValid: $accessTokenValid, IsRefreshTokenValid: $refreshTokenValid")

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

                        !accessTokenValid && !refreshTokenValid -> {
                            setWWWHeader(exchange.response, "invalid_token", "Tokens has been revoked")
                            exchange.response.statusCode = HttpStatus.FORBIDDEN
                            exchange.response.setComplete()
                        }

                        else -> chain.filter(exchange)
                    }
                }
            }
            .onErrorResume {
                log.error("Error occurred during JWT decoding in JwtRefreshFilter", it)
                setWWWHeader(exchange.response, "server_error", "Token validation failed due to server issue")
                exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                exchange.response.setComplete()
            }
    }
}
