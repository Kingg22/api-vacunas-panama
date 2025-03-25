package io.github.kingg22.api_vacunas_panama.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sintomas", indexes = {
        @Index(name = "ix_sintomas_nombre", columnList = "nombre"),
        @Index(name = "uq_sintomas_nombre", columnList = "nombre", unique = true)
})
public class Sintoma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Nationalized
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

}
