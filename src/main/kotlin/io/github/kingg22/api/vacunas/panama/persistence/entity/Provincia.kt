package io.github.kingg22.api.vacunas.panama.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized

@Entity
@Table(name = "provincias")
class Provincia @JvmOverloads constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "tinyint not null")
    var id: Short? = null,

    @Nationalized
    @Column(name = "nombre", nullable = false, length = 30)
    @Size(max = 30)
    var nombre: String,

    @OneToMany(mappedBy = "provincia")
    val distritos: Set<Distrito> = emptySet(),
)
