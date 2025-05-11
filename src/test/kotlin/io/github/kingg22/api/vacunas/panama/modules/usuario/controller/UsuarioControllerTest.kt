package io.github.kingg22.api.vacunas.panama.modules.usuario.controller

import io.github.kingg22.api.vacunas.panama.TestBase
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.kotest.matchers.string.shouldContain
import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertNotNull

@QuarkusTest
class UsuarioControllerTest : TestBase() {

    @Test
    fun register() {
        val registerUserDto =
            """
                {
                    "usuario": {
                        "password": "aSecu*redPa2sword",
                        "roles": [
                            {
                                "id": 1
                            }
                        ]
                    },
                    "cedula": "1-132-652"
                }
            """.trimIndent()

        val responseBody = Given {
            contentType(ContentType.JSON)
            body(registerUserDto)
        } When {
            post("/account/register")
        } Then {
            statusCode(HttpStatus.SC_CREATED)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldContain "\"persona\"" shouldContain "\"1-0132-000652\""
    }

    @Test
    fun login() {
        val loginDto =
            """
                {
                    "username": "1-123-456",
                    "password": "prue2*test"
                }
            """.trimIndent()

        val responseBody = Given {
            contentType(ContentType.JSON)
            body(loginDto)
        } When {
            post("/account/login")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata()
            .shouldContain("\"access_token\"")
            .shouldContain("\"persona\"")
            .shouldContain("\"refresh_token\"")
    }

    @Test
    fun restore() {
        val restoreDto =
            """
                {
                    "username": "1-123-456",
                    "new_password": "new*Secure2dPassword",
                    "fecha_nacimiento": "1990-01-01"
                }
            """.trimIndent()

        val responseBody = Given {
            contentType(ContentType.JSON)
            body(restoreDto)
        } When {
            patch("/account/restore")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
    }

    @Test
    fun profile() {
        val responseBody = Given {
            authenticateRequest()
        } When {
            get("/account")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldContain "\"persona\""

        /* validate last used is updated
        val now = Instant.now().minus(1, java.time.temporal.ChronoUnit.MINUTES)
        val lastUsedString = responseBody.extractJsonToken("$.data.persona.usuario.last_used")
            .shouldNotBeBlank().shouldNotBeNull()
        val lastUsed = Instant.parse(lastUsedString)
        lastUsed.shouldNotBeNull()
        lastUsed.isAfter(now).shouldNotBeNull().shouldBeTrue()
         */
    }
}
