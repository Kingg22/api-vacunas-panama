package io.github.kingg22.api.vacunas.panama.modules.usuario.entity

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.RolDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.fromRol
import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.fromUsuario

fun Rol.toRolDto() = RolDto.fromRol(this)

fun Usuario.toUsuarioDto() = UsuarioDto.fromUsuario(this)
