package io.github.kingg22.api.vacunas.panama.modules.fabricante.service

import io.github.kingg22.api.vacunas.panama.modules.fabricante.dto.FabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.toFabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.fabricante.repository.FabricanteRepository
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.UUID

@Service
class FabricanteServiceImpl(private val fabricanteRepository: FabricanteRepository) : FabricanteService {
    override fun getFabricante(licencia: String): Optional<FabricanteDto> =
        fabricanteRepository.findByLicencia(licencia).map { it.toFabricanteDto() }

    override fun getFabricanteByUserID(idUser: UUID): Optional<FabricanteDto> =
        fabricanteRepository.findByUsuario_Id(idUser).map { it.toFabricanteDto() }
}
