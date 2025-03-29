package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.response.ApiResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil.sendResponse
import io.github.kingg22.api.vacunas.panama.service.IUsuarioManagementService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest
import java.util.UUID

@RestController
@RequestMapping(path = ["/vacunacion/v1/token"], produces = [MediaType.APPLICATION_JSON_VALUE])
class TokenController(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val usuarioManagementService: IUsuarioManagementService,
) {
    /**
     * Handles refreshing of tokens. The validation is not performed here as a security filter and OAuth ensures access
     * to this endpoint only if a valid refresh token is provided. The used refresh token is removed from memory.
     *
     * @param jwt The [Jwt] containing user ID.
     * @param request The [ServletWebRequest] used for building the response.
     * @return [ApiResponse] with new access_token and refresh_token.
     */
    @PostMapping("/refresh")
    fun refreshToken(@AuthenticationPrincipal jwt: Jwt, request: ServletWebRequest): ResponseEntity<ApiResponse> {
        val apiResponse = createResponse()
        val userId = jwt.subject
        val tokenId = jwt.id

        val key = "token:refresh:$userId:$tokenId"
        redisTemplate.delete(key)

        try {
            apiResponse.addData(usuarioManagementService.generateTokens(UUID.fromString(userId)))
        } catch (e: IllegalArgumentException) {
            apiResponse.addStatus("message", "Invalid token")
            apiResponse.addStatusCode(HttpStatus.FORBIDDEN)
            return sendResponse(apiResponse, request)
        }
        apiResponse.addStatusCode(HttpStatus.OK)
        return sendResponse(apiResponse, request)
    }
}
