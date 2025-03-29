package io.github.kingg22.api.vacunas.panama.modules.usuario.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.Hibernate
import java.io.Serial
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Embeddable
class UsuariosRolesId(
    @Column(name = "usuario", nullable = false)
    var idUsuario: UUID,

    @Column(name = "rol", nullable = false)
    private var idRol: Short,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        val entity = other as UsuariosRolesId
        return this.idRol == entity.idRol && this.idUsuario == entity.idUsuario
    }

    override fun hashCode(): Int = Objects.hash(idRol, idUsuario)

    companion object {
        @Serial
        @JvmStatic
        val serialVersionUID = -5265922496317860603L
    }
}
