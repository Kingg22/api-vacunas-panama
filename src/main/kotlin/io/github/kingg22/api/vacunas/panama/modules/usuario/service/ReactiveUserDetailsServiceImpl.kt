package io.github.kingg22.api.vacunas.panama.modules.usuario.service

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
 * Security to verify and authenticate JWT tokens. Delegates user operations to [IUsuarioManagementService].
 */
@Component
class ReactiveUserDetailsServiceImpl(private val usuarioManagementService: IUsuarioManagementService) :
    ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> =
        Mono.just(usuarioManagementService.getUsuario(username))
            .flatMap { optUser ->
                optUser.map { Mono.just(it) }.orElseGet { Mono.empty() }
            }
            .switchIfEmpty(Mono.error { UsernameNotFoundException("User not found") })
            .flatMap { user ->
                Flux.fromIterable(user.roles)
                    .flatMap<SimpleGrantedAuthority> { role ->
                        val roleAuthorities = Mono.just(SimpleGrantedAuthority("ROLE_" + role.nombre.uppercase()))
                        val permisoAuthorities = Flux.fromIterable(role.permisos)
                            .map { permiso -> SimpleGrantedAuthority(permiso.nombre.uppercase()) }
                        Flux.concat(roleAuthorities, permisoAuthorities)
                    }
                    .collectList()
                    .map { authorities ->
                        User.withUsername(user.id.toString())
                            .password(user.password)
                            .authorities(authorities)
                            .accountExpired(false)
                            .accountLocked(false)
                            .disabled(user.isDisabled())
                            .build()
                    }
            }
}
