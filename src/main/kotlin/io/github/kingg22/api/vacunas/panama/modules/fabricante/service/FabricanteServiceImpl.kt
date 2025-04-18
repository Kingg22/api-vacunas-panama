package io.github.kingg22.api.vacunas.panama.modules.fabricante.service

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.toFabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.fabricante.repository.FabricanteRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FabricanteServiceImpl(private val fabricanteRepository: FabricanteRepository) : FabricanteService {
    override fun getFabricante(licencia: String) = fabricanteRepository.findByLicencia(licencia)?.toFabricanteDto()

    override fun getFabricanteByUserID(idUser: UUID) = fabricanteRepository.findByUsuario_Id(idUser)?.toFabricanteDto()
}
