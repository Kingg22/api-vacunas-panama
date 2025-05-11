@file:Suppress("ktlint:standard:no-empty-file")

package io.github.kingg22.api.vacunas.panama.configuration.security

/*
TODO all configuration need be change to quarkus or something
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
            it.requireCsrfProtectionMatcher(
                NegatedServerWebExchangeMatcher(PathPatternParserServerWebExchangeMatcher("/**/")),
            )
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
                "/account/restore/**/",
                "/public/**/",
                "/bulk/**/",
                "/direccion/provincias",
                "/direccion/distritos",
                "/sedes",
                "/vaccines",
                "/roles",
                "/roles/permisos",
            ).permitAll()
                .pathMatchers("/patient/**/", "/pdf/**/").hasAnyAuthority("PACIENTE_READ")
                .pathMatchers("/vaccines/**/").hasAnyRole("DOCTOR", "ENFERMERA")
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
    fun reactiveJwtDecoder(): ReactiveJwtDecoder {
        val nimbusJwtDecoder = NimbusReactiveJwtDecoder.withPublicKey(publicKey)
            .signatureAlgorithm(SignatureAlgorithm.RS256)
            .build()
        nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer))
        return nimbusJwtDecoder
    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk =
            RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build()
        return NimbusJwtEncoder(ImmutableJWKSet(JWKSet(jwk)))
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun reactiveCompromisedPasswordChecker(): ReactiveCompromisedPasswordChecker =
        HaveIBeenPwnedRestApiReactivePasswordChecker()

    @Bean
    fun reactiveAuthenticationManager(
        userDetailsServiceImpl: ReactiveUserDetailsService,
        passwordEncoder: PasswordEncoder,
        compromisedPasswordChecker: ReactiveCompromisedPasswordChecker,
    ): ReactiveAuthenticationManager {
        val provider = UserDetailsRepositoryReactiveAuthenticationManager(userDetailsServiceImpl)
        provider.setPasswordEncoder(passwordEncoder)
        provider.setCompromisedPasswordChecker(compromisedPasswordChecker)
        return provider
    }
}
*/
