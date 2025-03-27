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
        assertEquals(fabricante.getId(), dto.getId());
        assertEquals(fabricante.getNombre(), dto.getNombre());
        assertEquals(fabricante.getCorreo(), dto.getCorreo());
        assertEquals(fabricante.getTelefono(), dto.getTelefono());
        assertEquals(fabricante.getDependencia(), dto.getDependencia());
        assertEquals(fabricante.getEstado(), dto.getEstado());
        assertEquals(fabricante.getDisabled(), dto.getDisabled());
        assertEquals(fabricante.getLicencia(), dto.getLicencia());
        assertEquals(fabricante.getContactoNombre(), dto.getContactoNombre());
        assertEquals(fabricante.getContactoCorreo(), dto.getContactoCorreo());
        assertEquals(fabricante.getContactoTelefono(), dto.getContactoTelefono());
        assertEquals(fabricante.getCreatedAt(), dto.getCreatedAt());
        assertEquals(fabricante.getUpdatedAt(), dto.getUpdatedAt());

        // Verificar conversión de direccion a DireccionDto
        assertNotNull(dto.getDireccion());
        assertEquals(fabricante.getDireccion().getId(), dto.getDireccion().id());
        assertEquals(
                fabricante.getDireccion().getCreatedAt(), dto.getDireccion().createdAt());
        assertEquals(
                fabricante.getDireccion().getUpdatedAt(), dto.getDireccion().updatedAt());
        assertNotNull(dto.getDireccion().distrito());
        assertEquals(
                fabricante.getDireccion().getDistrito().getId(),
                dto.getDireccion().distrito().id());
        assertEquals(
                fabricante.getDireccion().getDistrito().getNombre(),
                dto.getDireccion().distrito().nombre());
        assertNotNull(dto.getDireccion().distrito().provincia());
        assertEquals(
                fabricante.getDireccion().getDistrito().getProvincia().getId(),
                dto.getDireccion().distrito().provincia().id());
        assertEquals(
                fabricante.getDireccion().getDistrito().getProvincia().getNombre(),
                dto.getDireccion().distrito().provincia().nombre());
    }
}
