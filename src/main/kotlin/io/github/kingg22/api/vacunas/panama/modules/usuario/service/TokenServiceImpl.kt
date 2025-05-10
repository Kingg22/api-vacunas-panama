package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.util.logger
import io.smallrye.jwt.build.Jwt
import io.smallrye.jwt.build.JwtClaimsBuilder
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.jwt.Claims
import java.io.Serializable
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class TokenServiceImpl(
    @param:ConfigProperty(name = "security.jwt.issuer") private val issuer: String,
    @param:ConfigProperty(name = "security.jwt.expiration-time") private val expirationTime: Long,
    @param:ConfigProperty(name = "security.jwt.refresh-time") private val refreshTime: Long,
) : TokenService {
    private val log = logger()

    override suspend fun generateTokens(
        usuarioDto: UsuarioDto,
        idsAdicionales: Map<String, Serializable?>,
    ): Map<String, Serializable> = checkNotNull(usuarioDto.id) { "UsuarioDto id is null. $usuarioDto" }.let {
        mapOf(
            "access_token" to createToken(
                subject = usuarioDto.id.toString(),
                rolesPermisos = getRolesPermisos(usuarioDto),
                claims = idsAdicionales,
            ),
            "refresh_token" to createRefreshToken(usuarioDto.id.toString()),
        )
    }

    override suspend fun isAccessTokenValid(userId: String, tokenId: String): Boolean = true
    // TODO
    // reactiveRedisTemplate.hasKey(generateKey("access", userId, tokenId))

    override suspend fun isRefreshTokenValid(userId: String, tokenId: String): Boolean = false
    // TODO
    // reactiveRedisTemplate.hasKey(generateKey("refresh", userId, tokenId))

    private suspend fun createToken(
        subject: String,
        rolesPermisos: Set<String>,
        claims: Map<String, Serializable?>,
    ): String {
        val (id, token) = encodeJwt(subject, expirationTime, claims, rolesPermisos)
        saveInCache(generateKey("access", subject, id), token, expirationTime)
        return token
    }

    private suspend fun createRefreshToken(subject: String): String {
        val (id, token) = encodeJwt(subject, refreshTime)
        saveInCache(generateKey("refresh", subject, id), token, refreshTime)
        return token
    }

    private fun encodeJwt(
        subject: String,
        expiresIn: Long,
        claimsExtra: Map<String, Serializable?> = emptyMap(),
        scope: Set<String> = emptySet(),
    ): Pair<String, String> {
        val now: Instant = Instant.now()
        val id = UUID.randomUUID().toString()

        val jwtBuilder: JwtClaimsBuilder = Jwt.claims()
            .issuer(issuer)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiresIn))
            .subject(subject)
            .upn(subject)
            .groups(scope)
            .claim(Claims.jti, id)

        claimsExtra.forEach { (key, value) ->
            if (key.isNotBlank() && value != null) {
                jwtBuilder.claim(key, value)
            }
        }

        val token = jwtBuilder.sign()

        checkNotNull(token) { "Generated token is null. Subject: $subject, id: $id, token: $token" }
        check(token.isNotBlank()) { "Generated token is blank. Subject: $subject, id: $id, token: $token" }
        log.trace("Generated token for subject: {}, id: {}, token: {}", subject, id, token)
        return id to token
    }

    private suspend fun saveInCache(key: String, jwtToken: String, expirationTime: Long) {
        // reactiveRedisTemplate.opsForValue().setAndAwait(key, jwtToken, timeout = expirationTime.seconds.toJavaDuration())
    }

    private fun generateKey(type: String, subject: String, id: String) = "token:$type:$subject:$id"

    private fun getRolesPermisos(usuarioDto: UsuarioDto) = usuarioDto.roles
        .flatMap { role ->
            val permisos = role.permisos.map { it.nombre }
            if (role.nombre != null) {
                listOf("ROLE_${role.nombre.uppercase()}") + permisos
            } else {
                permisos
            }
        }.toSet()
}
