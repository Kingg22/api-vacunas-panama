package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.modules.usuario.service.TokenService
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.UsuarioService
import io.github.kingg22.api.vacunas.panama.response.ActualApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.createResponseEntity
import io.github.kingg22.api.vacunas.panama.util.logger
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.Serializable
import java.util.UUID

@RestController
@RequestMapping(path = ["/token"], produces = [MediaType.APPLICATION_JSON_VALUE])
class TokenController(
    private val redisTemplate: ReactiveRedisTemplate<String, Serializable>,
    private val usuarioService: UsuarioService,
    private val tokenService: TokenService,
) {
    private val log = logger()

    /**
     * Handles refreshing of tokens. The validation is not performed here as a security filter and OAuth ensures access
     * to this endpoint only if a valid refresh token is provided. The used refresh token is removed from memory.
     *
     * @param jwt The [Jwt] containing user ID.
     * @param request The [ServerHttpRequest] used for building the response.
     * @return [ActualApiResponse] with new access_token and refresh_token.
     */
    @PostMapping("/refresh")
    suspend fun refreshToken(
        @AuthenticationPrincipal jwt: Jwt,
        request: ServerHttpRequest,
    ): ResponseEntity<ActualApiResponse> {
        val apiResponse = createResponse()
        val userId = checkNotNull(jwt.subject) { "Jwt subject is null" }
        log.debug("Receive a request to refresh token for user with id: {}", userId)

        try {
            usuarioService.getUsuarioById(UUID.fromString(userId))?.let {
                log.trace("User found, refreshing token for user with id: {}", userId)
                apiResponse.addData(tokenService.generateTokens(it))
                apiResponse.addStatusCode(HttpStatus.OK.value())
                log.debug("Deleting refresh token for user with id: {}", userId)
                redisTemplate.delete("token:refresh:$userId:${jwt.id}").awaitSingle()
            } ?: {
                log.trace("User not found, refreshing token for user with id: {}", userId)
                apiResponse.addError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.NOT_FOUND)
                        withMessage("Usuario no encontrado, intente nuevamente")
                    },
                )
                apiResponse.addStatusCode(HttpStatus.NOT_FOUND.value())
            }
        } catch (e: IllegalArgumentException) {
            log.error("Error while refreshing token to {}", userId, e)
            apiResponse.addStatus("message", "Invalid token")
            apiResponse.addStatusCode(HttpStatus.FORBIDDEN.value())
        }
        return createResponseEntity(apiResponse, request)
    }
}
