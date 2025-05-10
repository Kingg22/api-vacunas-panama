package io.github.kingg22.api.vacunas.panama.modules.direccion.entity

import io.github.kingg22.api.vacunas.panama.modules.direccion.domain.ProvinciaModel
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(
    name = "provincias",
    indexes = [
        Index(name = "ix_provincias_nombre", columnList = "nombre"),
    ],
)
@KonvertTo(ProvinciaDto::class)
class Provincia(
    @Id
    @Column(name = "id", nullable = false)
    var id: Short? = null,

    @all:Size(max = 30)
    @all:NotNull
    @Column(name = "nombre", nullable = false, length = 30)
    var nombre: String,
) : PanacheEntityBase {
    override fun toString(): String = Provincia::class.simpleName +
        "(id=$id, nombre='$nombre')"

    @KonvertFrom(ProvinciaModel::class)
    companion object : PanacheCompanionBase<Provincia, Short>
}
