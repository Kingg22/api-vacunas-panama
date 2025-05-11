package io.github.kingg22.api.vacunas.panama.modules.vacuna.controller

import io.github.kingg22.api.vacunas.panama.TestBase
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.InsertDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.NumDosisEnum
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.github.kingg22.api.vacunas.panama.util.retrieveFileJson
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.string.shouldContain
import io.quarkus.test.junit.QuarkusTest
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.apache.http.HttpStatus
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertNotNull

@QuarkusTest
class VacunaControllerTest : TestBase() {

    @Test
    fun getVacunas() {
        val expectedJson = retrieveFileJson("/responses/vacunas/get_vacunas_fabricante.json")

        val responseBody = When {
            get("/vaccines")
        } Then {
            statusCode(HttpStatus.SC_OK)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldEqualJson expectedJson
    }

    // crear dosis con datos válidos debe retornar CREATED
    @Ignore("Until create a new user with role DOCTOR and ENFERMERA")
    @Test
    fun createDosis() {
        /*
            @WithMockUser(
                username = "f087b04c-02de-4d13-acdb-3e837d21538b",
                roles = ["DOCTOR", "ENFERMERA"],
            )
         */
        val pacienteId = UUID.fromString("901e153d-4c1a-46bf-906b-eee22007c835") // Paciente Test
        val vacunaId = UUID.fromString("123e4567-e89b-12d3-a456-426614174004") // Id vacuna existente (COVID)
        val sedeId = UUID.fromString("e2ff1bb7-0031-40b8-9c45-71efb79fa14e") // Id sede existente (H Santo Tomás)
        val doctorId = null // Id doctor existente

        val insertDto = InsertDosisDto(
            pacienteId = pacienteId,
            fechaAplicacion = LocalDateTime.now(),
            numeroDosis = NumDosisEnum.SEGUNDA_DOSIS, // Primera dosis ya insertada en la base de datos
            vacunaId = vacunaId,
            sedeId = sedeId,
            lote = null,
            doctorId = doctorId,
        )

        val responseBody = Given {
            contentType(ContentType.JSON)
            body(insertDto)
        } When {
            post("/vaccines/create-dosis")
        } Then {
            statusCode(HttpStatus.SC_CREATED)
        } Extract {
            body().asString()
        }

        assertNotNull(responseBody)
        responseBody.removeMetadata() shouldContain "\"dosis\""
    }
}
