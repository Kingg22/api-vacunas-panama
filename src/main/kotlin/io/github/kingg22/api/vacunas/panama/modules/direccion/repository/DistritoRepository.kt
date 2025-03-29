package io.github.kingg22.api.vacunas.panama.modules.direccion.repository

import io.github.kingg22.api.vacunas.panama.modules.direccion.entity.Distrito
import org.springframework.data.jpa.repository.JpaRepository

interface DistritoRepository : JpaRepository<Distrito, Short>
