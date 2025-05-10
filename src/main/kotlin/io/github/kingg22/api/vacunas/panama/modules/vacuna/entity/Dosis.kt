package io.github.kingg22.api.vacunas.panama.modules.vacuna.entity

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.sede.entity.Sede
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
class Dosis(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @all:Size(max = 2)
    @all:NotNull
    @Column(name = "numero_dosis", nullable = false, length = 2)
    var numeroDosis: String,

    @all:NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @all:NotNull
    @ColumnDefault("now()")
    @Column(name = "fecha_aplicacion", nullable = false)
    var fechaAplicacion: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor")
    var doctor: Doctor? = null,

    @all:NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "paciente", nullable = false)
    var paciente: Paciente,

    @all:NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sede", nullable = false)
    var sede: Sede,

    @all:NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "vacuna", nullable = false)
    var vacuna: Vacuna,

    @all:Size(max = 50)
    @Column(name = "lote", length = 50)
    var lote: String? = null,
)
