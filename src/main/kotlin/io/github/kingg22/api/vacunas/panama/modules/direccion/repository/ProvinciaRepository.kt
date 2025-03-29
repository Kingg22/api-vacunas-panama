package io.github.kingg22.api.vacunas.panama.modules.direccion.repository

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Provincia
import org.springframework.data.jpa.repository.JpaRepository

interface ProvinciaRepository : JpaRepository<Provincia, Short>
