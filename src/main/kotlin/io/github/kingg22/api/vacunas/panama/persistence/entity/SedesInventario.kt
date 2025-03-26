package io.github.kingg22.api.vacunas.panama.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import java.time.LocalDateTime

@Entity
@Table(name = "sedes_inventarios")
class SedesInventario(
    @EmbeddedId
    val id: SedesInventarioId? = null,

    @MapsId("sede")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sede", nullable = false)
    val sede: Sede,

    @MapsId("vacuna")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vacuna", nullable = false)
    val vacuna: Vacuna,

    @Column(name = "cantidad", nullable = false)
    var cantidad: Int,

    @Column(name = "fecha_expiracion", nullable = false)
    var fechaExpiracion: LocalDateTime,

    @Nationalized
    @Column(name = "lote", nullable = false, length = 50)
    @Size(max = 50)
    var lote: String,

    @Column(name = "fecha_recepcion", nullable = false)
    var fechaRecepcion: LocalDateTime,
)
