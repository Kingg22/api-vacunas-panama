package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

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
class RolesPermisosControllerTest : TestBase() {

    @Test
    fun getRoles() {
        val expectedJson = retrieveFileJson("responses/usuario/get_roles.json")

        val responseBody = When {
            get("/roles")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldEqualJson expectedJson
    }

    @Test
    fun getPermisos() {
        val expectedJson = retrieveFileJson("responses/usuario/get_permisos.json")

        val responseBody = When {
            get("/roles/permisos")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldEqualJson expectedJson
    }
}
