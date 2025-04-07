package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.setAndAwait
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.io.Serializable
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Service
class TokenServiceImpl(
    private val jwtEncoder: JwtEncoder,
    private val reactiveRedisTemplate: ReactiveStringRedisTemplate,
    @Value("\${security.jwt.issuer}") private val issuer: String,
    @Value("\${security.jwt.expiration-time}") private val expirationTime: Long,
    @Value("\${security.jwt.refresh-time}") private val refreshTime: Long,
) : TokenService {
    // TODO make suspend fun
    override fun generateTokens(
        usuarioDto: UsuarioDto,
        idsAdicionales: Map<String, Serializable>,
    ): Map<String, Serializable> = runBlocking {
        mapOf(
            "access_token" to createToken(
                subject = usuarioDto.id.toString(),
                rolesPermisos = getRolesPermisos(usuarioDto),
                claims = idsAdicionales,
            ),
            "refresh_token" to createRefreshToken(usuarioDto.id.toString()),
        )
    }

    override fun isAccessTokenValid(userId: String, tokenId: String): Mono<Boolean> =
        reactiveRedisTemplate.hasKey(generateKey("access", userId, tokenId))

    override fun isRefreshTokenValid(userId: String, tokenId: String): Mono<Boolean> =
        reactiveRedisTemplate.hasKey(generateKey("refresh", userId, tokenId))

    private suspend fun createToken(
        subject: String,
        rolesPermisos: Collection<String>,
        claims: Map<String, Serializable>?,
    ): String {
        val (id, token) = encodeJwt(subject, expirationTime, claims.orEmpty(), rolesPermisos)
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
        claimsExtra: Map<String, Serializable> = emptyMap(),
        scope: Collection<String>? = null,
    ): Pair<String, String> {
        val now = Instant.now()
        val id = UUID.randomUUID().toString()

        val builder = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(now)
            .notBefore(now)
            .expiresAt(now.plusSeconds(expiresIn))
            .subject(subject)
            .id(id)

        scope?.let { builder.claim("scope", it) }
        claimsExtra.forEach { (key, value) -> builder.claim(key, value) }

        val claims = builder.build()
        val header = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build()
        val token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue

        return id to token
    }

    private suspend fun saveInCache(key: String, jwtToken: String, expirationTime: Long) {
        reactiveRedisTemplate.opsForValue()
            .setAndAwait(key, jwtToken, timeout = expirationTime.seconds.toJavaDuration())
    }

    private fun generateKey(type: String, subject: String, id: String) = "token:$type:$subject:$id"

    private fun getRolesPermisos(usuarioDto: UsuarioDto) = usuarioDto.roles
        ?.filterNotNull()
        ?.flatMap { role ->
            val permisos = role.permisos?.mapNotNull { it.nombre } ?: emptyList()
            listOf("ROLE_${role.nombre?.uppercase()}") + permisos
        }.orEmpty()
}
