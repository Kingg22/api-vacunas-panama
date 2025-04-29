package io.github.kingg22.api.vacunas.panama.modules.vacuna.domain

import io.github.kingg22.api.vacunas.panama.modules.doctor.domain.DoctorModel
import io.github.kingg22.api.vacunas.panama.modules.paciente.domain.PacienteModel
import io.github.kingg22.api.vacunas.panama.modules.persona.domain.PersonaModel
import io.github.kingg22.api.vacunas.panama.modules.sede.domain.SedeModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

/**
 * Domain model representing a vaccine dose.
 */
data class DosisModel(
    val id: UUID? = null,
    val paciente: PacienteModel,
    val numeroDosis: String,
    val vacuna: VacunaModel,
    val sede: SedeModel,
    val lote: String? = null,
    val doctor: DoctorModel? = null,
    val fechaAplicacion: LocalDateTime,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
) {
    /**
     * @param pacienteId The UUID of the patient.
     * @param vacunaId The UUID of the vaccine.
     * @param sedeId The UUID of the sede.
     * @param numeroDosis The number of the dose.
     * @param lote The lot of the vaccine (optional).
     * @param doctorId The UUID of the doctor (optional).
     * @param fechaAplicacion The date of application (optional).
     */
    constructor(
        pacienteId: UUID,
        vacunaId: UUID,
        sedeId: UUID,
        numeroDosis: String,
        lote: String? = null,
        doctorId: UUID? = null,
        fechaAplicacion: LocalDateTime? = null,
    ) : this(
        id = null,
        PacienteModel(PersonaModel(pacienteId), createdAt = LocalDateTime.now(ZoneOffset.UTC)),
        numeroDosis,
        VacunaModel(vacunaId, "", createdAt = LocalDateTime.now(ZoneOffset.UTC)),
        SedeModel(sedeId, "", createdAt = LocalDateTime.now()),
        lote,
        doctorId?.let { DoctorModel(it, PersonaModel(), createdAt = LocalDateTime.now(ZoneOffset.UTC)) },
        fechaAplicacion ?: LocalDateTime.now(ZoneOffset.UTC),
        LocalDateTime.now(ZoneOffset.UTC),
        null,
    )
}
