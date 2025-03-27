package io.github.kingg22.api.vacunas.panama.persistence.entity

import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FabricanteTests {
    @Test
    fun `map to dto successful`() {
        // Arrange: Crear un objeto Fabricante con datos de prueba
        val direccion = Direccion(
            id = UUID.randomUUID(),
            direccion = "Avenida Principal",
            distrito = Distrito(1, "Ciudad de Panamá", provincia = Provincia(1, "Panamá")),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        val fabricante = Fabricante(
            nombre = "BioTech Inc.",
            estado = "Activo",
            direccion = direccion,
            licencia = "BIO-1234/DNFD",
            contactoNombre = "Juan Pérez",
            contactoCorreo = "juan.perez@biotech.com",
            contactoTelefono = "+50760001122",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Act: Convertir a DTO
        val dto = fabricante.toFabricanteDto()

        // Assert: Verificar que los valores se mapean correctamente
        assertNotNull(dto)
        assertEquals(fabricante.id, dto.entidad.id)
        assertEquals(fabricante.nombre, dto.entidad.nombre)
        assertEquals(fabricante.correo, dto.entidad.correo)
        assertEquals(fabricante.telefono, dto.entidad.telefono)
        assertEquals(fabricante.dependencia, dto.entidad.dependencia)
        assertEquals(fabricante.estado, dto.entidad.estado)
        assertEquals(fabricante.disabled, dto.entidad.disabled)
        assertEquals(fabricante.licencia, dto.licencia)
        assertEquals(fabricante.contactoNombre, dto.contactoNombre)
        assertEquals(fabricante.contactoCorreo, dto.contactoCorreo)
        assertEquals(fabricante.contactoTelefono, dto.contactoTelefono)
        assertEquals(fabricante.createdAt, dto.createdAt)
        assertEquals(fabricante.updatedAt, dto.updatedAt)

        // Verificar conversión de direccion a DireccionDto
        assertNotNull(dto.entidad.direccion)
        assertEquals(fabricante.direccion.id, dto.entidad.direccion?.id)
        assertEquals(fabricante.direccion.createdAt, dto.entidad.direccion?.createdAt)
        assertEquals(fabricante.direccion.updatedAt, dto.entidad.direccion?.updatedAt)
        assertNotNull(dto.entidad.direccion?.distrito)
        assertEquals(fabricante.direccion.distrito?.id, dto.entidad.direccion?.distrito?.id)
        assertEquals(fabricante.direccion.distrito?.nombre, dto.entidad.direccion?.distrito?.nombre)
        assertNotNull(dto.entidad.direccion?.distrito?.provincia)
        assertEquals(fabricante.direccion.distrito?.provincia?.id, dto.entidad.direccion?.distrito?.provincia?.id)
        assertEquals(
            fabricante.direccion.distrito?.provincia?.nombre,
            dto.entidad.direccion?.distrito?.provincia?.nombre,
        )
    }
}
