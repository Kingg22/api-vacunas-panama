package io.github.kingg22.api.vacunas.panama.modules.direccion.entity

import io.github.kingg22.api.vacunas.panama.modules.direccion.domain.DistritoModel
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(
    name = "distritos",
    indexes = [
        Index(name = "ix_distritos_nombre", columnList = "nombre"),
    ],
)
@KonvertTo(DistritoDto::class)
class Distrito(
    @Id
    @Column(name = "id", nullable = false)
    var id: Short? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "provincia", nullable = false)
    var provincia: Provincia,

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    var nombre: String,
) {
    override fun toString(): String = Distrito::class.simpleName +
        "(id=$id, provincia=$provincia, nombre='$nombre')"

    @KonvertFrom(DistritoModel::class)
    companion object
}
