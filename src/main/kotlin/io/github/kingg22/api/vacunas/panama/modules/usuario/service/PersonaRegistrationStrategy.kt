package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.Doctor
import io.github.kingg22.api.vacunas.panama.modules.doctor.entity.toDoctorDto
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.Paciente
import io.github.kingg22.api.vacunas.panama.modules.paciente.entity.toPacienteDto
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.Persona
import io.github.kingg22.api.vacunas.panama.modules.persona.entity.toPersonaDto
import io.github.kingg22.api.vacunas.panama.modules.persona.service.PersonaService
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.RegistrationResult.RegistrationError
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.RegistrationResult.RegistrationSuccess
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createContentResponse
import io.github.kingg22.api.vacunas.panama.util.logger
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PersonaRegistrationStrategy(
    @Lazy private val personaService: PersonaService,
    @Lazy private val usuarioService: UsuarioService,
) : RegistrationStrategy {
    private val log = logger()

    override fun validate(registerUserDto: RegisterUserDto): RegistrationResult {
        val identifier = registerUserDto.cedula ?: registerUserDto.pasaporte
            ?: return RegistrationError(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.MISSING_INFORMATION)
                    withMessage("Falta cédula o pasaporte")
                },
            )

        return personaService.getPersona(identifier)?.let { persona ->
            when {
                persona.disabled -> RegistrationError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.PERMISSION_DENIED)
                        withMessage("No puede registrarse")
                    },
                )

                persona.usuario?.id != null -> RegistrationError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.ALREADY_EXISTS)
                        withMessage("La persona ya tiene un usuario registrado")
                    },
                )

                else -> RegistrationSuccess(persona)
            }
        } ?: RegistrationError(
            createApiErrorBuilder {
                withCode(ApiResponseCode.NOT_FOUND)
                withMessage("Persona no encontrada")
            },
        )
    }

    @Transactional
    override fun create(registerUserDto: RegisterUserDto): ApiContentResponse {
        val resultValidate = validate(registerUserDto)
        return when (resultValidate) {
            is RegistrationError -> createContentResponse().apply {
                addErrors(resultValidate.errors)
            }

            is RegistrationSuccess -> {
                val persona = (
                    resultValidate.outcome as? Persona
                        ?: return createContentResponse().apply {
                            addError(
                                createApiErrorBuilder {
                                    withCode(ApiResponseCode.API_UPDATE_UNSUPPORTED)
                                    withMessage("No se puede crear persona")
                                },
                            )
                        }
                    )
                log.debug("Persona validated: {}", persona)
                log.debug("Persona ID: {}", persona.id)

                usuarioService.createUser(registerUserDto.usuario) {
                    persona.usuario = it
                    it.persona = persona
                }

                createContentResponse().apply {
                    addData("persona", persona.toPersonaDto())
                    if (persona is Paciente) addData("paciente", persona.toPacienteDto())
                    if (persona is Doctor) addData("doctor", persona.toDoctorDto())
                }
            }
        }
    }
}
