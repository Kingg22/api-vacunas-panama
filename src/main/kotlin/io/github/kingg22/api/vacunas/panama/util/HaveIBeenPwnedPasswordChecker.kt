package io.github.kingg22.api.vacunas.panama.util

import jakarta.inject.Singleton
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * A utility class for checking passwords against the "Have I Been Pwned" API, which provides
 * information about whether a password has been exposed in known data breaches. It uses the
 * k-Anonymity model to protect the confidentiality of the full password.
 *
 * The password's SHA-1 hash is computed, and the prefix of the hash is used to retrieve a list
 * of potentially matching leaked passwords from the API. The suffix of the hash is checked
 * locally against this list to determine if the password has been compromised.
 *
 * The class uses a REST client to interact with the "Have I Been Pwned" API.
 *
 * @param webClient A REST client implementation that performs network requests to the "Have I Been Pwned"
 * password API. This client is used for retrieving leaked password hash prefixes based on
 * the SHA-1 hash prefix of the user's password.
 *
 * @see HaveIBeenPwnedPasswordChecker.isPasswordCompromised
 */
@Singleton
class HaveIBeenPwnedPasswordChecker(@RestClient private val webClient: ApiPwnedPasswordsClient) {
    private val logger = logger()

    companion object {
        private const val PREFIX_LENGTH = 5

        @Suppress("kotlin:S4790")
        private val sha1Digest: MessageDigest = MessageDigest.getInstance("SHA-1")
    }

    /**
     * Checks whether the given password has been compromised by consulting a leaked password database.
     *
     * @param password The password to check for compromise. It must be a non-empty string representing the plaintext password.
     * @return A boolean value indicating whether the password is found in the database of leaked passwords.
     */
    suspend fun isPasswordCompromised(password: String): Boolean {
        require(password.isNotBlank()) { "Password must not be blank" }
        val hash = hashPassword(password)
        val hexHash = hash.joinToString("") { "%02x".format(it) }.uppercase()
        val prefix = hexHash.substring(0, PREFIX_LENGTH)
        val suffix = hexHash.substring(PREFIX_LENGTH)

        val leakedList = getLeakedPasswordsForPrefix(prefix)
        return leakedList.any { it.startsWith(suffix) }.also {
            logger.trace("Password Hash '{}' is {} compromised", hash, if (!it) "not" else "")
        }
    }

    private suspend fun getLeakedPasswordsForPrefix(prefix: String): List<String> = try {
        val response = webClient.getLeakedPasswordsForPrefix(prefix)
        response.lineSequence().filter { it.isNotBlank() }.toList()
    } catch (ex: Exception) {
        logger.error("Request for leaked passwords failed", ex)
        emptyList()
    }

    private fun hashPassword(password: String): ByteArray =
        sha1Digest.digest(password.toByteArray(StandardCharsets.UTF_8))

    @RegisterRestClient(configKey = "api-pwnedpasswords")
    fun interface ApiPwnedPasswordsClient {
        @GET
        @Path("/range/{prefix: [a-zA-Z0-9]+}")
        suspend fun getLeakedPasswordsForPrefix(@PathParam("prefix") prefix: String): String
    }
}
