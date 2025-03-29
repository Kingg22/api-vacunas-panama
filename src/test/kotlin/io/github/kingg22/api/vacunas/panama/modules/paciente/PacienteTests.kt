package io.github.kingg22.api.vacunas.panama.modules.paciente

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.toPacienteDto
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PacienteTests {
    @Test
    fun `debe mapear Paciente a PacienteDto correctamente`() {
        // Arrange: Crear un objeto Direccion con datos de prueba
        val direccion = Direccion(
            id = UUID.randomUUID(),
            direccion = "Avenida Principal, Edificio X",
            distrito = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Crear un objeto Paciente con datos de prueba
        val paciente = Paciente(
            estado = "Activo",
            direccion = direccion,
            identificacionTemporal = "123456789",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Act: Convertir a DTO usando el método generado por Konvert
        val dto = paciente.toPacienteDto()

        // Assert: Verificar que los valores se mapean correctamente
        assertNotNull(dto)
        assertEquals(paciente.id, dto.persona.id)
        assertEquals(paciente.estado, dto.persona.estado)
        assertEquals(paciente.identificacionTemporal, dto.identificacionTemporal)
        assertEquals(paciente.createdAt, dto.createdAt)
        assertEquals(paciente.updatedAt, dto.updatedAt)

        // Verificar conversión de direccion a DireccionDto
        assertNotNull(dto.persona.direccion)
        assertEquals(paciente.direccion.id, dto.persona.direccion.id)
        assertEquals(paciente.direccion.direccion, dto.persona.direccion.direccion)
        assertEquals(paciente.direccion.createdAt, dto.persona.direccion.createdAt)
        assertEquals(paciente.direccion.updatedAt, dto.persona.direccion.updatedAt)
        assertNull(dto.persona.direccion.distrito)
    }
}
