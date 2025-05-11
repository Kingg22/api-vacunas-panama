package io.github.kingg22.api.vacunas.panama.modules.direccion.controller

import io.github.kingg22.api.vacunas.panama.TestBase
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.github.kingg22.api.vacunas.panama.util.retrieveFileJson
import io.kotest.assertions.json.shouldEqualJson
import io.quarkus.test.junit.QuarkusTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertNotNull

@QuarkusTest
class DireccionControllerTest : TestBase() {

    @Test
    fun getProvincias() {
        val expectedJson = retrieveFileJson("responses/direccion/get_provincias.json")

        val responseBody = When {
            get("/direccion/provincias")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldEqualJson expectedJson
    }

    @Test
    fun getDistritos() {
        val expectedJson = retrieveFileJson("responses/direccion/get_distritos.json")

        val responseBody = When {
            get("/direccion/distritos")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldEqualJson expectedJson
    }
}
