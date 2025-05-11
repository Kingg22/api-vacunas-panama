package io.github.kingg22.api.vacunas.panama.modules.doctor.entity

import io.github.kingg22.api.vacunas.panama.modules.doctor.domain.DoctorModel
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.Mapping
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheCompanionBase
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheEntityBase
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
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
    name = "doctores",
    indexes = [
        Index(name = "ix_doctores_idoneidad", columnList = "idoneidad"),
    ],
)
@KonvertFrom(DoctorModel::class, [Mapping(target = "idoneidad", expression = "it.idoneidad ?: \"\"")])
class Doctor(
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @MapsId
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @ColumnDefault("gen_random_uuid()")
    @JoinColumn(name = "id", nullable = false)
    var persona: Persona,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sede")
    var sede: Sede? = null,

    @all:Size(max = 20)
    @all:NotNull
    @Column(name = "idoneidad", nullable = false, length = 20)
    var idoneidad: String,

    @all:Size(max = 100)
    @Column(name = "categoria", length = 100)
    var categoria: String? = null,

    @all:NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<Doctor, UUID>
}
