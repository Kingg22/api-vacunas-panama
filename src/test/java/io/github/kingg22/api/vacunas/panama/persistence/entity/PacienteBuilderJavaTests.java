package io.github.kingg22.api.vacunas.panama.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class PacienteBuilderJavaTests {

    @Test
    void build() {
        Direccion direccion = new Direccion("prueba");
        LocalDateTime fecha = LocalDateTime.MIN;
        var persona = Persona.builder().estado("prueba").direccion(direccion).build();

        Paciente paciente = new Paciente(persona);
        paciente.setCreatedAt(fecha);

        var pacienteBuilder = Paciente.builderPaciente()
                .createdAt(fecha)
                .estado(persona.getEstado())
                .direccion(persona.getDireccion())
                .build();
        assertThat(paciente).usingRecursiveComparison().withStrictTypeChecking().isEqualTo(pacienteBuilder);
    }
}
