package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.TestBase
import io.github.kingg22.api.vacunas.panama.util.extractJsonToken
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.kotest.matchers.string.shouldBeBlank
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.Test
import kotlin.test.assertNotNull

class TokenControllerTest(@LocalServerPort port: Int) : TestBase(port) {

    @Test
    fun refreshToken() {
        val loginResponse = getLoginResponse()

        // Extraemos el token de refresh
        val refreshToken = loginResponse.extractJsonToken("$.data.refresh_token")

        // Aseguramos que el token existe
        refreshToken.shouldNotBeBlank()

        // Segundo: Usamos ese token para pedir refresh
        val responseBody = Given {
            header("Authorization", "Bearer $refreshToken")
        } When {
            post("/token/refresh")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldContain "\"access_token\"" shouldContain "\"refresh_token\""
    }

    @Test
    fun refreshTokenWithAccessTokenFail() {
        val authenticateHeader = Given {
            authenticateRequest()
        } When {
            post("/token/refresh")
        } Then {
            statusCode(HttpStatus.SC_FORBIDDEN)
        } Extract {
            body().asString().shouldBeBlank()
            header("WWW-Authenticate")
        }
        assertNotNull(authenticateHeader)
            .shouldNotBeBlank()
            .shouldContain("Access token cannot be used to refresh tokens")
    }
}
