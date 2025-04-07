package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.common.dto.IdNombreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RegisterUserDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RestoreDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.entity.Usuario
import io.github.kingg22.api.vacunas.panama.response.ApiContentResponse
import io.github.kingg22.api.vacunas.panama.response.ApiError
import jakarta.validation.constraints.NotNull
import org.springframework.security.core.Authentication
import java.io.Serializable
import java.util.Optional
import java.util.UUID

interface IUsuarioManagementService {
    fun getUsuario(identifier: String): Optional<Usuario>

    fun createUser(registerUserDto: RegisterUserDto): ApiContentResponse

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
