package io.github.kingg22.api.vacunas.panama.modules.common.entity

import io.github.kingg22.api.vacunas.panama.modules.common.dto.EntidadDto
import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.mcarle.konvert.api.KonvertTo
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.util.UUID

@Entity
@Table(
    name = "entidades",
    indexes = [
        Index(name = "ix_entidades_telefono", columnList = "telefono", unique = true),
        Index(name = "ix_entidades_correo", columnList = "correo", unique = true),
    ],
)
@KonvertTo(EntidadDto::class)
class Entidad(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Size(max = 13)
    @Column(name = "dependencia", length = 13)
    var dependencia: String? = null,

    @Size(max = 15)
    @Column(name = "telefono", length = 15)
    var telefono: String? = null,

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "direccion", nullable = false)
    var direccion: Direccion = Direccion.DIRECCION_DEFAULT,

    @Size(max = 50)
    @NotNull
    @ColumnDefault("'ACTIVO'")
    @Column(name = "estado", nullable = false, length = 50)
    var estado: String = "ACTIVO",

    @Size(max = 100)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 100)
    var nombre: String,

    @Size(max = 254)
    @Column(name = "correo", length = 254)
    var correo: String? = null,
)
