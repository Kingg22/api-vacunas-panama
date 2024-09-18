package com.kingg.api_vacunas_panama.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "provincias")
@NoArgsConstructor
@Getter
@Setter
public class Provincia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_provincia", columnDefinition = "tinyint not null")
    private Short id;

    @Size(max = 30)
    @NotNull
    @Nationalized
    @Column(name = "nombre_provincia", nullable = false, length = 30)
    private String nombreProvincia;

    @OneToMany(mappedBy = "idProvincia")
    private Set<Distrito> distritos = new LinkedHashSet<>();

}