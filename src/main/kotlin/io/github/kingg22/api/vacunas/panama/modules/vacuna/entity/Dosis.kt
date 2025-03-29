package io.github.kingg22.api.vacunas.panama.modules.vacuna.entity

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.DosisDto
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.NumDosisEnum
import io.mcarle.konvert.api.KonvertTo
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Entity
@Table(name = "dosis")
@KonvertTo(DosisDto::class)
class Dosis @JvmOverloads constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente", nullable = false)
    var paciente: Paciente,

    @Column(name = "fecha_aplicacion", nullable = false)
    var fechaAplicacion: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @Column(name = "numero_dosis", nullable = false, length = 2, columnDefinition = "CHAR(2)")
    var numeroDosis: NumDosisEnum,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vacuna", nullable = false)
    var vacuna: Vacuna,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sede", nullable = false)
    var sede: Sede,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor")
    var doctor: Doctor? = null,

    @Nationalized
    @Column(name = "lote", length = 50)
    @Size(max = 50)
    var lote: String? = null,

    @CreatedDate
    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,
) {
    companion object {
        @JvmStatic
        fun builder() = Builder()

        @JvmStatic
        fun builder(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    class Builder {
        var id: UUID? = null
        lateinit var paciente: Paciente
        lateinit var fechaAplicacion: LocalDateTime
        lateinit var numeroDosis: NumDosisEnum
        lateinit var vacuna: Vacuna
        lateinit var sede: Sede
        var doctor: Doctor? = null
        var lote: String? = null
        var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)
        var updatedAt: LocalDateTime? = null

        fun id(id: UUID?) = apply { this.id = id }
        fun paciente(paciente: Paciente) = apply { this.paciente = paciente }
        fun fechaAplicacion(fechaAplicacion: LocalDateTime) = apply { this.fechaAplicacion = fechaAplicacion }
        fun numeroDosis(numeroDosis: NumDosisEnum) = apply { this.numeroDosis = numeroDosis }
        fun vacuna(vacuna: Vacuna) = apply { this.vacuna = vacuna }
        fun sede(sede: Sede) = apply { this.sede = sede }
        fun doctor(doctor: Doctor?) = apply { this.doctor = doctor }
        fun lote(lote: String?) = apply { this.lote = lote }
        fun createdAt(createdAt: LocalDateTime) = apply { this.createdAt = createdAt }
        fun updatedAt(updatedAt: LocalDateTime?) = apply { this.updatedAt = updatedAt }

        fun build() =
            Dosis(id, paciente, fechaAplicacion, numeroDosis, vacuna, sede, doctor, lote, createdAt, updatedAt)
    }
}
