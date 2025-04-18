package io.github.kingg22.api.vacunas.panama.modules.paciente.controller

import io.github.kingg22.api.vacunas.panama.TestcontainersConfiguration
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
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
class PacienteControllerTest @Autowired constructor(private val webTestClient: WebTestClient) {
    @Test
    fun getPaciente() {
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
            .uri("/patient")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldContain "\"view_vacuna_enfermedad\""
            }
    }
}
