package io.github.kingg22.api.vacunas.panama.modules.sede.controller

import io.github.kingg22.api.vacunas.panama.util.BaseIntegrationTest
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.github.kingg22.api.vacunas.panama.util.retrieveFileJson
import io.kotest.assertions.json.shouldEqualJson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull

@Ignore("Sede data init database is not ready")
class SedeControllerTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun getSedes() {
        val expectedJson = retrieveFileJson("responses/sedes/get_sedes.json")

        webTestClient.get()
            .uri("/sedes")
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
