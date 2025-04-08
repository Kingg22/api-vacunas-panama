package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.toUsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.TokenService
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.UsuarioService
import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import io.github.kingg22.api.vacunas.panama.util.logger
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
import reactor.core.publisher.Mono
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
     * @return [ApiResponse] with new access_token and refresh_token.
     */
    @PostMapping("/refresh")
    fun refreshToken(@AuthenticationPrincipal jwt: Jwt, request: ServerHttpRequest): Mono<ResponseEntity<ApiResponse>> {
        val apiResponse = createResponse()
        val userId = jwt.subject

        try {
            val key = "token:refresh:$userId:${jwt.id}"
            val userOpt = usuarioService.getUsuarioById(UUID.fromString(userId))
            var delete = false
            userOpt.ifPresentOrElse({
                apiResponse.addData(tokenService.generateTokens(userOpt.get().toUsuarioDto()))
                apiResponse.addStatusCode(HttpStatus.OK)
                delete = true
            }) {
                apiResponse.addError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.NOT_FOUND)
                        withMessage("Usuario no encontrado, intente nuevamente")
                    },
                )
                apiResponse.addStatusCode(HttpStatus.NOT_FOUND)
            }
            return if (delete) {
                redisTemplate.delete(key).then(sendResponse(apiResponse, request))
            } else {
                sendResponse(apiResponse, request)
            }
        } catch (e: IllegalArgumentException) {
            log.error("Error while refreshing token to {}", userId, e)
            apiResponse.addStatus("message", "Invalid token")
            apiResponse.addStatusCode(HttpStatus.FORBIDDEN)
            return sendResponse(apiResponse, request)
        }
    }
}
