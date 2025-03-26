package io.github.kingg22.api.vacunas.panama.persistence.entity

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import org.hibernate.annotations.Nationalized

@Entity
@Table(
    name = "fabricantes",
    indexes = [Index(name = "ix_fabricantes_licencia", columnList = "licencia")],
)
class Fabricante(
    nombre: String,
    estado: String,
    direccion: Direccion,

    @Nationalized
    @Column(name = "licencia", nullable = false, length = 50)
    @Size(max = 50)
    var licencia: String,

    @Nationalized
    @Column(name = "contacto_nombre", length = 100)
    @Size(max = 100)
    var contactoNombre: String? = null,

    @Column(name = "contacto_correo", length = 254)
    @Size(max = 254)
    var contactoCorreo: String? = null,

    @Column(name = "contacto_telefono", length = 15)
    @Size(max = 15)
    var contactoTelefono: String? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario")
    var usuario: Usuario? = null,

    @ManyToMany
    @JoinTable(
        name = "fabricantes_vacunas",
        joinColumns = [JoinColumn(name = "fabricante")],
        inverseJoinColumns = [JoinColumn(name = "vacuna")],
    )
    @JsonBackReference
    val vacunas: Set<Vacuna> = emptySet(),
) : Entidad(nombre = nombre, estado = estado, direccion = direccion)
