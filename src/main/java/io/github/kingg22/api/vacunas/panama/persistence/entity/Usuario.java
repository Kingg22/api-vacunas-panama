package io.github.kingg22.api.vacunas.panama.persistence.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(
        name = "usuarios",
        indexes = {@Index(name = "ix_usuarios_username", columnList = "usuario", unique = true)})
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
    @JoinTable(
            name = "usuarios_roles",
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
