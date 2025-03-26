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
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized
import java.time.LocalDateTime
import java.util.UUID

// abstract
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(
    name = "personas",
    indexes = [
        Index(name = "ix_personas_nombres_apellidos", columnList = "nombre, nombre2, apellido1, apellido2"),
        Index(name = "ix_personas_cedula", columnList = "cedula", unique = true),
        Index(name = "ix_personas_pasaporte", columnList = "pasaporte", unique = true),
        Index(name = "ix_personas_correo", columnList = "correo", unique = true),
        Index(name = "ix_personas_telefono", columnList = "telefono", unique = true),
    ],
)
class Persona(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,

    @Column(name = "cedula", length = 15)
    @Size(max = 15)
    var cedula: String? = null,

    @Column(name = "pasaporte", length = 20)
    @Size(max = 20)
    var pasaporte: String? = null,

    @Nationalized
    @Column(name = "nombre", length = 100)
    @Size(max = 100)
    var nombre: String? = null,

    @Nationalized
    @Column(name = "nombre2", length = 100)
    @Size(max = 100)
    var nombre2: String? = null,

    @Nationalized
    @Column(name = "apellido1", length = 100)
    @Size(max = 100)
    var apellido1: String? = null,

    @Nationalized
    @Column(name = "apellido2", length = 100)
    @Size(max = 100)
    var apellido2: String? = null,

    @Column(name = "correo", length = 254)
    @Size(max = 254)
    var correo: String? = null,

    @Column(name = "telefono", length = 15)
    @Size(max = 15)
    var telefono: String? = null,

    @Column(name = "fecha_nacimiento")
    var fechaNacimiento: LocalDateTime? = null,

    @Column(name = "edad", columnDefinition = "tinyint")
    var edad: Short? = null,

    @Column(name = "sexo")
    var sexo: Char? = null,

    @Nationalized
    @Column(name = "estado", nullable = false, length = 50)
    @Size(max = 50)
    var estado: String,

    @Column(name = "disabled", nullable = false)
    var disabled: @NotNull Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "direccion", nullable = false)
    var direccion: Direccion,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario")
    var usuario: Usuario? = null,
) {
    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    open class Builder {
        var id: UUID? = null
        var cedula: String? = null
        var pasaporte: String? = null
        var nombre: String? = null
        var nombre2: String? = null
        var apellido1: String? = null
        var apellido2: String? = null
        var correo: String? = null
        var telefono: String? = null
        var fechaNacimiento: LocalDateTime? = null
        var edad: Short? = null
        var sexo: Char? = null
        lateinit var estado: String
        var disabled: Boolean = false
        lateinit var direccion: Direccion
        var usuario: Usuario? = null

        fun id(id: UUID?) = apply { this.id = id }
        fun cedula(cedula: String?) = apply { this.cedula = cedula }
        fun pasaporte(pasaporte: String?) = apply { this.pasaporte = pasaporte }
        fun nombre(nombre: String?) = apply { this.nombre = nombre }
        fun nombre2(nombre2: String?) = apply { this.nombre2 = nombre2 }
        fun apellido1(apellido1: String?) = apply { this.apellido1 = apellido1 }
        fun apellido2(apellido2: String?) = apply { this.apellido2 = apellido2 }
        fun correo(correo: String?) = apply { this.correo = correo }
        fun telefono(telefono: String?) = apply { this.telefono = telefono }
        fun fechaNacimiento(fechaNacimiento: LocalDateTime?) = apply { this.fechaNacimiento = fechaNacimiento }
        fun edad(edad: Short?) = apply { this.edad = edad }
        fun sexo(sexo: Char?) = apply { this.sexo = sexo }
        fun estado(estado: String) = apply { this.estado = estado }
        fun disabled(disabled: Boolean) = apply { this.disabled = disabled }
        fun direccion(direccion: Direccion) = apply { this.direccion = direccion }
        fun usuario(usuario: Usuario?) = apply { this.usuario = usuario }

        open fun build() = Persona(
            id = id,
            cedula = cedula,
            pasaporte = pasaporte,
            nombre = nombre,
            nombre2 = nombre2,
            apellido1 = apellido1,
            apellido2 = apellido2,
            correo = correo,
            telefono = telefono,
            fechaNacimiento = fechaNacimiento,
            edad = edad,
            sexo = sexo,
            estado = estado,
            disabled = disabled,
            direccion = direccion,
            usuario = usuario,
        )
    }
}
