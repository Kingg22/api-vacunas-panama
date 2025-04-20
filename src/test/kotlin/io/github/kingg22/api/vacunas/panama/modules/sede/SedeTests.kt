package io.github.kingg22.api.vacunas.panama.modules.sede

import io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.toSedeDto
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SedeTests {
    @Test
    fun `debe mapear Sede a SedeDto correctamente`() {
        // Arrange: Crear un objeto Direccion con datos de prueba
        val distrito = Distrito(0, Provincia(0, "Por registrar"), nombre = "Por registrar")
        val direccion = Direccion(
            id = UUID.randomUUID(),
            descripcion = "Calle 50, Edificio Principal",
            distrito = distrito,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Crear un objeto Sede con datos de prueba
        val sede = Sede(
            entidad = Entidad(
                nombre = "Sede Central",
                estado = "Operativa",
                direccion = direccion,
            ),
            region = "Metropolitana",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Act: Convertir a DTO usando el método generado por Konvert
        val dto = sede.toSedeDto()

        // Assert: Verificar que los valores se mapean correctamente
        assertNotNull(dto)
        assertEquals(sede.id, dto.entidad.id)
        assertEquals(sede.entidad.nombre, dto.entidad.nombre)
        assertEquals(sede.entidad.correo, dto.entidad.correo)
        assertEquals(sede.entidad.telefono, dto.entidad.telefono)
        assertEquals(sede.entidad.dependencia, dto.entidad.dependencia)
        assertEquals(sede.entidad.estado, dto.entidad.estado)
        assertEquals(sede.entidad.disabled, dto.entidad.disabled)
        assertEquals(sede.region, dto.region)
        assertEquals(sede.createdAt, dto.createdAt)
        assertEquals(sede.updatedAt, dto.updatedAt)

        // Verificar conversión de direccion a DireccionDto
        assertNotNull(dto.entidad.direccion)
        assertEquals(sede.entidad.direccion.id, dto.entidad.direccion.id)
        assertEquals(sede.entidad.direccion.descripcion, dto.entidad.direccion.descripcion)
        assertEquals(sede.entidad.direccion.createdAt, dto.entidad.direccion.createdAt)
        assertEquals(sede.entidad.direccion.updatedAt, dto.entidad.direccion.updatedAt)
        assertEquals(distrito.id, dto.entidad.direccion.distrito.id)
        assertEquals(distrito.nombre, dto.entidad.direccion.distrito.nombre)
        assertEquals(distrito.provincia.id, dto.entidad.direccion.distrito.provincia.id)
        assertEquals(distrito.provincia.nombre, dto.entidad.direccion.distrito.provincia.nombre)
    }
}
