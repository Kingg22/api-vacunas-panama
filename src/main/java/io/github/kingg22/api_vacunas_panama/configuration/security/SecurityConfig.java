package io.github.kingg22.api_vacunas_panama.configuration.security;

import io.github.kingg22.api_vacunas_panama.service.TokenService;
import io.github.kingg22.api_vacunas_panama.service.UserDetailsServiceImpl;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${security.jwt.issuer}")
    private String issuer;
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final AccessDeniedHandler accessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenService tokenService, JwtDecoder jwtDecoder) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/vacunacion/**"))
                .headers(headers ->
                        headers.contentSecurityPolicy(cspc -> cspc
                                .policyDirectives("default-src 'none'; frame-ancestors 'none'; sandbox; media-src 'self'; object-src 'self';")))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/vacunacion/v1/account/register").permitAll()
                        .requestMatchers("/vacunacion/v1/account/login").permitAll()
                        .requestMatchers("/vacunacion/v1/account/restore/**").permitAll()
                        .requestMatchers("/vacunacion/v1/public/**").permitAll()
                        .requestMatchers("/vacunacion/v1/bulk/**").permitAll()
                        .requestMatchers("/vacunacion/v1/pdf/**").permitAll()
                        .requestMatchers("/vacunacion/v1/patient/**").hasAnyAuthority("PACIENTE_READ")
                        .requestMatchers("/vacunacion/v1/vaccines/**").hasAnyRole("DOCTOR", "ENFERMERA")
                        .anyRequest().authenticated())
                .addFilterAfter(new CustomJwtRefreshFilter(tokenService, jwtDecoder), BearerTokenAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }

    @Bean
    JwtDecoder jwtDecoder(TokenService tokenService) {
        NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder.withPublicKey(this.publicKey)
                .signatureAlgorithm(SignatureAlgorithm.RS256)
                .build();
        nimbusJwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        nimbusJwtDecoder.setJwtValidator(new CustomRedisJwtValidator(tokenService));
        return nimbusJwtDecoder;
    }

    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker() {
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }

    @Bean
    AuthenticationManager authenticationManager(UserDetailsServiceImpl userDetailsServiceImpl,
                                                PasswordEncoder passwordEncoder,
                                                CompromisedPasswordChecker compromisedPasswordChecker) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsServiceImpl);
        daoAuthenticationProvider.setCompromisedPasswordChecker(compromisedPasswordChecker);
        return new ProviderManager(daoAuthenticationProvider);
    }

}
