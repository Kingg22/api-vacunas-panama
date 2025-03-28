package io.github.kingg22.api.vacunas.panama.persistence.repository

import io.github.kingg22.api.vacunas.panama.persistence.entity.Distrito
import org.springframework.data.jpa.repository.JpaRepository

interface DistritoRepository : JpaRepository<Distrito, Short>
