package io.github.kingg22.api.vacunas.panama.persistence.entity

import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SedeTests {
    @Test
    fun `debe mapear Sede a SedeDto correctamente`() {
        // Arrange: Crear un objeto Direccion con datos de prueba
        val direccion = Direccion(
            id = UUID.randomUUID(),
            direccion = "Calle 50, Edificio Principal",
            distrito = null,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Crear un objeto Sede con datos de prueba
        val sede = Sede(
            nombre = "Sede Central",
            estado = "Operativa",
            direccion = direccion,
            region = "Metropolitana",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Act: Convertir a DTO usando el método generado por Konvert
        val dto = sede.toSedeDto()

        // Assert: Verificar que los valores se mapean correctamente
        assertNotNull(dto)
        assertEquals(sede.id, dto.id)
        assertEquals(sede.nombre, dto.nombre)
        assertEquals(sede.correo, dto.correo)
        assertEquals(sede.telefono, dto.telefono)
        assertEquals(sede.dependencia, dto.dependencia)
        assertEquals(sede.estado, dto.estado)
        assertEquals(sede.disabled, dto.disabled)
        assertEquals(sede.region, dto.region)
        assertEquals(sede.createdAt, dto.createdAt)
        assertEquals(sede.updatedAt, dto.updatedAt)

        // Verificar conversión de direccion a DireccionDto
        assertNotNull(dto.direccion)
        assertEquals(sede.direccion.id, dto.direccion?.id)
        assertEquals(sede.direccion.direccion, dto.direccion?.direccion)
        assertEquals(sede.direccion.createdAt, dto.direccion?.createdAt)
        assertEquals(sede.direccion.updatedAt, dto.direccion?.updatedAt)
        assertNull(dto.direccion?.distrito)
    }
}
