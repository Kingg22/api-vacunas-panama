package io.github.kingg22.api.vacunas.panama.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "provincias")
@NoArgsConstructor
@Getter
@Setter
public class Provincia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "tinyint not null")
    private Short id;

    @Size(max = 30)
    @NotNull
    @Nationalized
    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @OneToMany(mappedBy = "provincia")
    private Set<Distrito> distritos = new LinkedHashSet<>();
}
