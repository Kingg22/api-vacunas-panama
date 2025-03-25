package io.github.kingg22.api_vacunas_panama.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "personas", indexes = {
        @Index(name = "ix_personas_nombres_apellidos", columnList = "nombre, nombre2, apellido1, apellido2"),
        @Index(name = "ix_personas_cedula", columnList = "cedula", unique = true),
        @Index(name = "ix_personas_pasaporte", columnList = "pasaporte", unique = true),
        @Index(name = "ix_personas_correo", columnList = "correo", unique = true),
        @Index(name = "ix_personas_telefono", columnList = "telefono", unique = true)
})
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 15)
    @Column(name = "cedula", length = 15)
    private String cedula;

    @Size(max = 20)
    @Column(name = "pasaporte", length = 20)
    private String pasaporte;

    @Size(max = 100)
    @Nationalized
    @Column(name = "nombre", length = 100)
    private String nombre;

    @Size(max = 100)
    @Nationalized
    @Column(name = "nombre2", length = 100)
    private String nombre2;

    @Size(max = 100)
    @Nationalized
    @Column(name = "apellido1", length = 100)
    private String apellido1;

    @Size(max = 100)
    @Nationalized
    @Column(name = "apellido2", length = 100)
    private String apellido2;

    @Size(max = 254)
    @Column(name = "correo", length = 254)
    private String correo;

    @Size(max = 15)
    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDateTime fechaNacimiento;

    @Column(name = "edad", columnDefinition = "tinyint")
    private Short edad;

    @Column(name = "sexo")
    private Character sexo;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "estado", nullable = false, length = 50)
    private String estado;

    @NotNull
    @Column(name = "disabled", nullable = false)
    private Boolean disabled = false;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "direccion", nullable = false)
    private Direccion direccion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario")
    private Usuario usuario;

}
