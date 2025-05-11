package io.github.kingg22.api.vacunas.panama

import io.github.kingg22.api.vacunas.panama.util.HaveIBeenPwnedPasswordChecker
import io.github.kingg22.api.vacunas.panama.util.extractJsonToken
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.coEvery
import io.mockk.mockk
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.config.LogConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.filter.log.LogDetail
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.specification.RequestSpecification
import org.apache.http.HttpStatus
import org.junit.jupiter.api.BeforeAll
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertNotNull

/** Base class for all tests that need to interact with the API. */
@QuarkusTest
abstract class TestBase {
    companion object {
        @JvmStatic
        @BeforeAll
        fun setupRestAssured() {
            // Configure Rest-Assured
            RestAssured.basePath = "/vacunacion/v1"
            RestAssured.config = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL))
        }
    }

    @BeforeTest
    fun setupTest() {
        // Additional setup for each test if needed
    }

    @AfterTest
    fun tearDownTest() {
        // Clean up after each test if needed
    }

    /**
     * Sends a login request to the authentication endpoint and retrieves the response.
     * This method creates a login payload with a predefined username and password,
     * performs a POST request to the login endpoint, and extracts the response body as a string.
     *
     * @return The response received from the login endpoint as a string.
     */
    protected fun getLoginResponse(): String {
        val pwChecker = mockk<HaveIBeenPwnedPasswordChecker>()
        coEvery { pwChecker.isPasswordCompromised(any()) } returns false
        val loginDto =
            """
                {
                    "username": "1-123-456",
                    "password": "prue2*test"
                }
            """.trimIndent()

        // Primero: Hacemos login
        val loginResponse = Given {
            contentType(ContentType.JSON)
            body(loginDto)
        } When {
            post("/account/login")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }
        assertNotNull(loginResponse)
        return loginResponse
    }

    /**
     * Retrieves an access token from the login response.
     * The method sends a login request, extracts the access token from
     * the JSON response, ensures its presence, and returns it.
     *
     * @return The retrieved access token as a non-blank string.
     */
    private fun getAccessToken(): String {
        val loginResponse = getLoginResponse()
        // Extraemos el token de acceso
        val accessToken = loginResponse.extractJsonToken("$.data.access_token")

        // Aseguramos que el token existe
        accessToken.shouldNotBeBlank()
        return accessToken
    }

    /**
     * Adds an "Authorization" header with a Bearer token to the current request specification.
     * The token is automatically retrieved using the [getAccessToken] method to ensure proper authentication.
     *
     * @return The updated request specification with the "Authorization" header included.
     */
    fun RequestSpecification.authenticateRequest(): RequestSpecification = this
        .header("Authorization", "Bearer ${getAccessToken()}")
}
