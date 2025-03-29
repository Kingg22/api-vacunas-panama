package io.github.kingg22.api.vacunas.panama.configuration.security

import io.github.kingg22.api.vacunas.panama.util.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.Key
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

/**
 * Configuration class for managing RSA keys used in [org.springframework.security.oauth2.jwt.Jwt] authentication.
 *
 * This class is responsible for determining whether the RSA keys are provided as [String] values or as
 * [RSAPublicKey] and [RSAPrivateKey] objects in `application.properties`. If the keys are provided as
 * strings, they are encoded into [java.security.interfaces.RSAKey] objects for use in [org.springframework.security.oauth2.jwt.JwtEncoder]
 * and [org.springframework.security.oauth2.jwt.JwtDecoder]. This configuration is part of the Spring Security setup
 * for securing JWT tokens in a web application.
 *
 * The class provides methods to retrieve the public and private keys, either from properties or from
 * provided strings, and ensures proper decoding and key generation.
 *
 * @see SecurityConfig
 * @see RSAPublicKey
 * @see RSAPrivateKey
 * @see org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
 * @see org.springframework.security.config.annotation.web.builders.HttpSecurity
 */
@Configuration
class RsaConfig(
    @Value("\${security.jwt.public}") private val rsaPublicKey: String?,
    @Value("\${security.jwt.private}") private val rsaPrivateKey: String?,
    @Value("\${security.jwt.public.key}") val publicKey: RSAPublicKey?,
    @Value("\${security.jwt.private.key}") private val privateKey: RSAPrivateKey?,
) {
    private val log = logger()

    /**
     * Retrieves the public RSA key for JWT authentication.
     * If a public key is provided as a [RSAPublicKey] in the configuration, it will be used directly.
     * If the public key is provided as a [String], it will be decoded from Base64 and converted to a [RSAPublicKey].
     *
     * @return the [RSAPublicKey] used for JWT encoding/decoding, or null if no key is available.
     */
    @Bean
    fun retrievePublicKey(): RSAPublicKey? = (
        publicKey ?: rsaPublicKey?.let {
            log.info("Using public key from String")
            getKeyFromString(it, true) as RSAPublicKey
        }
        )?.also { log.info("Successfully loaded public key") }

    /**
     * Retrieves the private RSA key for JWT authentication.
     * If a private key is provided as a [RSAPrivateKey] in the configuration, it will be used directly.
     * If the private key is provided as a [String], it will be decoded from Base64 and converted to a [RSAPrivateKey].
     *
     * @return the [RSAPrivateKey] used for JWT encoding/decoding, or null if no key is available.
     */
    @Bean
    fun retrievePrivateKey(): RSAPrivateKey? = (
        privateKey ?: rsaPrivateKey?.let {
            log.info("Using private key from String")
            getKeyFromString(it, false) as RSAPrivateKey
        }
        )?.also { log.info("Successfully loaded private key") }

    /**
     * Decodes and generates an RSA key (either public or private) from a string representation.
     * The string is expected to be in Base64 format and may contain the "BEGIN" and "END" markers for the key.
     *
     * @param keyString the string containing the encoded RSA key (public or private).
     * @param isPublic whether the key is a public key or a private key.
     * @return the corresponding [RSAPublicKey] or [RSAPrivateKey].
     * @throws IllegalStateException if there is an error during key decoding or generation.
     */
    private fun getKeyFromString(keyString: String, isPublic: Boolean): Key {
        try {
            val cleanedKey = keyString
                .replace("-----BEGIN ${if (isPublic) "PUBLIC" else "PRIVATE"} KEY-----", "")
                .replace("-----END ${if (isPublic) "PUBLIC" else "PRIVATE"} KEY-----", "")
                .replace("\\s".toRegex(), "")
            val encoded = Base64.getDecoder().decode(cleanedKey)
            val keySpec = if (isPublic) X509EncodedKeySpec(encoded) else PKCS8EncodedKeySpec(encoded)
            val keyFactory = KeyFactory.getInstance("RSA")
            return if (isPublic) keyFactory.generatePublic(keySpec) else keyFactory.generatePrivate(keySpec)
        } catch (e: Exception) {
            throw IllegalStateException("Error loading ${if (isPublic) "public" else "private"} key from string", e)
        }
    }
}
