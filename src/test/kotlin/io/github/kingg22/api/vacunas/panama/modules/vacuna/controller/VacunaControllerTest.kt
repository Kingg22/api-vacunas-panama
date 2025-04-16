package io.github.kingg22.api.vacunas.panama.modules.vacuna.controller

import io.github.kingg22.api.vacunas.panama.TestcontainersConfiguration
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.InsertDosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.NumDosisEnum
import io.github.kingg22.api.vacunas.panama.util.removeMetadata
import io.github.kingg22.api.vacunas.panama.util.retrieveFileJson
import io.kotest.assertions.json.shouldEqualJson
import io.kotest.matchers.string.shouldContain
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertNotNull

@ActiveProfiles("test")
@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VacunaControllerTest @Autowired constructor(
    private val webTestClient: WebTestClient,
    private val jdbcTemplate: JdbcTemplate,
) {
    @Test
    fun getVacunas() {
        val expectedJson = retrieveFileJson("/responses/vacunas/get_vacunas_fabricante.json")

        webTestClient.get()
            .uri("/vaccines")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldEqualJson expectedJson
            }
    }

    // crear dosis con datos válidos debe retornar CREATED
    @Test
    @WithMockUser(
        username = "f087b04c-02de-4d13-acdb-3e837d21538b",
        roles = ["DOCTOR", "ENFERMERA"],
    )
    fun createDosis() {
        // 1. Insertar paciente
        val pacienteId = UUID.fromString("f087b04c-02de-4d13-acdb-3e837d21538b")
        val direccionPorRegistrar = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        jdbcTemplate.update(
            """INSERT INTO personas (id, nombre, cedula, fecha_nacimiento, sexo, direccion)
           VALUES (?, 'Test Paciente', '1-123-456', '1990-01-01', 'M', ?)""",
            pacienteId,
            direccionPorRegistrar,
        )
        jdbcTemplate.update("""INSERT INTO pacientes (id) VALUES (?)""", pacienteId)

        val vacunaId = UUID.fromString("123e4567-e89b-12d3-a456-426614174004") // Id vacuna existente (COVID)
        val sedeId = UUID.fromString("e2ff1bb7-0031-40b8-9c45-71efb79fa14e") // Id sede existente (H Santo Tomás)
        val doctorId = null // Id doctor existente

        val insertDto = InsertDosisDto(
            pacienteId = pacienteId,
            fechaAplicacion = LocalDateTime.now(),
            numeroDosis = NumDosisEnum.PRIMERA_DOSIS,
            vacunaId = vacunaId,
            sedeId = sedeId,
            lote = null,
            doctorId = doctorId,
        )

        webTestClient.post()
            .uri("/vaccines/create-dosis")
            .bodyValue(insertDto)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .consumeWith {
                val responseBody = it.responseBody?.toString(Charsets.UTF_8)
                assertNotNull(responseBody)
                responseBody.removeMetadata() shouldContain "\"dosis\""
            }
    }
}
