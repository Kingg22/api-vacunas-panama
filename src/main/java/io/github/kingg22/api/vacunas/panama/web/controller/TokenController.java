package io.github.kingg22.api.vacunas.panama.web.controller;

import io.github.kingg22.api.vacunas.panama.response.ApiResponse;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory;
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil;
import io.github.kingg22.api.vacunas.panama.service.IUsuarioManagementService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/vacunacion/v1/token", produces = MediaType.APPLICATION_JSON_VALUE)
public class TokenController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final IUsuarioManagementService usuarioManagementService;

    /**
     * Handles refreshing of tokens. The validation is not performed here as a security filter and OAuth ensures access
     * to this endpoint only if a valid refresh token is provided. The used refresh token is removed from memory.
     *
     * @param jwt The {@link Jwt} containing user ID.
     * @param request The {@link ServletWebRequest} used for building the response.
     * @return {@link ApiResponse} with new access_token and refresh_token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refreshToken(
            @NotNull @AuthenticationPrincipal Jwt jwt, ServletWebRequest request) {
        var apiResponse = ApiResponseFactory.createResponse();
        var userId = jwt.getSubject();
        var tokenId = jwt.getId();

        var key = "token:refresh:".concat(userId).concat(":").concat(tokenId);
        redisTemplate.delete(key);

        apiResponse.addData(this.usuarioManagementService.generateTokens(UUID.fromString(userId)));
        apiResponse.addStatusCode(HttpStatus.OK);
        return ApiResponseUtil.sendResponse(apiResponse, request);
    }
}
