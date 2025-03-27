package io.github.kingg22.api.vacunas.panama.events

import io.github.kingg22.api.vacunas.panama.service.IUsuarioManagementService
import io.github.kingg22.api.vacunas.panama.util.logger
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class AuthenticationSuccessListener(private val usuarioService: IUsuarioManagementService) :
    ApplicationListener<AuthenticationSuccessEvent> {
    private val log = logger()

    override fun onApplicationEvent(event: AuthenticationSuccessEvent) {
        log.debug("Authentication Success Event: {}", event.toString())
        if (event.authentication != null && event.authentication.isAuthenticated) {
            val userId = event.authentication.name
            log.debug("User ID for update last used: {}", userId)
            if (userId.isNotBlank()) {
                try {
                    val uuid = UUID.fromString(userId)
                    usuarioService.updateLastUsed(uuid)
                    log.debug("User updated successfully for usuario: {}", userId)
                } catch (e: IllegalArgumentException) {
                    log.error("Invalid user ID: {} is not a valid UUID", userId, e)
                }
            }
        }
    }
}
