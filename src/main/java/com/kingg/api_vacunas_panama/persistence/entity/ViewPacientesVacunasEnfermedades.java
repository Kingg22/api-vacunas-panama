package com.kingg.api_vacunas_panama.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "view_pacientes_vacunas_enfermedades")
public class ViewPacientesVacunasEnfermedades {
    @Id
    @Size(max = 20)
    @NotNull
    @Nationalized
    @Column(name = "\"Cédula\"", nullable = false, length = 20)
    private String cedula;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "Nombre", nullable = false, length = 50)
    private String nombre;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "\"Apellido 1\"", nullable = false, length = 50)
    private String apellido1;

    @Size(max = 50)
    @Nationalized
    @Column(name = "\"Apellido 2\"", length = 50)
    private String apellido2;

    @NotNull
    @Column(name = "\"Fecha de Nacimiento\"", nullable = false)
    private LocalDateTime fechaDeNacimiento;

    @Column(name = "Edad")
    private Integer edad;

    @NotNull
    @Column(name = "Sexo", nullable = false)
    private Character sexo;

    @Size(max = 15)
    @Nationalized
    @Column(name = "\"Teléfono\"", length = 15)
    private String telefono;

    @Size(max = 50)
    @Nationalized
    @Column(name = "\"Correo electrónico\"", length = 50)
    private String correoElectronico;

    @Size(max = 150)
    @Column(name = "\"Dirección residencia actual\"", length = 150)
    private String direccionResidencial;

    @Size(max = 100)
    @Nationalized
    @Column(name = "Distrito", length = 100)
    private String distrito;

    @Size(max = 30)
    @Nationalized
    @Column(name = "Provincia", length = 30)
    private String provincia;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "\"Nombre vacuna\"", nullable = false, length = 100)
    private String nombreVacuna;

    @Size(max = 2)
    @NotNull
    @Column(name = "\"Número de dosis\"", nullable = false, length = 2, columnDefinition = "CHAR(2)")
    private String numDeDosis;

    @Size(max = 100)
    @Nationalized
    @Column(name = "\"Enfermedad previene\"", length = 100)
    private String enfermedadPreviene;

    @Column(name = "\"Edad mínima recomendada en meses\"")
    private Short edadMinRecomendadaEnMeses;

    @NotNull
    @Column(name = "\"Fecha de aplicación\"", nullable = false)
    private LocalDateTime fechaDeAplicacion;

    @Column(name = "\"Intervalo recomendado entre dosis 1 y 2 en meses\"")
    private Double intervaloRecomendadoEntreDosisEnMeses;

    @Size(max = 100)
    @Nationalized
    @Column(name = "Sede", length = 100)
    private String sede;

    @Size(max = 13)
    @Nationalized
    @Column(name = "Dependencia", length = 13)
    private String dependencia;

}