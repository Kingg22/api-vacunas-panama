package io.github.kingg22.api.vacunas.panama.persistence.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

@Entity
@Table(name = "direcciones")
class Direccion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null

    @Column(name = "direccion", nullable = false, length = 150)
    @Size(max = 150)
    lateinit var direccion: String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distrito")
    var distrito: Distrito? = null

    @OneToMany(mappedBy = "direccion")
    @JsonBackReference
    val pacientes: Set<Paciente> = emptySet()

    @OneToMany(mappedBy = "direccion")
    @JsonBackReference
    val sedes: Set<Sede> = emptySet()

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
}
