package io.github.kingg22.api.vacunas.panama.modules.usuario.service

import io.github.kingg22.api.vacunas.panama.modules.usuario.dto.UsuarioDto
import io.github.kingg22.api.vacunas.panama.util.monoFromNullable
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for loading user details during JWT authentication. Extends [ReactiveUserDetailsService] and is used by Spring
 * Security to verify and authenticate JWT tokens. Delegates user operations to [UsuarioService].
 */
@Component
class ReactiveUserDetailsServiceImpl(private val usuarioService: UsuarioService) : ReactiveUserDetailsService {
    override fun findByUsername(username: String): Mono<UserDetails> =
        monoFromNullable(usuarioService.getUsuarioByIdentifier(username))
            .switchIfEmpty(Mono.error { UsernameNotFoundException("User not found") })
            .flatMap { user: UsuarioDto ->
                val roles = user.roles.mapNotNull { it.nombre?.uppercase() }.toTypedArray()
                Flux.fromIterable(user.roles).flatMap { Flux.fromIterable(it.permisos) }
                    .flatMap { Mono.just(SimpleGrantedAuthority(it.nombre.uppercase())) }
                    .collectList()
                    .flatMap { permisoAuthorities: List<SimpleGrantedAuthority> ->
                        Mono.just(
                            User.withUsername(user.id.toString())
                                .password(user.password)
                                .roles(*roles)
                                .authorities(permisoAuthorities)
                                .accountExpired(false)
                                .accountLocked(false)
                                .disabled(user.disabled)
                                .build(),
                        )
                    }
            }
}
