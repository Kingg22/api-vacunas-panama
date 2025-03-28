package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.web.dto.PermisoDto;
import io.github.kingg22.api.vacunas.panama.web.dto.UsuarioDto;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/**
 * Service for managing JWT tokens.
 *
 * <p>This service is responsible for encoding information from DTOs entities previously fetched by
 * {@link UsuarioManagementService}, Its sets the issuer and time values based on application.properties.
 *
 * <p>The service now supports both access tokens and refresh tokens, ensuring the correct usage of each token type.
 * Tokens are stored in Redis cache to facilitate efficient validation and retrieval.
 *
 * <p>This service provides methods for validating the existence of tokens in the cache. It does not handler the
 * decoding of tokens; this is managed by the {@link org.springframework.security.oauth2.jwt.JwtDecoder}
 *
 * @see UsuarioManagementService
 * @see org.springframework.security.oauth2.jwt.JwtDecoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {
    private final JwtEncoder jwtEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expiration-time}")
    private Integer expirationTime;

    @Value("${security.jwt.refresh-time}")
    private Integer refreshTime;

    public Map<String, Serializable> generateTokens(
            @org.jetbrains.annotations.NotNull UsuarioDto usuarioDto, Map<String, Serializable> idsAdicionales) {
        var data = new LinkedHashMap<String, Serializable>();
        data.put(
                "access_token",
                this.createToken(usuarioDto.id().toString(), getRolesPermisos(usuarioDto), idsAdicionales));
        data.put("refresh_token", this.createRefreshToken(usuarioDto.id().toString()));
        return data;
    }

    public boolean isAccessTokenValid(
            @org.jetbrains.annotations.NotNull @NotNull String userId,
            @org.jetbrains.annotations.NotNull @NotNull String tokenId) {
        var key = generateKey("access", userId, tokenId);
        var hasKeyAccessToken = redisTemplate.hasKey(key);
        if (hasKeyAccessToken == null) {
            throw new IllegalStateException("Redis is unavailable, token validation failed");
        }
        return hasKeyAccessToken;
    }

    public boolean isRefreshTokenValid(
            @org.jetbrains.annotations.NotNull @NotNull String userId,
            @org.jetbrains.annotations.NotNull @NotNull String tokenId) {
        String key = "token:refresh:".concat(userId).concat(":").concat(tokenId);
        Boolean hasKeyRefreshToken = redisTemplate.hasKey(key);
        if (hasKeyRefreshToken == null) {
            throw new IllegalStateException("Redis is unavailable, token validation failed");
        }
        return hasKeyRefreshToken;
    }

    private Collection<String> getRolesPermisos(@org.jetbrains.annotations.NotNull @NotNull UsuarioDto usuarioDto) {
        if (usuarioDto.roles() == null || usuarioDto.roles().isEmpty()) {
            return Collections.emptyList();
        }
        return usuarioDto.roles().stream()
                .flatMap(role -> role != null && role.permisos() != null
                        ? Stream.concat(
                                Stream.of("ROLE_" + role.nombre().toUpperCase()),
                                role.permisos().stream().map(PermisoDto::nombre))
                        : null)
                .toList();
    }

    private String createToken(
            @NotNull String subject, Collection<String> rolesPermisos, Map<String, Serializable> claimsAdicionales) {
        var now = Instant.now();
        var builder = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .notBefore(now)
                .expiresAt(now.plusSeconds(expirationTime))
                .subject(subject)
                .claim("scope", rolesPermisos)
                .id(UUID.randomUUID().toString());

        if (claimsAdicionales != null) {
            for (var claim : claimsAdicionales.entrySet()) {
                if (claim.getValue() != null) {
                    builder.claim(claim.getKey(), claim.getValue());
                }
            }
        }
        var claims = builder.build();
        var header = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build();

        var jwtToken = this.jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
        log.debug(
                "created a token for: {}, expires at: {}, id_token: {}",
                subject,
                claims.getExpiresAt(),
                claims.getId());

        saveInCache("access", subject, claims.getId(), jwtToken);
        return jwtToken;
    }

    private String createRefreshToken(@org.jetbrains.annotations.NotNull @NotNull String subject) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .notBefore(now)
                .expiresAt(now.plusSeconds(refreshTime))
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .build();
        var header = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build();
        var jwtToken = this.jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
        log.debug(
                "created a refresh token for: {}, expires at: {}, id_token: {}",
                subject,
                claims.getExpiresAt(),
                claims.getId());

        saveInCache("refresh", subject, claims.getId(), jwtToken);
        return jwtToken;
    }

    private void saveInCache(
            @NotNull @org.jetbrains.annotations.NotNull String type,
            @NotNull @org.jetbrains.annotations.NotNull String subject,
            @NotNull @org.jetbrains.annotations.NotNull String id,
            @NotNull @org.jetbrains.annotations.NotNull String jwtToken) {
        redisTemplate.opsForValue().set(generateKey(type, subject, id), jwtToken, Duration.ofSeconds(refreshTime));
    }

    @org.jetbrains.annotations.NotNull
    private String generateKey(
            @NotNull @org.jetbrains.annotations.NotNull String type,
            @NotNull @org.jetbrains.annotations.NotNull String subject,
            @NotNull @org.jetbrains.annotations.NotNull String id) {
        var key = new StringBuilder("token:");
        key.append(type).append(":").append(subject).append(":").append(id);
        return key.toString();
    }
}
