package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.TestBase
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.vertx.RunOnVertxContext
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull

@QuarkusTest
class BulkControllerTest : TestBase() {

    @Test
    @RunOnVertxContext
    @Ignore("Until set password encoder")
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

        val responseBody = Given {
            contentType(ContentType.JSON)
            body(json)
        } When {
            post("/bulk/paciente-usuario-direccion")
        } Then {
            statusCode(HttpStatus.SC_CREATED)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody
            .removeMetadata()
            .shouldContain("\"persona\"")
            .shouldNotContain("\"usuario\":null".toRegex())
            .shouldContain("\"direccion\"")
    }
}
