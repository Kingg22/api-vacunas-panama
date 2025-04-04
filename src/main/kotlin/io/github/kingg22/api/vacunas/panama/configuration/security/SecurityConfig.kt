package io.github.kingg22.api.vacunas.panama.configuration.security

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.authentication.password.ReactiveCompromisedPasswordChecker
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiReactivePasswordChecker
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    @Value("\${security.jwt.issuer}") private val issuer: String,
    private val privateKey: RSAPrivateKey,
    private val publicKey: RSAPublicKey,
    private val accessDeniedHandler: ServerAccessDeniedHandler,
    private val authenticationEntryPoint: ServerAuthenticationEntryPoint,
) {
    @Bean
    fun securityFilterChain(
        http: ServerHttpSecurity,
        reactiveJwtDecoder: ReactiveJwtDecoder,
        reactiveJwtAuthenticationConverter: ReactiveJwtAuthenticationConverterAdapter,
        jwtRefreshFilter: CustomJwtRefreshFilter,
    ): SecurityWebFilterChain = http
        .csrf {
            it.requireCsrfProtectionMatcher(PathPatternParserServerWebExchangeMatcher("/vacunacion/**"))
            it.disable()
        }
        .headers {
            it.contentSecurityPolicy { cspc ->
                cspc.policyDirectives(
                    "default-src 'none'; frame-ancestors 'none'; sandbox; media-src 'self'; object-src 'self';",
                )
            }
        }
        .authorizeExchange {
            it.pathMatchers(
                "/account/register",
                "/account/login",
                "/account/restore/**",
                "/public/**",
                "/bulk/**",
                "/pdf/**",
                "/direccion/provincias",
                "/direccion/distritos",
                "/sedes",
                "/vaccines",
                "/roles",
                "/roles/permisos",
            ).permitAll()
                .pathMatchers("/patient/**").hasAnyAuthority("PACIENTE_READ")
                .pathMatchers("/vaccines/**").hasAnyRole("DOCTOR", "ENFERMERA")
                .anyExchange().authenticated()
        }
        .addFilterAfter(jwtRefreshFilter, SecurityWebFiltersOrder.AUTHENTICATION) // check this
        .exceptionHandling {
            it.accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint)
        }
        .oauth2ResourceServer {
            it.jwt { jwt ->
                jwt.jwtDecoder(reactiveJwtDecoder)
                jwt.jwtAuthenticationConverter(reactiveJwtAuthenticationConverter)
            }
        }
        .requestCache { it.disable() }
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .formLogin { it.disable() }
        .logout { it.disable() }
        .httpBasic { it.disable() }
        .build()

    @Bean
    fun reactiveJwtAuthenticationConverter(): ReactiveJwtAuthenticationConverterAdapter {
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        grantedAuthoritiesConverter.setAuthorityPrefix("")
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope")
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        return ReactiveJwtAuthenticationConverterAdapter(converter)
    }

    @Bean
    fun reactiveJwtDecoder(jwtValidator: CustomRedisJwtValidator): ReactiveJwtDecoder {
        val nimbusJwtDecoder = NimbusReactiveJwtDecoder.withPublicKey(publicKey)
            .signatureAlgorithm(SignatureAlgorithm.RS256)
            .build()
        nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer))
        nimbusJwtDecoder.setJwtValidator(jwtValidator)
        return nimbusJwtDecoder
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk =
            RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build()
        return NimbusJwtEncoder(ImmutableJWKSet<SecurityContext?>(JWKSet(jwk)))
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun reactiveCompromisedPasswordChecker(): ReactiveCompromisedPasswordChecker =
        HaveIBeenPwnedRestApiReactivePasswordChecker()

    @Bean
    fun reactiveAuthenticationManager(
        userDetailsServiceImpl: ReactiveUserDetailsService,
        passwordEncoder: PasswordEncoder?,
        compromisedPasswordChecker: ReactiveCompromisedPasswordChecker?,
    ): ReactiveAuthenticationManager {
        val provider = UserDetailsRepositoryReactiveAuthenticationManager(userDetailsServiceImpl)
        provider.setPasswordEncoder(passwordEncoder)
        provider.setCompromisedPasswordChecker(compromisedPasswordChecker)
        return provider
    }
}
