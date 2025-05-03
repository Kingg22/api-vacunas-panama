package io.github.kingg22.api.vacunas.panama.modules.vacuna.entity

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import io.github.kingg22.api.vacunas.panama.modules.vacuna.dto.VacunaDto
import io.mcarle.konvert.api.KonvertTo
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
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

@Entity
@Table(
    name = "vacunas",
    indexes = [
        Index(name = "ix_vacunas_nombre", columnList = "nombre"),
    ],
)
@KonvertTo(VacunaDto::class, mappings = [Mapping("dosisMaxima", constant = "null")])
class Vacuna(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @NotNull
    @Column(name = "edad_minima_dias", nullable = false)
    var edadMinimaDias: Short = 0,

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    var nombre: String,

    @Size(max = 2)
    @Column(name = "dosis_maxima", length = 2)
    var dosisMaxima: String? = null,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "fabricantes_vacunas",
        joinColumns = [JoinColumn(name = "vacuna")],
        inverseJoinColumns = [JoinColumn(name = "fabricante")],
    )
    var fabricantes: MutableSet<Fabricante> = mutableSetOf(),
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<Vacuna, UUID>
}
