package io.github.kingg22.api.vacunas.panama.service

import io.github.kingg22.api.vacunas.panama.persistence.entity.Fabricante
import jakarta.validation.constraints.NotNull
import java.util.Optional
import java.util.UUID

interface IFabricanteService {
    fun getFabricante(@NotNull licencia: String): Optional<Fabricante>

    fun getFabricanteByUserID(@NotNull idUser: UUID): Optional<Fabricante>
}
