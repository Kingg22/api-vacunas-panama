package io.github.kingg22.api.vacunas.panama.service;

import io.github.kingg22.api.vacunas.panama.persistence.entity.Permiso;
import io.github.kingg22.api.vacunas.panama.persistence.entity.Usuario;
import io.github.kingg22.api.vacunas.panama.web.dto.PermisoDto;
import io.github.kingg22.api.vacunas.panama.web.dto.UsuarioDto;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
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
 * <p>
 * This service is responsible for encoding information from DTOs  entities previously fetched by {@link UsuarioManagementService},
 * Its sets the issuer and time values based on application.properties.
 * </p>
 * <p>
 * The service now supports both access tokens and refresh tokens, ensuring the correct usage of each token type.
 * Tokens are stored in Redis cache to facilitate efficient validation and retrieval.
 * </p>
 * <p>
 * This service provides methods for validating the existence of tokens in the cache. It does not handler the decoding of
 * tokens; this is managed by the {@link org.springframework.security.oauth2.jwt.JwtDecoder}
 * </p>
 *
 * @see UsuarioManagementService
 * @see org.springframework.security.oauth2.jwt.JwtDecoder
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expiration-time}")
    private Integer expirationTime;

    @Value("${security.jwt.refresh-time}")
    private Integer refreshTime;

    public Map<String, Serializable> generateTokens(UsuarioDto usuarioDto, Map<String, Serializable> idsAdicionales) {
        Map<String, Serializable> data = new LinkedHashMap<>();
        data.put(
                "access_token",
                this.createToken(usuarioDto.id().toString(), getRolesPermisos(usuarioDto), idsAdicionales));
        data.put("refresh_token", this.createRefreshToken(usuarioDto.id().toString()));
        return data;
    }

    public Map<String, Serializable> generateTokens(Usuario usuario, Map<String, Serializable> idsAdicionales) {
        Map<String, Serializable> data = new LinkedHashMap<>();
        data.put(
                "access_token",
                this.createToken(usuario.getId().toString(), getRolesPermisos(usuario), idsAdicionales));
        data.put("refresh_token", this.createRefreshToken(usuario.getId().toString()));
        return data;
    }

    public boolean isAccessTokenValid(@NotNull String userId, @NotNull String tokenId) {
        String key = "token:access:".concat(userId).concat(":").concat(tokenId);
        Boolean hasKeyAccessToken = redisTemplate.hasKey(key);
        if (hasKeyAccessToken == null) {
            throw new IllegalStateException("Redis is unavailable, token validation failed");
        }
        return hasKeyAccessToken;
    }

    public boolean isRefreshTokenValid(@NotNull String userId, @NotNull String tokenId) {
        String key = "token:refresh:".concat(userId).concat(":").concat(tokenId);
        Boolean hasKeyRefreshToken = redisTemplate.hasKey(key);
        if (hasKeyRefreshToken == null) {
            throw new IllegalStateException("Redis is unavailable, token validation failed");
        }
        return hasKeyRefreshToken;
    }

    private Collection<String> getRolesPermisos(@NotNull UsuarioDto usuarioDto) {
        return usuarioDto.roles().stream()
                .flatMap(role -> role != null && role.permisos() != null
                        ? Stream.concat(
                                Stream.of("ROLE_" + role.nombre().toUpperCase()),
                                role.permisos().stream().map(PermisoDto::nombre))
                        : null)
                .toList();
    }

    private Collection<String> getRolesPermisos(@NotNull Usuario usuario) {
        return usuario.getRoles().stream()
                .flatMap(rol -> rol != null && rol.getPermisos() != null
                        ? Stream.concat(
                                Stream.of("ROLE_" + rol.getNombre().toUpperCase()),
                                rol.getPermisos().stream().map(Permiso::getNombre))
                        : null)
                .toList();
    }

    private String createToken(
            @NotNull String subject, Collection<String> rolesPermisos, Map<String, Serializable> claimsAdicionales) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .notBefore(now)
                .expiresAt(now.plusSeconds(expirationTime))
                .subject(subject)
                .claim("scope", rolesPermisos)
                .id(UUID.randomUUID().toString());

        if (claimsAdicionales != null) {
            for (Map.Entry<String, Serializable> claim : claimsAdicionales.entrySet()) {
                if (claim.getValue() != null) {
                    builder.claim(claim.getKey(), claim.getValue());
                }
            }
        }
        JwtClaimsSet claims = builder.build();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build();

        String jwtToken = this.jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
        log.debug(
                "created a token for: {}, expires at: {}, id_token: {}",
                subject,
                claims.getExpiresAt(),
                claims.getId());

        String key = "token:access:".concat(subject).concat(":").concat(claims.getId());
        redisTemplate.opsForValue().set(key, jwtToken, Duration.ofSeconds(expirationTime));

        return jwtToken;
    }

    private String createRefreshToken(@NotNull String subject) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .notBefore(now)
                .expiresAt(now.plusSeconds(refreshTime))
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .build();
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build();
        String jwtToken = this.jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
        log.debug(
                "created a refresh token for: {}, expires at: {}, id_token: {}",
                subject,
                claims.getExpiresAt(),
                claims.getId());

        String key = "token:refresh:".concat(subject).concat(":").concat(claims.getId());
        redisTemplate.opsForValue().set(key, jwtToken, Duration.ofSeconds(refreshTime));

        return jwtToken;
    }
}
