package io.github.kingg22.api.vacunas.panama.util

import reactor.core.publisher.Mono

/**
 * Returns the receiver object if it is not null; otherwise, returns the provided alternative.
 *
 * @param other The alternative value to return if the receiver object is null.
 * @return The receiver object if it is not null, otherwise the alternative value.
 */
infix fun <T> T?.or(other: T?) = this ?: other

/**
 * Returns the receiver object if it is not null; otherwise, returns the provided alternative.
 *
 * @param other The alternative value to return if the receiver object is null.
 * @return The receiver object if it is not null, otherwise the alternative value.
 */
infix fun <T> T?.or(other: () -> T?) = this ?: other()

/**
 * Returns the receiver if it is not null; otherwise, returns the specified alternative value.
 *
 * @param other The alternative value to return if the receiver is null.
 * @return The receiver if not null, otherwise the specified alternative value _non null_.
 */
infix fun <T> T?.orElse(other: T) = this ?: other

/**
 * Returns the receiver if it is not null; otherwise, returns the specified alternative value.
 *
 * @param other The alternative value to return if the receiver is null.
 * @return The receiver if not null, otherwise the specified alternative value _non null_.
 */
infix fun <T> T?.orElse(other: () -> T) = this ?: other()

/**
 * Executes the given `presentConsumer` function if the nullable receiver is not null.
 * Otherwise, executes the `missingAction` function if the receiver is null.
 *
 * @param presentConsumer a function to be executed if the receiver is not null.
 * @param missingAction a function to be executed if the receiver is null.
 */
fun <T> T?.ifPresentOrElse(presentConsumer: (T) -> Unit, missingAction: () -> Unit) {
    if (this != null) presentConsumer(this) else missingAction()
}

/**
 * Converts a nullable value into a Reactor Mono.
 * If the value is non-null, wraps it in a Mono.
 * If the value is null, it returns an empty Mono.
 *
 * @param nullable The nullable value to be converted into a Mono.
 * @return A Mono containing the value if non-null, or an empty Mono if the value is null.
 */
fun <T> monoFromNullable(nullable: T?): Mono<T> = if (nullable != null) Mono.just(nullable) else Mono.empty()
