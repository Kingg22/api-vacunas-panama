package io.github.kingg22.api.vacunas.panama.modules.vacuna.entity

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.extensions.getNumeroDosisAsEnum
import io.mcarle.konvert.api.KonvertTo
import io.mcarle.konvert.api.Mapping
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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
@Table(name = "dosis")
@KonvertTo(DosisDto::class, mappings = [Mapping("numeroDosis", constant = "getNumeroDosisAsEnum()")])
class Dosis(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Size(max = 2)
    @NotNull
    @Column(name = "numero_dosis", nullable = false, length = 2)
    var numeroDosis: String,

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "fecha_aplicacion", nullable = false)
    var fechaAplicacion: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor")
    var doctor: Doctor? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "paciente", nullable = false)
    var paciente: Paciente,

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sede", nullable = false)
    var sede: Sede,

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vacuna", nullable = false)
    var vacuna: Vacuna,

    @Size(max = 50)
    @Column(name = "lote", length = 50)
    var lote: String? = null,
) {
    /** Only for [KonvertTo], use [String.getNumeroDosisAsEnum] */
    final fun getNumeroDosisAsEnum() = numeroDosis.getNumeroDosisAsEnum()
}
