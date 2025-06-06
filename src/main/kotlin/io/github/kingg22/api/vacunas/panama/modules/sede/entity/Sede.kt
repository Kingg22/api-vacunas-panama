package io.github.kingg22.api.vacunas.panama.modules.sede.entity

import io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad
import io.github.kingg22.api.vacunas.panama.modules.sede.domain.SedeModel
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.Mapping
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

@Entity
@Table(
    name = "sedes",
    indexes = [
        Index(name = "ix_sedes_region", columnList = "region"),
    ],
)
@KonvertFrom(
    SedeModel::class,
    [
        Mapping(
            "entidad",
            expression = "io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad(nombre = it.nombre)",
        ),
    ],
)
class Sede(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @MapsId
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @ColumnDefault("gen_random_uuid()")
    @JoinColumn(name = "id", nullable = false)
    var entidad: Entidad,

    @all:Size(max = 50)
    @Column(name = "region", length = 50)
    var region: String? = null,

    @all:NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<Sede, UUID>
}
