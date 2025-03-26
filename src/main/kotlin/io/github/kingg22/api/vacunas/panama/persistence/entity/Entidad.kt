package io.github.kingg22.api.vacunas.panama.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import java.util.UUID

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(
    name = "entidades",
    indexes = [
        Index(name = "ix_entidades_correo", columnList = "correo", unique = true),
        Index(name = "ix_entidades_telefono", columnList = "telefono", unique = true),
    ],
)
abstract class Entidad @JvmOverloads constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Nationalized
    @Column(name = "nombre", nullable = false, length = 100)
    @Size(max = 100)
    var nombre: String,

    @Column(name = "correo", length = 254)
    @Size(max = 254)
    var correo: String? = null,

    @Column(name = "telefono", length = 15)
    @Size(max = 15)
    var telefono: String? = null,

    @Nationalized
    @Column(name = "dependencia", length = 13)
    @Size(max = 13)
    var dependencia: String? = null,

    @Nationalized
    @Column(name = "estado", nullable = false, length = 50)
    @Size(max = 50)
    var estado: String,

    @Column(name = "disabled", nullable = false)
    var disabled: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "direccion", nullable = false)
    var direccion: Direccion,
)
