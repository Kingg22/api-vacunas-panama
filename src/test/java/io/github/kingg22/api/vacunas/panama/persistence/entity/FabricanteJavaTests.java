package io.github.kingg22.api.vacunas.panama.persistence.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class FabricanteJavaTests {
    @Test
    void mapToDtoSuccessful() {
        // Arrange: Crear un objeto Fabricante con datos de prueba
        var direccion = new Direccion(
                UUID.randomUUID(),
                "Avenida Principal",
                new Distrito((short) 1, "Ciudad de Panamá", new Provincia((short) 1, "Panamá")),
                Collections.emptySet(),
                Collections.emptySet(),
                LocalDateTime.now(),
                LocalDateTime.now());

        var fabricante = new Fabricante(
                "BioTech Inc.",
                "Activo",
                direccion,
                "BIO-1234/DNFD",
                "Juan Pérez",
                "juan.perez@biotech.com",
                "+50760001122",
                null,
                Collections.emptySet(),
                LocalDateTime.now(),
                LocalDateTime.now());

        // Act: Convertir a DTO
        var dto = FabricanteKonverterKt.toFabricanteDto(fabricante);

        // Assert: Verificar que los valores se mapean correctamente
        assertNotNull(dto);
        assertEquals(fabricante.getId(), dto.entidad().id());
        assertEquals(fabricante.getNombre(), dto.entidad().nombre());
        assertEquals(fabricante.getCorreo(), dto.entidad().correo());
        assertEquals(fabricante.getTelefono(), dto.entidad().telefono());
        assertEquals(fabricante.getDependencia(), dto.entidad().dependencia());
        assertEquals(fabricante.getEstado(), dto.entidad().estado());
        assertEquals(fabricante.getDisabled(), dto.entidad().disabled());
        assertEquals(fabricante.getLicencia(), dto.licencia());
        assertEquals(fabricante.getContactoNombre(), dto.contactoNombre());
        assertEquals(fabricante.getContactoCorreo(), dto.contactoCorreo());
        assertEquals(fabricante.getContactoTelefono(), dto.contactoTelefono());
        assertEquals(fabricante.getCreatedAt(), dto.createdAt());
        assertEquals(fabricante.getUpdatedAt(), dto.updatedAt());

        // Verificar conversión de direccion a DireccionDto
        assertNotNull(dto.entidad().direccion());
        assertEquals(
                fabricante.getDireccion().getId(), dto.entidad().direccion().id());
        assertEquals(
                fabricante.getDireccion().getCreatedAt(),
                dto.entidad().direccion().createdAt());
        assertEquals(
                fabricante.getDireccion().getUpdatedAt(),
                dto.entidad().direccion().updatedAt());
        assertNotNull(dto.entidad().direccion().distrito());
        assertEquals(
                fabricante.getDireccion().getDistrito().getId(),
                dto.entidad().direccion().distrito().id());
        assertEquals(
                fabricante.getDireccion().getDistrito().getNombre(),
                dto.entidad().direccion().distrito().nombre());
        assertNotNull(dto.entidad().direccion().distrito().provincia());
        assertEquals(
                fabricante.getDireccion().getDistrito().getProvincia().getId(),
                dto.entidad().direccion().distrito().provincia().id());
        assertEquals(
                fabricante.getDireccion().getDistrito().getProvincia().getNombre(),
                dto.entidad().direccion().distrito().provincia().nombre());
    }
}
