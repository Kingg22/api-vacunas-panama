package io.github.kingg22.api.vacunas.panama.modules.sede.controller

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
class SedeControllerTest : TestBase() {

    @Test
    fun getSedes() {
        val expectedJson = retrieveFileJson("responses/sedes/get_sedes.json")

        val responseBody = When {
            get("/sedes")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldEqualJson expectedJson
    }
}
