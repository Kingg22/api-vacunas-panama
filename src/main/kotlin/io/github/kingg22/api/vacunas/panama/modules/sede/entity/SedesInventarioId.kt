package io.github.kingg22.api.vacunas.panama.modules.sede.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.Hibernate
import java.io.Serial
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Embeddable
class SedesInventarioId(
    @Column(name = "sede", nullable = false)
    var sede: UUID,

    @Column(name = "vacuna", nullable = false)
    var vacuna: UUID,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        val entity = other as SedesInventarioId
        return this.vacuna == entity.vacuna && this.sede == entity.sede
    }

    override fun hashCode(): Int = Objects.hash(vacuna, sede)

    companion object {
        @Serial
        @JvmStatic
        val serialVersionUID = -1833808364336740202L
    }
}
