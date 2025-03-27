package io.github.kingg22.api.vacunas.panama.persistence.entity

import io.github.kingg22.api.vacunas.panama.web.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.web.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.web.dto.ProvinciaDto
import io.github.kingg22.api.vacunas.panama.web.dto.toDireccion
import io.github.kingg22.api.vacunas.panama.web.dto.toDistrito
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DireccionTests {

    @Test
    fun `test entity to DTO mapping`() {
        val entity = Direccion(
            id = UUID.randomUUID(),
            direccion = "Calle 123",
            distrito = Distrito(nombre = "Panama", provincia = Provincia(nombre = "Panama")),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        val dto = entity.toDireccionDto()

        assertEquals(entity.id, dto.id)
        assertEquals(entity.direccion, dto.direccion)
        assertEquals(entity.createdAt, dto.createdAt)
        assertEquals(entity.updatedAt, dto.updatedAt)
        assertNotNull(dto.distrito)
        assertEquals(entity.distrito?.nombre, dto.distrito?.nombre)
        assertEquals(entity.distrito?.id, dto.distrito?.id)
        assertEquals(entity.distrito?.provincia?.id, dto.distrito?.provincia?.id)
        assertEquals(entity.distrito?.provincia?.nombre, dto.distrito?.provincia?.nombre)
    }

    @Test
    fun `test DTO to entity mapping`() {
        val dto = DireccionDto(
            id = UUID.randomUUID(),
            direccion = "Calle 123",
            distrito = DistritoDto(nombre = "Panama", provincia = ProvinciaDto(nombre = "Panama")),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        val entity = dto.toDireccion()

        assertEquals(dto.id, entity.id)
        assertEquals(dto.direccion, entity.direccion)
        assertEquals(dto.createdAt, entity.createdAt)
        assertEquals(dto.updatedAt, entity.updatedAt)
        assertNotNull(dto.distrito)
        assertEquals(dto.distrito?.nombre, entity.distrito?.nombre)
        assertEquals(dto.distrito?.id, entity.distrito?.id)
        assertEquals(dto.distrito?.provincia?.id, entity.distrito?.provincia?.id)
        assertEquals(dto.distrito?.provincia?.nombre, entity.distrito?.provincia?.nombre)
    }

    @Test
    fun `test DTO to entity mapping with null values`() {
        val dto = DireccionDto()

        val entity = dto.toDireccion()

        assertNull(entity.id)
        assertEquals(dto.id, entity.id)
        assertNotNull(entity.direccion)
        assertEquals(dto.direccion, entity.direccion)
        assertEquals(entity.direccion, DireccionDto.DEFAULT_DIRECCION)
        assertEquals(dto.createdAt, entity.createdAt)
        assertNull(entity.updatedAt)
        assertEquals(dto.updatedAt, entity.updatedAt)
        assertNull(entity.distrito)
    }

    @Test
    fun `test distrito DTO and provincia DTO to entity mapping with default values`() {
        val dto = DistritoDto()

        val entity = dto.toDistrito()

        assertNull(entity.id)
        assertEquals(dto.id, entity.id)
        assertEquals(dto.nombre, entity.nombre)
        assertEquals(entity.nombre, DistritoDto.DEFAULT_DISTRITO)
        assertNull(entity.provincia.id)
        assertEquals(entity.provincia.id, dto.provincia.id)
        assertEquals(entity.provincia.nombre, dto.provincia.nombre)
        assertEquals(entity.provincia.nombre, ProvinciaDto.DEFAULT_PROVINCIA)
    }
}
