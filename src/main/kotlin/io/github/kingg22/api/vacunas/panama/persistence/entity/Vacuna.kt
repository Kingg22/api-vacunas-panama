package io.github.kingg22.api.vacunas.panama.persistence.entity

import io.github.kingg22.api.vacunas.panama.util.NumDosisEnum
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Entity
@Table(name = "vacunas", indexes = [Index(name = "ix_vacunas_nombre", columnList = "nombre")])
class Vacuna(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Nationalized
    @Column(name = "nombre", nullable = false, length = 100)
    @Size(max = 100)
    var nombre: String,

    @Column(name = "edad_minima_dias")
    var edadMinimaDias: Int? = null,

    @Column(name = "intervalo_dosis_1_2_dias")
    var intervaloDosisDias: Int? = null,

    @Column(name = "dosis_maxima", columnDefinition = "CHAR(2)")
    var dosisMaxima: NumDosisEnum? = null,

    @Column(name = "created_at")
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "vacuna")
    val dosis: Set<Dosis> = emptySet(),

    @ManyToMany(mappedBy = "vacunas")
    val fabricantes: Set<Fabricante> = emptySet(),

    @OneToMany(mappedBy = "vacuna")
    val sedesInventarios: Set<SedesInventario> = emptySet(),
)
