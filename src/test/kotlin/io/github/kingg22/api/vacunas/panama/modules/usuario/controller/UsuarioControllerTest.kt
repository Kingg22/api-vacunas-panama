package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.TestcontainersConfiguration
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsuarioControllerTest @Autowired constructor(private val webTestClient: WebTestClient) {
    @Test
    fun register() {
        val registerUserDto =
            """
                {
                    "usuario": {
                        "password": "aSecu*redPa2sword",
                        "roles": [
                            {
                                "id": 1
                            }
                        ]
                    },
                    "cedula": "1-132-652"
                }
            """.trimIndent()

        webTestClient
            .post()
            .uri("/account/register")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(registerUserDto)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldContain "\"persona\"" shouldContain "\"1-0132-000652\""
            }
    }

    @Test
    fun login() {
        val loginDto =
            """
                {
                    "username": "1-123-456",
                    "password": "prue2*test"
                }
            """.trimIndent()

        webTestClient
            .post()
            .uri("/account/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginDto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldContain "\"access_token\"" shouldContain "\"persona\"" shouldContain
                    "\"refresh_token\""
            }
    }

    @Test
    fun restore() {
        val restoreDto =
            """
                {
                    "username": "1-123-456",
                    "new_password": "new*Secure2dPassword",
                    "fecha_nacimiento": "1990-01-01T00:00:00"
                }
            """.trimIndent()

        webTestClient
            .patch()
            .uri("/account/restore")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(restoreDto)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
            }
    }

    @Test
    fun profile() {
        webTestClient
            .mutateWith(
                mockJwt().jwt {
                    it.jti(UUID.randomUUID().toString())
                    it.expiresAt(Instant.now().plusSeconds(3600))
                    it.subject("577ff96c-533d-4b14-a0e3-04bd03f0e830")
                    it.claim("persona", "901e153d-4c1a-46bf-906b-eee22007c835")
                }.authorities(SimpleGrantedAuthority("ROLE_PACIENTE"), SimpleGrantedAuthority("PACIENTE_READ")),
            )
            .get()
            .uri("/account")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldContain "\"persona\""
            }
    }
}
