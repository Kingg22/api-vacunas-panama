package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.TestcontainersConfiguration
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.github.kingg22.api.vacunas.panama.util.retrieveFileJson
import io.kotest.assertions.json.shouldEqualJson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test
import kotlin.test.assertNotNull

@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RolesPermisosControllerTest {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun getRoles() {
        val expectedJson = retrieveFileJson("responses/usuario/get_roles.json")

        webTestClient.get()
            .uri("/roles")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldEqualJson expectedJson
            }
    }

    @Test
    fun getPermisos() {
        val expectedJson = retrieveFileJson("responses/usuario/get_permisos.json")

        webTestClient.get()
            .uri("/roles/permisos")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldEqualJson expectedJson
            }
    }
}
