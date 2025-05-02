package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.io.Serializable
import java.time.Instant
import java.util.UUID

@ApplicationScoped
class TokenServiceImpl(
    @ConfigProperty(name = "security.jwt.issuer") private val issuer: String,
    @ConfigProperty(name = "security.jwt.expiration-time") private val expirationTime: Long,
    @ConfigProperty(name = "security.jwt.refresh-time") private val refreshTime: Long,
) : TokenService {
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
        rolesPermisos: Collection<String>,
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
        scope: Collection<String>? = null,
    ): Pair<String, String> {
        val now = Instant.now()
        val id = UUID.randomUUID().toString()

        /*
            TODO: implement JWT
            val builder = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .notBefore(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .subject(subject)
                .id(id)

            scope?.let { builder.claim("scope", it) }
            claimsExtra.forEach { (key, value) ->
                if (key.isNotBlank() && value != null) {
                    builder.claim(key, value)
                }
            }

            val claims = builder.build()
            val header = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build()
            val token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
         */

        return id to ""
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
        }
}
