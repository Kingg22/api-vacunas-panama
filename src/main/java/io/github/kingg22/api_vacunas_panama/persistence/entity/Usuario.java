package io.github.kingg22.api_vacunas_panama.persistence.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "usuarios", indexes = {
        @Index(name = "ix_usuarios_username", columnList = "usuario", unique = true)
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 50)
    @Nationalized
    @Column(name = "usuario", length = 50)
    private String username;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "clave", nullable = false, length = 100)
    private String password;

    @Transient
    private boolean disabled;

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @ManyToMany
    @JoinTable(name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "usuario"),
            inverseJoinColumns = @JoinColumn(name = "rol"))
    @JsonManagedReference
    private Set<Rol> roles = new LinkedHashSet<>();

    @OneToOne(mappedBy = "usuario")
    private Fabricante fabricante;

    @OneToOne(mappedBy = "usuario")
    private Persona persona;

    @OneToMany(mappedBy = "idUsuario")
    private Set<UsuariosRoles> usuariosRoles = new LinkedHashSet<>();

}
