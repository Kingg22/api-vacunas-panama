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
        assertEquals(sede.id, dto.entidad.id)
        assertEquals(sede.nombre, dto.entidad.nombre)
        assertEquals(sede.correo, dto.entidad.correo)
        assertEquals(sede.telefono, dto.entidad.telefono)
        assertEquals(sede.dependencia, dto.entidad.dependencia)
        assertEquals(sede.estado, dto.entidad.estado)
        assertEquals(sede.disabled, dto.entidad.disabled)
        assertEquals(sede.region, dto.region)
        assertEquals(sede.createdAt, dto.createdAt)
        assertEquals(sede.updatedAt, dto.updatedAt)

        // Verificar conversión de direccion a DireccionDto
        assertNotNull(dto.entidad.direccion)
        assertEquals(sede.direccion.id, dto.entidad.direccion?.id)
        assertEquals(sede.direccion.direccion, dto.entidad.direccion?.direccion)
        assertEquals(sede.direccion.createdAt, dto.entidad.direccion?.createdAt)
        assertEquals(sede.direccion.updatedAt, dto.entidad.direccion?.updatedAt)
        assertNull(dto.entidad.direccion?.distrito)
    }
}
