package io.github.kingg22.api.vacunas.panama.util

import io.quarkus.elytron.security.common.BcryptUtil
import org.wildfly.security.password.interfaces.BCryptPassword
import java.security.SecureRandom

/**
 * Hashes the invoking string using the bcrypt algorithm.
 *
 * @param iterationCount The cost factor for the bcrypt hashing process. Must be greater than 0. Default value is 10.
 * @param salt The salt to be used during hashing. Its size must equal the value of `BCryptPassword.BCRYPT_SALT_SIZE`.
 * @return The bcrypt-hashed string result.
 * @throws IllegalArgumentException If the iterationCount is less than or equal to 0, or if the size of the provided salt is incorrect.
 */
fun String.bcryptHash(iterationCount: Int = 10, salt: ByteArray? = null): String {
    val salt = salt ?: ByteArray(BCryptPassword.BCRYPT_SALT_SIZE).apply { SecureRandom().nextBytes(this) }
    require(iterationCount > 0) { "iterationCount must be > 0" }
    require(salt.size == BCryptPassword.BCRYPT_SALT_SIZE) {
        "salt size must be equal to ${BCryptPassword.BCRYPT_SALT_SIZE}"
    }
    return BcryptUtil.bcryptHash(this, iterationCount, salt)
}

/**
 * Verifies if the current string (_plain password_) matches the given bcrypt hashed password.
 *
 * @param passwordHash The bcrypt hashed password to compare against.
 * @return `true` if the current string matches the hash, `false` otherwise.
 */
fun String.bcryptMatch(passwordHash: String) = BcryptUtil.matches(this, passwordHash)

/** @see io.github.kingg22.api.vacunas.panama.util.HaveIBeenPwnedPasswordChecker.isPasswordCompromised */
suspend fun String.isCompromisedUsing(checker: HaveIBeenPwnedPasswordChecker) = checker.isPasswordCompromised(this)
