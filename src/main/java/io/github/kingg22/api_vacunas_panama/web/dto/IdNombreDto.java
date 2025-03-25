package io.github.kingg22.api_vacunas_panama.web.dto;

import java.io.Serializable;

public record IdNombreDto(Long id, String nombre) implements Serializable {
    public IdNombreDto(Short id, String nombre) {
        this(id.longValue(), nombre);
    }

    public IdNombreDto(Integer id, String nombre) {
        this(id.longValue(), nombre);
    }
}
