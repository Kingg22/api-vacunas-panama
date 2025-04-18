package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.TestcontainersConfiguration
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BulkControllerTest @Autowired constructor(private val webTestClient: WebTestClient) {
    @Test
    fun createPacienteUsuario() {
        val json =
            """
            {
                "fecha_nacimiento": "2000-12-12T00:00:00",
                "cedula": "1-123-1432",
                "nombre": "Test",
                "apellido1": "Prueba",
                "sexo": "M",
                "estado": "ACTIVO",
                "disabled": false,
                "usuario": {
                  "password": "aSecu*redPa2sword",
                  "roles": [
                    { "id": 1 }
                  ]
                }
            }
            """.trimIndent()

        webTestClient.post()
            .uri("/bulk/paciente-usuario-direccion")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldContain "\"persona\""
            }
    }
}
