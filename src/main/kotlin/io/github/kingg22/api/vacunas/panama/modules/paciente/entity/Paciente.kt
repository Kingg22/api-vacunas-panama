package io.github.kingg22.api.vacunas.panama.modules.paciente.entity

import io.github.kingg22.api.vacunas.panama.modules.paciente.domain.PacienteModel
import io.github.kingg22.api.vacunas.panama.modules.paciente.dto.PacienteDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.mcarle.konvert.api.KonvertFrom
import io.mcarle.konvert.api.KonvertTo
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
    name = "pacientes",
    indexes = [
        Index(name = "uq_pacientes_id_temporal", columnList = "identificacion_temporal", unique = true),
    ],
)
@KonvertTo(PacienteDto::class)
class Paciente(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @MapsId
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @ColumnDefault("gen_random_uuid()")
    @JoinColumn(name = "id", nullable = false)
    var persona: Persona,

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Size(max = 255)
    @Column(name = "identificacion_temporal")
    var identificacionTemporal: String? = null,
) : PanacheEntityBase {
    override fun toString(): String = Paciente::class.simpleName +
        ": id=$id, persona=$persona, createdAt=$createdAt, updatedAt=$updatedAt, identificacionTemporal=$identificacionTemporal"

    @KonvertFrom(PacienteModel::class)
    companion object : PanacheCompanionBase<Paciente, UUID>
}
