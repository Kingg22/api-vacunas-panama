package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.Fabricante
import io.github.kingg22.api.vacunas.panama.modules.fabricante.entity.toFabricanteDto
import io.github.kingg22.api.vacunas.panama.modules.fabricante.service.FabricanteService
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.RegistrationResult.RegistrationError
import io.github.kingg22.api.vacunas.panama.modules.usuario.service.RegistrationResult.RegistrationSuccess
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiResponseCode
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createApiErrorBuilder
import io.github.kingg22.api.vacunas.panama.response.ApiResponseFactory.createContentResponse
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FabricanteRegistrationStrategy(
    @Lazy private val fabricanteService: FabricanteService,
    @Lazy private val usuarioService: UsuarioService,
) : RegistrationStrategy {

    override fun validate(registerUserDto: RegisterUserDto): RegistrationResult {
        val licencia = registerUserDto.licenciaFabricante
            ?: return RegistrationError(
                createApiErrorBuilder {
                    withCode(ApiResponseCode.MISSING_INFORMATION)
                    withMessage("Falta licencia fabricante")
                },
            )

        return fabricanteService.getFabricante(licencia)?.let { fabricante ->
            when {
                fabricante.usuario?.disabled == true -> RegistrationError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.PERMISSION_DENIED)
                        withMessage("No puede registrarse")
                    },
                )

                fabricante.usuario?.id != null -> RegistrationError(
                    createApiErrorBuilder {
                        withCode(ApiResponseCode.ALREADY_EXISTS)
                        withMessage("Ya tiene usuario")
                    },
                )

                else -> RegistrationSuccess(fabricante)
            }
        } ?: RegistrationError(
            createApiErrorBuilder {
                withCode(ApiResponseCode.NOT_FOUND)
                withMessage("Fabricante no encontrado")
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
                val fabricante = resultValidate.outcome as? Fabricante
                    ?: return createContentResponse().apply {
                        addError(
                            createApiErrorBuilder {
                                withCode(ApiResponseCode.API_UPDATE_UNSUPPORTED)
                                withMessage("No se puede crear fabricante")
                            },
                        )
                    }

                usuarioService.createUser(registerUserDto.usuario) {
                    fabricante.usuario = it
                    it.fabricante = fabricante
                }

                createContentResponse().apply {
                    addData("fabricante", fabricante.toFabricanteDto())
                }
            }
        }
    }
}
