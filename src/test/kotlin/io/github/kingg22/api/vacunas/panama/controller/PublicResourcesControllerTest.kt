package io.github.kingg22.api.vacunas.panama.controller

import io.github.kingg22.api.vacunas.panama.TestBase
import io.kotest.matchers.string.shouldNotBeBlank
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import org.springframework.boot.test.web.server.LocalServerPort
import kotlin.test.Test
import kotlin.test.assertNotNull

class PublicResourcesControllerTest(@LocalServerPort port: Int) : TestBase(port) {
    @Test
    fun getDistritos() {
        val responseBody = When {
            get("/public/distritos")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody).shouldNotBeBlank()
    }

    @Test
    fun getProvincias() {
        val responseBody = When {
            get("/public/provincias")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody).shouldNotBeBlank()
    }

    @Test
    fun getSedes() {
        val responseBody = When {
            get("/public/sedes")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody).shouldNotBeBlank()
    }

    @Test
    fun getVacunas() {
        val responseBody = When {
            get("/public/vacunas")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody).shouldNotBeBlank()
    }

    @Test
    fun getRoles() {
        val responseBody = When {
            get("/public/roles")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody).shouldNotBeBlank()
    }

    @Test
    fun getPermisos() {
        val responseBody = When {
            get("/public/roles/permisos")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody).shouldNotBeBlank()
    }
}
