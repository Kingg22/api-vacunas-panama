package io.github.kingg22.api.vacunas.panama.service;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for loading user details during JWT authentication. Extends {@link UserDetailsService} and is used by Spring
 * Security to verify and authenticate JWT tokens. Delegates user operations to {@link UsuarioManagementService}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final IUsuarioManagementService usuarioManagementService;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        var usuario = usuarioManagementService.getUsuario(identifier);

        if (usuario.isPresent()) {
            var user = usuario.get();
            var authorities = user.getRoles().stream()
                    .flatMap(role -> Stream.concat(
                            Stream.of(new SimpleGrantedAuthority(
                                    "ROLE_" + role.getNombre().toUpperCase())),
                            role.getPermisos().stream()
                                    .map(permiso -> new SimpleGrantedAuthority(
                                            permiso.getNombre().toUpperCase()))))
                    .collect(Collectors.toSet());

            return User.withUsername(user.getId().toString())
                    .password(user.getPassword())
                    .authorities(authorities)
                    .accountExpired(false)
                    .accountLocked(false)
                    .disabled(user.isDisabled())
                    .build();
        }
        log.info("Not found user with identifier: {}, throwing UsernameNotFoundException", identifier);
        throw new UsernameNotFoundException("User not found");
    }
}
