package io.github.kingg22.api.vacunas.panama.modules.persona.entity

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion
import io.github.kingg22.api.vacunas.panama.modules.persona.domain.PersonaModel
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.mcarle.konvert.api.KonvertFrom
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
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "personas",
    indexes = [
        Index(name = "ix_personas_cedula", columnList = "cedula", unique = true),
        Index(name = "ix_personas_telefono", columnList = "telefono", unique = true),
        Index(name = "ix_personas_usuario", columnList = "usuario", unique = true),
        Index(name = "ix_personas_pasaporte", columnList = "pasaporte", unique = true),
        Index(name = "ix_personas_correo", columnList = "correo", unique = true),
        Index(name = "ix_personas_nombres_apellidos", columnList = "nombre, nombre2, apellido1, apellido2"),
    ],
)
@KonvertFrom(
    PersonaModel::class,
    [
        Mapping(
            "direccion",
            expression = "io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Direccion.DIRECCION_DEFAULT",
        ),
    ],
)
class Persona(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "edad")
    var edad: Short? = null,

    @all:Size(max = 1)
    @Column(name = "sexo", length = 1)
    var sexo: String? = null,

    @Column(name = "fecha_nacimiento")
    var fechaNacimiento: LocalDateTime? = null,

    @all:Size(max = 15)
    @Column(name = "cedula", length = 15)
    var cedula: String? = null,

    @all:Size(max = 15)
    @Column(name = "telefono", length = 15)
    var telefono: String? = null,

    @all:NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "direccion", nullable = false)
    var direccion: Direccion = Direccion.DIRECCION_DEFAULT,

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario")
    var usuario: Usuario? = null,

    @all:Size(max = 20)
    @Column(name = "pasaporte", length = 20)
    var pasaporte: String? = null,

    @all:Size(max = 50)
    @all:NotNull
    @ColumnDefault("'NO_VALIDADO'")
    @Column(name = "estado", nullable = false, length = 50)
    var estado: String = "NO_VALIDADO",

    @all:Size(max = 100)
    @Column(name = "apellido1", length = 100)
    var apellido1: String? = null,

    @all:Size(max = 100)
    @Column(name = "apellido2", length = 100)
    var apellido2: String? = null,

    @all:Size(max = 100)
    @Column(name = "nombre", length = 100)
    var nombre: String? = null,

    @all:Size(max = 100)
    @Column(name = "nombre2", length = 100)
    var nombre2: String? = null,

    @all:Size(max = 254)
    @Column(name = "correo", length = 254)
    var correo: String? = null,
) : PanacheEntityBase {
    override fun toString(): String = Persona::class.simpleName +
        " id: $id, nombre: $nombre, apellido1: $apellido1, apellido2: $apellido2, cedula: $cedula, direccion: $direccion, estado: $estado, fechaNacimiento: $fechaNacimiento, pasaporte: $pasaporte, telefono: $telefono, usuario: $usuario, correo: $correo, sexo: $sexo, edad: $edad"

    companion object : PanacheCompanionBase<Persona, UUID>
}
