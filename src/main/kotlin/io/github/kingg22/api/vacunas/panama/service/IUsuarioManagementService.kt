package io.github.kingg22.api.vacunas.panama.service

import io.github.kingg22.api.vacunas.panama.persistence.entity.Usuario
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiError
import io.github.kingg22.api.vacunas.panama.web.dto.IdNombreDto
import io.github.kingg22.api.vacunas.panama.web.dto.RegisterUser
import io.github.kingg22.api.vacunas.panama.web.dto.RestoreDto
import io.github.kingg22.api.vacunas.panama.web.dto.UsuarioDto
import jakarta.validation.constraints.NotNull
import org.springframework.security.core.Authentication
import java.io.Serializable
import java.util.Optional
import java.util.UUID

interface IUsuarioManagementService {
    fun getUsuario(identifier: String): Optional<Usuario>

    fun createUser(registerUser: RegisterUser): ApiContentResponse

    fun changePassword(restoreDto: RestoreDto): ApiContentResponse

    fun validateAuthoritiesRegister(
        @NotNull usuarioDto: UsuarioDto,
        @NotNull authentication: Authentication,
    ): List<ApiError>

    fun generateTokens(id: UUID): Map<String, Serializable>

    val idNombrePermisos: List<IdNombreDto>

    val idNombreRoles: List<IdNombreDto>

    fun getProfile(id: UUID): Map<String, Serializable>

    fun setLoginData(id: UUID): Map<String, Serializable>

    fun updateLastUsed(id: UUID)
}
