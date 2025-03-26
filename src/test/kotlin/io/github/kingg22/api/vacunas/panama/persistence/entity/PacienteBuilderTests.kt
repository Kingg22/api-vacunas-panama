package io.github.kingg22.api.vacunas.panama.persistence.entity

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PacienteBuilderTests {
    @Test
    fun build() {
        val direccion = Direccion()
        direccion.direccion = "prueba"

        val fecha = LocalDateTime.MIN
        val persona = Persona.builder {
            estado = "prueba"
            this.direccion = direccion
        }
        val paciente = Paciente(persona, createdAt = fecha)
        val pacienteBuilder = Paciente.builder {
            estado = persona.estado
            this.direccion = direccion
            createdAt = fecha
        }
        assertThat(paciente).usingRecursiveComparison().isEqualTo(pacienteBuilder)
    }
}
