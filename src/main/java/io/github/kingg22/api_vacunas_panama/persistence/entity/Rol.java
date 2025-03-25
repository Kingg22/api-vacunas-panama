package io.github.kingg22.api_vacunas_panama.persistence.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.github.kingg22.api_vacunas_panama.util.RolesEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "roles", indexes = {
        @Index(name = "uq_roles_rol", columnList = "nombre", unique = true)
})
@NoArgsConstructor
@Getter
@Setter
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Short id;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 100)
    @Nationalized
    @Column(name = "descripcion", length = 100)
    private String descripcion;

    @ManyToMany
    @JoinTable(name = "roles_permisos",
            joinColumns = @JoinColumn(name = "rol"),
            inverseJoinColumns = @JoinColumn(name = "permiso"))
    @JsonManagedReference
    private Set<Permiso> permisos = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    private Set<Usuario> usuarios = new LinkedHashSet<>();

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "idRol")
    private Set<RolesPermisos> rolesPermisos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idRol")
    private Set<UsuariosRoles> usuariosRoles = new LinkedHashSet<>();

    public void setNombre(RolesEnum rol) {
        this.nombre = rol.name();
    }

}
