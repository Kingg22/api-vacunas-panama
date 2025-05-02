package io.github.kingg22.api.vacunas.panama.modules.usuario.service

/**
 * Service for loading user details during JWT authentication. Extends [ReactiveUserDetailsService] and is used by Spring
 * Security to verify and authenticate JWT tokens. Delegates user operations to [UsuarioService].
 */
/*
TODO maybe this is not longer necesary
@Component
class ReactiveUserDetailsServiceImpl(private val usuarioService: UsuarioService) : ReactiveUserDetailsService {
    private val log = logger()
    override fun findByUsername(username: String): Mono<UserDetails> =
        mono { usuarioService.getUsuarioByIdentifier(username) }
            .switchIfEmpty(Mono.error { UsernameNotFoundException("User not found") })
            .flatMap { user: UsuarioDto ->
                val roles = user.roles.mapNotNull { it.nombre?.uppercase() }.toTypedArray()
                log.trace("Roles for user {}: {}", user.id, roles.contentToString())
                Flux.fromIterable(user.roles).flatMap { Flux.fromIterable(it.permisos) }
                    .flatMap { Mono.just(SimpleGrantedAuthority(it.nombre.uppercase())) }
                    .collectList()
                    .flatMap { permisoAuthorities: List<SimpleGrantedAuthority> ->
                        log.trace("Permisos for user {}: {}", user.id, permisoAuthorities.map { it.authority })
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
*/
