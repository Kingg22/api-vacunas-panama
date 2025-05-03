package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.modules.usuario.service.TokenService
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.UsuarioService
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.logger
import io.vertx.ext.web.RoutingContext
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.util.UUID

@Path("/token")
@Produces(MediaType.APPLICATION_JSON)
class TokenController(private val usuarioService: UsuarioService, private val tokenService: TokenService) {
    private val log = logger()

    /**
     * Handles refreshing of tokens. The validation is not performed here as a security filter and OAuth ensures access
     * to this endpoint only if a valid refresh token is provided. The used refresh token is removed from memory.
     *
     * @param jwt The [] containing user ID.
     * @param request The [] used for building the response.
     * @return [Response] with new access_token and refresh_token.
     */
    @Path("/refresh")
    @POST
    suspend fun refreshToken(rc: RoutingContext): Response {
        val jwt = object {
            val subject: String? = null
            val id = "43"
        }
        val apiResponse = createResponse()
        val userId = checkNotNull(jwt.subject) { "Jwt subject is null" }
        log.debug("Receive a request to refresh token for user with id: {}", userId)

        try {
            usuarioService.getUsuarioById(UUID.fromString(userId))?.let {
                log.trace("User found, refreshing token for user with id: {}", userId)
                // TODO change to add additional ids (persona, fabricante)
                apiResponse.addData(tokenService.generateTokens(it))
                apiResponse.addStatusCode(200)
                log.debug("Deleting refresh token for user with id: {}", userId)
                // redisTemplate.delete("token:refresh:$userId:${jwt.id}").awaitSingle()
            } ?: {
                log.trace("User not found, refreshing token for user with id: {}", userId)
                apiResponse.addError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.NOT_FOUND)
                        withMessage("Usuario no encontrado, intente nuevamente")
                    },
                )
                apiResponse.addStatusCode(404)
            }
        } catch (e: IllegalArgumentException) {
            log.error("Error while refreshing token to {}", userId, e)
            apiResponse.addStatus("message", "Invalid token")
            apiResponse.addStatusCode(403)
        }
        return createResponseEntity(apiResponse, rc)
    }
}
