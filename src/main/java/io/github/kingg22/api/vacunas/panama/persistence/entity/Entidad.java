package io.github.kingg22.api.vacunas.panama.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Nationalized;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(
        name = "entidades",
        indexes = {
            @Index(name = "ix_entidades_correo", columnList = "correo", unique = true),
            @Index(name = "ix_entidades_telefono", columnList = "telefono", unique = true)
        })
public abstract class Entidad {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 254)
    @Column(name = "correo", length = 254)
    private String correo;

    @Size(max = 15)
    @Column(name = "telefono", length = 15)
    private String telefono;

    @Size(max = 13)
    @Nationalized
    @Column(name = "dependencia", length = 13)
    private String dependencia;

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
}
