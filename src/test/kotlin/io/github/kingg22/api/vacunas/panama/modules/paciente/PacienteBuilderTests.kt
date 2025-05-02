package io.github.kingg22.api.vacunas.panama.modules.paciente

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PacienteBuilderTests {
    @Test
    fun build() {
        val direccion = Direccion(
            descripcion = "prueba",
            distrito = Distrito(0, provincia = Provincia(0, "Por registrar"), nombre = "Por registrar"),
        )

        val fecha = LocalDateTime.MIN
        val persona = Persona(
            estado = "prueba",
            direccion = direccion,
        )
        val paciente = Paciente(persona = persona, createdAt = fecha)
        val pacienteBuilder = Paciente(
            persona = Persona(
                estado = persona.estado,
                direccion = direccion,
            ),
            createdAt = fecha,
        )
        paciente shouldBe pacienteBuilder
    }
}
