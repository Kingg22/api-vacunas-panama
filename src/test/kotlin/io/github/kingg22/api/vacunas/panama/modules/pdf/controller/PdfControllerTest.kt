package io.github.kingg22.api.vacunas.panama.modules.pdf.controller

import io.github.kingg22.api.vacunas.panama.TestBase
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.kotest.matchers.string.shouldContain
import io.quarkus.test.junit.QuarkusTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.hamcrest.Matchers.containsString
import kotlin.test.Test
import kotlin.test.assertNotNull

@QuarkusTest
class PdfControllerTest : TestBase() {

    @Test
    fun getPdfFile() {
        Given {
            authenticateRequest()
            queryParam("idVacuna", "123e4567-e89b-12d3-a456-426614174004") // COVID
        } When {
            get("/pdf")
        } Then {
            statusCode(HttpStatus.SC_OK)
            contentType("application/pdf")
            header("Content-Disposition", containsString("certificado_vacunas"))
        } Extract {
            assertNotNull(body().asString())
        }
    }

    @Test
    fun getPdfBase64() {
        val responseBody = Given {
            authenticateRequest()
            queryParam("idVacuna", "123e4567-e89b-12d3-a456-426614174004") // COVID
        } When {
            get("/pdf/base64")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldContain "\"id_certificado\"" shouldContain "\"pdf\""
    }
}
