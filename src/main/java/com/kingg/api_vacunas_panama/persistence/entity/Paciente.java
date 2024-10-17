package com.kingg.api_vacunas_panama.persistence.entity;

import com.kingg.api_vacunas_panama.web.dto.ViewPacienteVacunaEnfermedadDto;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "pacientes", indexes = {
        @Index(name = "ix_pacientes_id_temporal", columnList = "identificacion_temporal", unique = true)
})
@NamedNativeQuery(name = "Paciente.getVacunaEnfermedades",
        query = "SELECT " +
                "[Nombre Vacuna] AS nombre_vacuna," +
                "[Número de dosis] AS numero_dosis, " +
                "[Enfermedades Prevenidas] AS enfermedades_prevenidas, " +
                "[Edad Mínima Recomendada en Meses] AS edad_minima," +
                "[Fecha de Aplicación] AS fecha_aplicacion," +
                "[Intervalo Recomendado entre Dosis 1 y 2 en Meses] AS intervalo_recomendado," +
                "[Intervalo Real en Días] AS intervalo_real," +
                "Sede AS nombre_sede," +
                "Dependencia AS dependencia_sede," +
                "id," +
                "id_vacuna," +
                "ids_enfermedades " +
                "FROM view_pacientes_vacunas_enfermedades " +
                "WHERE id = :id " +
                "ORDER BY fecha_aplicacion DESC",
        resultSetMapping = "view_paciente_vacuna_enfermedad"
)
@NamedNativeQuery(name = "Paciente.getVacunaPaciente",
        query = "SELECT " +
                "[Nombre Vacuna] AS nombre_vacuna," +
                "[Número de dosis] AS numero_dosis, " +
                "[Enfermedades Prevenidas] AS enfermedades_prevenidas, " +
                "[Edad Mínima Recomendada en Meses] AS edad_minima," +
                "[Fecha de Aplicación] AS fecha_aplicacion," +
                "[Intervalo Recomendado entre Dosis 1 y 2 en Meses] AS intervalo_recomendado," +
                "[Intervalo Real en Días] AS intervalo_real," +
                "Sede AS nombre_sede," +
                "Dependencia AS dependencia_sede," +
                "id," +
                "id_vacuna," +
                "ids_enfermedades " +
                "FROM view_pacientes_vacunas_enfermedades " +
                "WHERE id = :id AND id_vacuna = :vacuna " +
                "ORDER BY fecha_aplicacion DESC",
        resultSetMapping = "view_paciente_vacuna_enfermedad"
)
@SqlResultSetMapping(name = "view_paciente_vacuna_enfermedad", classes = @ConstructorResult(
        targetClass = ViewPacienteVacunaEnfermedadDto.class,
        columns = {
                @ColumnResult(name = "nombre_vacuna", type = String.class),
                @ColumnResult(name = "numero_dosis", type = String.class),
                @ColumnResult(name = "enfermedades_prevenidas", type = String.class),
                @ColumnResult(name = "edad_minima", type = Short.class),
                @ColumnResult(name = "fecha_aplicacion", type = LocalDateTime.class),
                @ColumnResult(name = "intervalo_recomendado", type = Double.class),
                @ColumnResult(name = "intervalo_real", type = Integer.class),
                @ColumnResult(name = "nombre_sede", type = String.class),
                @ColumnResult(name = "dependencia_sede", type = String.class),
                @ColumnResult(name = "id", type = UUID.class),
                @ColumnResult(name = "id_vacuna", type = UUID.class),
                @ColumnResult(name = "ids_enfermedades", type = String.class)
        }
))
public class Paciente extends Persona {
    @Size(max = 255)
    @Column(name = "identificacion_temporal")
    private String identificacionTemporal;

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "paciente")
    private Set<Dosis> dosis = new LinkedHashSet<>();

}