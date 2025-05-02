package io.github.kingg22.api.vacunas.panama.modules.usuario.events

/*
TODO find a solution to this

@Component
class AuthenticationSuccessListener(private val usuarioService: UsuarioService) :
    ApplicationListener<AuthenticationSuccessEvent> {
    private val log = logger()

    override fun onApplicationEvent(event: AuthenticationSuccessEvent) {
        log.debug("Authentication Success Event: {}", event.toString())
        if (event.authentication != null && event.authentication.isAuthenticated) {
            val userId = event.authentication.name
            log.debug("User ID for update last used: {}", userId)
            if (!userId.isNullOrBlank()) {
                try {
                    val uuid: UUID = UUID.fromString(userId)
                    CoroutineScope(Dispatchers.Default).launch {
                        usuarioService.updateLastUsed(uuid)
                        log.debug("User updated successfully for usuario: {}", userId)
                    }
                } catch (e: IllegalArgumentException) {
                    log.error("Invalid user ID: {} is not a valid UUID", userId, e)
                }
            }
        }
    }
}
*/
