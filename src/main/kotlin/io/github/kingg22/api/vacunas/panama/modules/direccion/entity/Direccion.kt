package io.github.kingg22.api.vacunas.panama.modules.direccion.entity

import io.github.kingg22.api.vacunas.panama.modules.direccion.domain.DireccionModel
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DireccionDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.ProvinciaDto
import io.mcarle.konvert.api.KonvertFrom
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

@Entity
@Table(
    name = "direcciones",
    indexes = [
        Index(name = "ix_direcciones_descripcion", columnList = "descripcion"),
    ],
)
@KonvertFrom(DireccionModel::class)
class Direccion(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @all:Size(max = 150)
    @all:NotNull
    @Column(name = "descripcion", nullable = false, length = 150)
    var descripcion: String,

    @all:NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @all:NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @ColumnDefault("0")
    @JoinColumn(name = "distrito", nullable = false)
    var distrito: Distrito,
) {
    override fun toString(): String = Direccion::class.simpleName +
        "(id=$id, descripcion='$descripcion', createdAt=$createdAt, updatedAt=$updatedAt, distrito=$distrito)"

    companion object {
        val DIRECCION_DEFAULT = Direccion(
            descripcion = DireccionDto.DEFAULT_DIRECCION,
            distrito = Distrito(
                nombre = DistritoDto.DEFAULT_DISTRITO,
                provincia = Provincia(nombre = ProvinciaDto.DEFAULT_PROVINCIA),
            ),
        )
    }
}
