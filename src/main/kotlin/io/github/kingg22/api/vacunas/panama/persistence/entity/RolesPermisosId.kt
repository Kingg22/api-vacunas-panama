package io.github.kingg22.api.vacunas.panama.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.Hibernate
import java.io.Serial
import java.io.Serializable
import java.util.Objects

@Embeddable
class RolesPermisosId(
    @Column(name = "rol", nullable = false)
    var idRol: Short,

    @Column(name = "permiso", nullable = false)
    var idPermiso: Short,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        val entity = other as RolesPermisosId
        return this.idRol == entity.idRol && this.idPermiso == entity.idPermiso
    }

    override fun hashCode(): Int = Objects.hash(idRol, idPermiso)

    companion object {
        @Serial
        @JvmStatic
        private val serialVersionUID = -5300599546506914322L
    }
}
