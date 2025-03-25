package io.github.kingg22.api_vacunas_panama.persistence.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "fabricantes", indexes = {
        @Index(name = "ix_fabricantes_licencia", columnList = "licencia")
})
public class Fabricante extends Entidad {
    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "licencia", nullable = false, length = 50)
    private String licencia;

    @Size(max = 100)
    @Nationalized
    @Column(name = "contacto_nombre", length = 100)
    private String contactoNombre;

    @Size(max = 254)
    @Column(name = "contacto_correo", length = 254)
    private String contactoCorreo;

    @Size(max = 15)
    @Column(name = "contacto_telefono", length = 15)
    private String contactoTelefono;

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario")
    private Usuario usuario;

    @ManyToMany
    @JoinTable(name = "fabricantes_vacunas",
            joinColumns = @JoinColumn(name = "fabricante"),
            inverseJoinColumns = @JoinColumn(name = "vacuna"))
    @JsonBackReference
    private Set<Vacuna> vacunas = new LinkedHashSet<>();

}
