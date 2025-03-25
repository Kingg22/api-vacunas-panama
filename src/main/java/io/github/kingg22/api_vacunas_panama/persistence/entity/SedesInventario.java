package io.github.kingg22.api_vacunas_panama.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sedes_inventarios")
public class SedesInventario {
    @EmbeddedId
    private SedesInventarioId id;

    @MapsId("sede")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sede", nullable = false)
    private Sede sede;

    @MapsId("vacuna")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vacuna", nullable = false)
    private Vacuna vacuna;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @NotNull
    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "lote", nullable = false, length = 50)
    private String lote;

    @NotNull
    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDateTime fechaRecepcion;

}
