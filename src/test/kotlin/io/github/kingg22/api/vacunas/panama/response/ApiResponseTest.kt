package io.github.kingg22.api.vacunas.panama.response

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.json.shouldBeJsonObject
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.RoutingContext
import java.io.Serializable
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ApiResponseTest {
    private lateinit var request: RoutingContext

    @BeforeTest
    fun retrieveMockRequest() {
        // Crear un mock de ServerHttpRequest
        val serverHttpRequest = mockk<RoutingContext>()

        // Crear un mock de URI para evitar NullPointerException al acceder a la ruta
        val uri = mockk<HttpServerRequest>()

        // Configurar el comportamiento del mock
        every { serverHttpRequest.request() } returns uri
        every { uri.path() } returns "/my/test/path"

        this.request = serverHttpRequest
    }

    @Test
    fun `test DefaultApiResponse`() {
        val response = ApiResponseFactory.createResponse()

        response.addStatus("testStatus", "testValue")
        response.addMetadata("testMetadata", 123)
        response.addData("testData", true)
        response.addError(
            ApiResponseFactory.createApiErrorBuilder {
                withCode("errorCode")
                withProperty("testProperty")
                withMessage("errorMessage")
            },
        )
        response.addWarning(DefaultApiErrorTest("warningCode", "testProperty", "warningMessage"))
        response.addStatusCode(200)

        ApiResponseUtil.setMetadata(response, request)

        assertEquals("testValue", response.status["testStatus"])
        assertEquals(123, response.metadata["testMetadata"])
        assertEquals(true, response.data["testData"])
        assertEquals("errorCode", response.errors[0].code)
        assertEquals("warningCode", response.warnings[0].code)
        assertEquals(200, response.retrieveStatusCode())
    }

    @Test
    fun `test ApiResponseBuilder`() {
        val response = ApiResponseFactory.createResponseBuilder {
            withData("testData", "testValue")
            withError {
                withCode(ApiResponseCode.NOT_FOUND)
                withMessage("Error message")
                withProperty("testProperty")
            }
            withWarning("warningCode", "Warning message")
            withStatus("testStatus", 123)
            withStatusCode(404)
        }

        assertEquals("testValue", response.data["testData"])
        assertEquals(ApiResponseCode.NOT_FOUND.toString(), response.errors[0].code)
        assertEquals("warningCode", response.warnings[0].code)
        assertEquals(123, response.status["testStatus"])
        assertEquals(404, response.retrieveStatusCode())
    }

    @Test
    fun `test ApiResponse External`() {
        val response = ApiResponseFactory.createResponseBuilder {
            withError(DefaultApiErrorTest("errorCode", "testProperty", "errorMessage"))
        }
        val responseWithErrors = response.returnIfErrors()
        assertEquals(response, responseWithErrors)
        assertTrue { response.hasErrors() }

        val responseWithoutErrors = ApiResponseFactory.createResponse().returnIfErrors()
        assertEquals(null, responseWithoutErrors)
    }

    @Test
    fun `test ApiResponseUtil`() {
        val response = ApiResponseFactory.createResponse()
        ApiResponseUtil.setMetadata(response, request)

        verify(inverse = true) { request.request().path() }
    }

    @Test
    fun `test ApiErrorBuilder`() {
        val error = ApiResponseFactory.createApiErrorBuilder()
            .withCode(ApiResponseCode.NOT_FOUND)
            .withMessage("Error message")
            .withProperty("testProperty")
            .build()

        assertEquals(ApiResponseCode.NOT_FOUND.toString(), error.code)
        assertEquals("Error message", error.message)
        assertEquals("testProperty", error.property)
    }

    @Test
    fun `Test response change to builder`() {
        val response = ApiResponseFactory.createResponseBuilder {
            withData(mapOf("testData" to "testValue"))
            withStatusCode(400)
        }
        val responseWithErrors = response.returnIfErrors()
        assertNull(responseWithErrors)
        val responseBuilder = response.asBuilder()
        assertEquals(response.retrieveStatusCode(), responseBuilder.build().retrieveStatusCode())
        assertEquals<Map<String, Serializable>>(mapOf("testData" to "testValue"), responseBuilder.build().data)
        responseBuilder.withWarning(DefaultApiErrorTest("warningCode", "testProperty", "warningMessage"))
        assertEquals(1, responseBuilder.build().warnings.size)
        assertTrue { responseBuilder.build().hasWarnings() }
    }

    @Test
    fun `Test response change to builder and continue with response`() {
        var response = ApiResponseFactory.createResponse()
        response.addStatus("testStatus", "testValue")
        response = response.builder {
            withData(mapOf("testData" to "testValue"))
        }
        assertEquals<Map<String, Serializable>>(mapOf("testData" to "testValue"), response.data)
    }

    @Test
    fun `Test default http status code`() {
        val response = ApiResponseFactory.createResponseBuilder().build()
        assertEquals(500, response.retrieveStatusCode())
    }

    @Test
    fun `Test Api Error Builder throw exception if not set code and - or message`() {
        assertFailsWith(IllegalStateException::class) {
            ApiResponseFactory.createApiErrorBuilder().build()
        }
        assertFailsWith(IllegalStateException::class) {
            ApiResponseFactory.createApiErrorBuilder().withCode("Test").build()
        }
    }

    @Test
    fun `ApiResponse can be serializable`() {
        // empty
        val response = ApiResponseFactory.createResponseBuilder().build()
        val json = ObjectMapper().writeValueAsString(response)
        json.shouldNotBeNull().shouldNotBeBlank()?.shouldBeJsonObject()

        // With all fields full
        response.addStatus("testStatus", "testValue")
        response.addMetadata("testMetadata", 123)
        response.addData("testData", true)
        response.addError(
            ApiResponseFactory.createApiErrorBuilder {
                withCode("errorCode")
                withProperty("testProperty")
                withMessage("errorMessage")
            },
        )
        response.addWarning(DefaultApiErrorTest("warningCode", "testProperty", "warningMessage"))
        val newJson = ObjectMapper().writeValueAsString(response)
        newJson.shouldNotBeNull().shouldNotBeBlank()?.shouldBeJsonObject()
    }

    private class DefaultApiErrorTest(
        override val code: String,
        override val property: String?,
        override val message: String?,
    ) : ApiError
}
