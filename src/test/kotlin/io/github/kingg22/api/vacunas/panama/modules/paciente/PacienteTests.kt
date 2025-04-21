package io.github.kingg22.api.vacunas.panama.modules.paciente

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.toPacienteDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PacienteTests {
    @Test
    fun `debe mapear Paciente a PacienteDto correctamente`() {
        // Arrange: Crear un objeto Direccion con datos de prueba
        val distrito = Distrito(0, Provincia(0, "Por registrar"), nombre = "Por registrar")
        val direccion = Direccion(
            id = UUID.randomUUID(),
            descripcion = "Avenida Principal, Edificio X",
            distrito = distrito,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Crear un objeto Paciente con datos de prueba
        val paciente = Paciente(
            persona = Persona(
                estado = "Activo",
                direccion = direccion,
            ),
            identificacionTemporal = "123456789",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Act: Convertir a DTO usando el método generado por Konvert
        val dto = paciente.toPacienteDto()

        // Assert: Verificar que los valores se mapean correctamente
        assertNotNull(dto)
        assertEquals(paciente.id, dto.persona.id)
        assertEquals(paciente.persona.estado, dto.persona.estado)
        assertEquals(paciente.identificacionTemporal, dto.identificacionTemporal)
        assertEquals(paciente.createdAt, dto.createdAt)
        assertEquals(paciente.updatedAt, dto.updatedAt)

        // Verificar conversión de direccion a DireccionDto
        assertNotNull(dto.persona.direccion)
        assertEquals(paciente.persona.direccion.id, dto.persona.direccion.id)
        assertEquals(paciente.persona.direccion.descripcion, dto.persona.direccion.descripcion)
        assertEquals(paciente.persona.direccion.createdAt, dto.persona.direccion.createdAt)
        assertEquals(paciente.persona.direccion.updatedAt, dto.persona.direccion.updatedAt)
        assertEquals(distrito.id, dto.persona.direccion.distrito.id)
        assertEquals(distrito.nombre, dto.persona.direccion.distrito.nombre)
        assertEquals(distrito.provincia.id, dto.persona.direccion.distrito.provincia.id)
        assertEquals(distrito.provincia.nombre, dto.persona.direccion.distrito.provincia.nombre)
    }
}
