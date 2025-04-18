package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.TestcontainersConfiguration
import io.github.kingg22.api.vacunas.panama.util.extractJsonToken
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TokenControllerTest @Autowired constructor(private val webTestClient: WebTestClient) {
    @Test
    fun refreshToken() {
        val loginDto =
            """
                {
                    "username": "1-123-456",
                    "password": "prue2*test"
                }
            """.trimIndent()

        // Primero: Hacemos login
        val accessToken = webTestClient
            .post()
            .uri("/account/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginDto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.data.access_token").value { token: String ->
                assertNotNull(token)
            }
            .shouldNotBeNull()
            .returnResult()
            .responseBody
            ?.let { body ->
                val json = String(body)
                json.extractJsonToken("$.data.access_token")
            }

        // Aseguramos que el token existe
        assertNotNull(accessToken)
        accessToken.shouldNotBeBlank()

        // Segundo: Usamos ese token para pedir refresh
        webTestClient
            .post()
            .uri("/token/refresh")
            .headers {
                it.setBearerAuth(accessToken)
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldContain "\"access_token\"" shouldContain "\"refresh_token\""
            }
    }
}
