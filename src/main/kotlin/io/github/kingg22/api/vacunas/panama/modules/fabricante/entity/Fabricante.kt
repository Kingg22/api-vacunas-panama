package io.github.kingg22.api.vacunas.panama.modules.fabricante.entity

import io.github.kingg22.api.vacunas.panama.modules.common.entity.Entidad
import io.github.kingg22.api.vacunas.panama.modules.fabricante.dto.FabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.mcarle.konvert.api.KonvertTo
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
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.util.UUID

@Entity
@Table(
    name = "fabricantes",
    indexes = [
        Index(name = "ix_fabricantes_usuario", columnList = "usuario", unique = true),
        Index(name = "ix_fabricantes_licencia", columnList = "licencia"),
    ],
)
@KonvertTo(FabricanteDto::class)
class Fabricante(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @MapsId
    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @ColumnDefault("gen_random_uuid()")
    @JoinColumn(name = "id", nullable = false)
    var entidad: Entidad,

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(UTC),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null,

    @Size(max = 15)
    @Column(name = "contacto_telefono", length = 15)
    var contactoTelefono: String? = null,

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario")
    var usuario: Usuario? = null,

    @Size(max = 50)
    @NotNull
    @Column(name = "licencia", nullable = false, length = 50)
    var licencia: String,

    @Size(max = 100)
    @Column(name = "contacto_nombre", length = 100)
    var contactoNombre: String? = null,

    @Size(max = 254)
    @Column(name = "contacto_correo", length = 254)
    var contactoCorreo: String? = null,
) : PanacheEntityBase {
    companion object : PanacheCompanionBase<Fabricante, UUID>
}
