package io.github.kingg22.api.vacunas.panama.persistence.entity

import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DoctorTests {
    @Test
    fun `debe mapear Doctor a DoctorDto correctamente`() {
        // Arrange: Crear objetos Direccion y Sede con datos de prueba
        val direccion = Direccion(
            id = UUID.randomUUID(),
            direccion = "Calle Ficticia 123, Piso 5",
            distrito = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        val sede = Sede(
            nombre = "Sede Principal",
            estado = "Activa",
            direccion = direccion,
            region = "Centro",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Crear un objeto Doctor con datos de prueba
        val doctor = Doctor(
            estado = "Activo",
            direccion = direccion,
            idoneidad = "12345",
            categoria = "Médico General",
            sede = sede,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Act: Convertir a DTO usando el método generado por Konvert
        val dto = doctor.toDoctorDto()

        // Assert: Verificar que los valores se mapean correctamente
        assertNotNull(dto)
        assertEquals(doctor.id, dto.persona.id)
        assertEquals(doctor.estado, dto.persona.estado)
        assertEquals(doctor.idoneidad, dto.idoneidad)
        assertEquals(doctor.categoria, dto.categoria)
        assertEquals(doctor.createdAt, dto.createdAt)
        assertEquals(doctor.updatedAt, dto.updatedAt)

        // Verificar conversión de direccion a DireccionDto
        assertNotNull(dto.persona.direccion)
        assertEquals(doctor.direccion.id, dto.persona.direccion?.id)
        assertEquals(doctor.direccion.direccion, dto.persona.direccion?.direccion)
        assertEquals(doctor.direccion.createdAt, dto.persona.direccion?.createdAt)
        assertEquals(doctor.direccion.updatedAt, dto.persona.direccion?.updatedAt)
        assertNull(dto.persona.direccion?.distrito)
    }
}
