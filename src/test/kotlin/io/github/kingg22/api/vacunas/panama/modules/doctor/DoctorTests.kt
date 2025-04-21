package io.github.kingg22.api.vacunas.panama.modules.doctor

import io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.toDoctorDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DoctorTests {
    @Test
    fun `debe mapear Doctor a DoctorDto correctamente`() {
        // Arrange: Crear objetos Direccion y Sede con datos de prueba
        val direccion = Direccion(
            id = UUID.randomUUID(),
            descripcion = "Calle Ficticia 123, Piso 5",
            distrito = Distrito(0, Provincia(0, "Por registrar"), nombre = "Por registrar"),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        val sede = Sede(
            entidad = Entidad(
                nombre = "Sede Principal",
                estado = "Activa",
                direccion = direccion,
            ),
            region = "Centro",
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )

        // Crear un objeto Doctor con datos de prueba
        val doctor = Doctor(
            persona = Persona(
                estado = "Activo",
                direccion = direccion,
            ),
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
        assertEquals(doctor.persona.estado, dto.persona.estado)
        assertEquals(doctor.idoneidad, dto.idoneidad)
        assertEquals(doctor.categoria, dto.categoria)
        assertEquals(doctor.createdAt, dto.createdAt)
        assertEquals(doctor.updatedAt, dto.updatedAt)

        // Verificar conversión de direccion a DireccionDto
        assertNotNull(dto.persona.direccion)
        assertEquals(doctor.persona.direccion.id, dto.persona.direccion.id)
        assertEquals(doctor.persona.direccion.descripcion, dto.persona.direccion.descripcion)
        assertEquals(doctor.persona.direccion.createdAt, dto.persona.direccion.createdAt)
        assertEquals(doctor.persona.direccion.updatedAt, dto.persona.direccion.updatedAt)

        assertEquals(doctor.persona.direccion.distrito.nombre, dto.persona.direccion.distrito.nombre)
        assertEquals(doctor.persona.direccion.distrito.id, dto.persona.direccion.distrito.id)
        assertEquals(doctor.persona.direccion.distrito.provincia.id, dto.persona.direccion.distrito.provincia.id)
        assertEquals(
            doctor.persona.direccion.distrito.provincia.nombre,
            dto.persona.direccion.distrito.provincia.nombre,
        )
    }
}
