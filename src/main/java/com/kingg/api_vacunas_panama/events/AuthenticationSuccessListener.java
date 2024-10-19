package com.kingg.api_vacunas_panama.events;

import com.kingg.api_vacunas_panama.service.UsuarioTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final UsuarioTransactionService usuarioTransactionService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        log.debug("Authentication Success Event: {}", event.toString());
        if (event.getAuthentication() != null && event.getAuthentication().isAuthenticated()) {
            String userId = event.getAuthentication().getName();
            log.debug("User ID for update last used: {}", userId);
            if (userId != null && !userId.isBlank()) {
                usuarioTransactionService.updateLastUsed(UUID.fromString(userId));
            }
        }
    }

}
