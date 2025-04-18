package io.github.kingg22.api.vacunas.panama.modules.pdf.controller

import io.github.kingg22.api.vacunas.panama.TestcontainersConfiguration
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
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
class PdfControllerTest @Autowired constructor(private val webTestClient: WebTestClient) {
    @Test
    fun getPdfFile() {
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
            .uri {
                it.path("/pdf")
                    .queryParam("idVacuna", "123e4567-e89b-12d3-a456-426614174004") // COVID
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_PDF_VALUE)
            .expectHeader().value(HttpHeaders.CONTENT_DISPOSITION) {
                assertNotNull(it)
                it shouldContain "certificado_vacunas"
            }
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
            }
    }

    @Test
    fun getPdfBase64() {
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
            .uri {
                it.path("/pdf/base64")
                    .queryParam("idVacuna", "123e4567-e89b-12d3-a456-426614174004") // COVID
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldContain "\"id_certificado\"" shouldContain "\"pdf\""
            }
    }
}
