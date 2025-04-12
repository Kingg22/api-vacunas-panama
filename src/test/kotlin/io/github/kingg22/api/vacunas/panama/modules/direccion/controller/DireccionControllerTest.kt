package io.github.kingg22.api.vacunas.panama.modules.direccion.controller

import io.github.kingg22.api.vacunas.panama.util.BaseIntegrationTest
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.github.kingg22.api.vacunas.panama.util.retrieveFileJson
import io.kotest.assertions.json.shouldEqualJson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.test.Test
import kotlin.test.assertNotNull

class DireccionControllerTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun getProvincias() {
        val expectedJson = retrieveFileJson("responses/direccion/get_provincias.json")

        webTestClient.get()
            .uri("/direccion/provincias")
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
    fun getDistritos() {
        val expectedJson = retrieveFileJson("responses/direccion/get_distritos.json")

        webTestClient.get()
            .uri("/direccion/distritos")
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
