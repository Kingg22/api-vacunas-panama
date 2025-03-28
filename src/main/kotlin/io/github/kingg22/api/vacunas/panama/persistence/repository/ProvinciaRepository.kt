package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Provincia
import org.springframework.data.jpa.repository.JpaRepository

interface ProvinciaRepository : JpaRepository<Provincia, Short>
