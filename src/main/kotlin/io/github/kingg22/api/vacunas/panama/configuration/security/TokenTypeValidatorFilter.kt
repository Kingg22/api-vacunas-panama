package io.github.kingg22.api.vacunas.panama.configuration.security

import io.github.kingg22.api.vacunas.panama.modules.usuario.service.TokenService
import io.github.kingg22.api.vacunas.panama.util.logger
import io.smallrye.jwt.auth.cdi.NullJsonWebToken
import jakarta.annotation.Priority
import jakarta.inject.Inject
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.eclipse.microprofile.jwt.JsonWebToken

@Provider
@Priority(Priorities.AUTHORIZATION)
class TokenTypeValidatorFilter(private val tokenService: TokenService) : ContainerRequestFilter {
    companion object {
        private const val REFRESH_TOKEN_ENDPOINT = "/vacunacion/v1/token/refresh"

        private fun setWWWHeader(context: ContainerRequestContext, errorCode: String, description: String) {
            context.abortWith(
                Response.status(Response.Status.FORBIDDEN).header(
                    "WWW-Authenticate",
                    buildString {
                        append("Bearer")
                        if (errorCode.isNotBlank() && description.isNotBlank()) {
                            append(" error=\"$errorCode\", error_description=\"$description\"")
                        }
                    },
                ).build(),
            )
        }
    }

    private val log = logger()

    @Inject
    lateinit var jwt: JsonWebToken

    override fun filter(requestContext: ContainerRequestContext) {
        try {
            if (jwt is NullJsonWebToken || jwt.subject == null || jwt.tokenID == null) return
            val userId = checkNotNull(jwt.subject)
            val tokenId = checkNotNull(jwt.tokenID)
            log.debug("Verifying token for $userId with tokenId: $tokenId")

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val (accessTokenValid, refreshTokenValid) = awaitAll(
                        this.async { tokenService.isAccessTokenValid(userId, tokenId) },
                        this.async { tokenService.isRefreshTokenValid(userId, tokenId) },
                    )
                    val path = requestContext.uriInfo.path
                    checkNotNull(path) { "Path is null for filter" }
                    log.debug(
                        "IsAccessTokenValid: $accessTokenValid, IsRefreshTokenValid: $refreshTokenValid. Going to '$path'",
                    )

                    when {
                        !accessTokenValid &&
                            refreshTokenValid &&
                            path != REFRESH_TOKEN_ENDPOINT -> {
                            log.debug("Refresh token invalid use. Returning 403 Forbidden.")
                            setWWWHeader(requestContext, "invalid_token", "Refresh token is only for refresh tokens")
                        }

                        accessTokenValid &&
                            !refreshTokenValid &&
                            path == REFRESH_TOKEN_ENDPOINT -> {
                            log.debug("Access token invalid use. Returning 403 Forbidden.")
                            setWWWHeader(
                                requestContext,
                                "invalid_token",
                                "Access token cannot be used to refresh tokens",
                            )
                        }

                        !accessTokenValid && !refreshTokenValid -> {
                            log.debug("Access and refresh token invalid use. Returning 403 Forbidden.")
                            setWWWHeader(requestContext, "invalid_token", "Tokens has been revoked")
                        }
                    }
                } catch (e: RuntimeException) {
                    log.error("Error occurred during verifying JWT", e)
                    setWWWHeader(requestContext, "server_error", "Token validation failed due to server issue")
                }
            }
        } catch (e: RuntimeException) {
            log.error("Error occurred during extraction to verifying JWT", e)
            setWWWHeader(requestContext, "server_error", "Token validation failed due to server issue")
        }
    }
}
