package io.github.kingg22.api.vacunas.panama.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized

@Entity
@Table(
    name = "sintomas",
    indexes = [
        Index(name = "ix_sintomas_nombre", columnList = "nombre"),
        Index(name = "uq_sintomas_nombre", columnList = "nombre", unique = true),
    ],
)
class Sintoma @JvmOverloads constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Int? = null,

    @Nationalized
    @Column(name = "nombre", nullable = false, length = 50)
    @Size(max = 50)
    var nombre: String,
)
