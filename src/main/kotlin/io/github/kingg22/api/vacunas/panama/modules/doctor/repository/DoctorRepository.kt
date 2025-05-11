package io.github.kingg22.api.vacunas.panama.modules.doctor.repository

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import io.github.kingg22.api.vacunas.panama.util.withSession
import io.quarkus.hibernate.reactive.panache.kotlin.PanacheRepositoryBase
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID

@ApplicationScoped
class DoctorRepository : PanacheRepositoryBase<Doctor, UUID> {
    suspend fun findByIdOrNull(id: UUID): Doctor? = withSession { findById(id).awaitSuspending() }
}
