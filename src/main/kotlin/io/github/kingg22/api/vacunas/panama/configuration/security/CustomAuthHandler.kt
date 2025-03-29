package io.github.kingg22.api.vacunas.panama.configuration.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.kingg22.api.vacunas.panama.response.ApiResponseUtil
import io.github.wimdeblauwe.errorhandlingspringbootstarter.ApiErrorResponse
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.ErrorCodeMapper
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.ErrorMessageMapper
import io.github.wimdeblauwe.errorhandlingspringbootstarter.mapper.HttpStatusMapper
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class CustomAuthHandler(
    private val httpStatusMapper: HttpStatusMapper,
    private val errorCodeMapper: ErrorCodeMapper,
    private val errorMessageMapper: ErrorMessageMapper,
    private val objectMapper: ObjectMapper,
) : ServerAuthenticationEntryPoint,
    ServerAccessDeniedHandler {

    override fun commence(exchange: ServerWebExchange, exception: AuthenticationException): Mono<Void> =
        createResponse(exchange, exception)

    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> =
        createResponse(exchange, denied)

    private fun createResponse(exchange: ServerWebExchange, exception: Exception?): Mono<Void> {
        val httpStatusCode = httpStatusMapper.getHttpStatus(exception, HttpStatus.UNAUTHORIZED)
        val code = errorCodeMapper.getErrorCode(exception)
        val message = errorMessageMapper.getErrorMessage(exception)
        val errorResponse = ApiErrorResponse(httpStatusCode, code, message)

        val response = exchange.response

        response.statusCode = errorResponse.httpStatus
        response.headers.contentType = MediaType.APPLICATION_JSON

        val body = objectMapper.writeValueAsBytes(
            ApiResponseUtil.transformApiErrorResponse(
                errorResponse,
                exchange.request,
            ),
        )

        val buffer = response.bufferFactory().wrap(body)

        return response.writeWith(Mono.just(buffer))
    }
}
