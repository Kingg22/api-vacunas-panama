package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.modules.usuario.service.UsuarioManagementService
import io.github.kingg22.api.vacunas.panama.response.ApiResponse
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
    private val usuarioManagementService: UsuarioManagementService,
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
        val tokenId = jwt.id

        val key = "token:refresh:$userId:$tokenId"

        try {
            apiResponse.addData(usuarioManagementService.generateTokens(UUID.fromString(userId)))
        } catch (e: IllegalArgumentException) {
            log.error("Error while refreshing token to {}", userId, e)
            apiResponse.addStatus("message", "Invalid token")
            apiResponse.addStatusCode(HttpStatus.FORBIDDEN)
            return sendResponse(apiResponse, request)
        }
        apiResponse.addStatusCode(HttpStatus.OK)
        return redisTemplate.delete(key).then(sendResponse(apiResponse, request))
    }
}
