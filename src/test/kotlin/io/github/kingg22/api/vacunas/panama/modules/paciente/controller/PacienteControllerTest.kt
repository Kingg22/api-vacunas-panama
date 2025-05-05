package io.github.kingg22.api.vacunas.panama.modules.paciente.controller

import io.github.kingg22.api.vacunas.panama.TestBase
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.kotest.matchers.string.shouldContain
import io.quarkus.test.junit.QuarkusTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertNotNull

@QuarkusTest
class PacienteControllerTest : TestBase() {

    @Test
    fun getPaciente() {
        val responseBody = Given {
            authenticateRequest()
        } When {
            get("/patient")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldContain "\"view_vacuna_enfermedad\""
    }
}
