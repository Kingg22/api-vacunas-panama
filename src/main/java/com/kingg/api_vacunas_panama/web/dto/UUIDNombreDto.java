package com.kingg.api_vacunas_panama.web.dto;

import java.io.Serializable;
import java.util.UUID;

public record UUIDNombreDto(UUID id, String nombre) implements Serializable {
}
