package io.github.kingg22.api.vacunas.panama.modules.direccion.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import io.github.kingg22.api.vacunas.panama.modules.direccion.dto.DistritoDto
import io.mcarle.konvert.api.KonvertTo
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized

@Entity
@Table(name = "distritos")
@KonvertTo(DistritoDto::class)
class Distrito @JvmOverloads constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Short? = null,

    @Nationalized
    @Column(name = "nombre", nullable = false, length = 100)
    @Size(max = 100)
    var nombre: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "provincia", nullable = false)
    var provincia: Provincia,

    @OneToMany(mappedBy = "distrito")
    @JsonBackReference
    val direcciones: Set<Direccion> = emptySet(),
)
