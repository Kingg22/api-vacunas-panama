package io.github.kingg22.api.vacunas.panama.util

import reactor.core.publisher.Mono
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Returns the receiver object if it is not null; otherwise, returns the provided alternative.
 *
 * @param other The alternative value to return if the receiver object is null.
 * @return The receiver object if it is not null, otherwise the alternative value.
 */
@OptIn(ExperimentalContracts::class)
infix fun <T> T?.or(other: () -> T?): T? {
    contract {
        returnsNotNull() implies (this@or != null)
        callsInPlace(other, InvocationKind.AT_MOST_ONCE)
    }
    return this ?: other()
}

/**
 * Returns the receiver if it is not null; otherwise, returns the specified alternative value.
 *
 * @param other The alternative value to return if the receiver is null.
 * @return The receiver if not null, otherwise the specified alternative value _non null_.
 */
@OptIn(ExperimentalContracts::class)
infix fun <T> T?.orElse(other: () -> T): T {
    contract {
        callsInPlace(other, InvocationKind.AT_MOST_ONCE)
    }
    return this ?: other()
}

/**
 * Executes the given `presentConsumer` function if the nullable receiver is not null.
 * Otherwise, executes the `missingAction` function if the receiver is null.
 *
 * @param presentConsumer a function to be executed if the receiver is not null.
 * @param missingAction a function to be executed if the receiver is null.
 */
@OptIn(ExperimentalContracts::class)
fun <T> T?.ifPresentOrElse(presentConsumer: (T) -> Unit, missingAction: () -> Unit) {
    contract {
        callsInPlace(presentConsumer, InvocationKind.AT_MOST_ONCE)
        callsInPlace(missingAction, InvocationKind.AT_MOST_ONCE)
    }
    if (this != null) presentConsumer(this) else missingAction()
}

/**
 * Executes suspended the given `presentConsumer` function if the nullable receiver is not null.
 * Otherwise, executes the `missingAction` function if the receiver is null.
 *
 * @param presentConsumer a function to be executed if the receiver is not null.
 * @param missingAction a function to be executed if the receiver is null.
 */
@OptIn(ExperimentalContracts::class)
suspend fun <T> T?.ifPresentOrElseSuspend(presentConsumer: suspend (T) -> Unit, missingAction: suspend () -> Unit) {
    contract {
        callsInPlace(presentConsumer, InvocationKind.AT_MOST_ONCE)
        callsInPlace(missingAction, InvocationKind.AT_MOST_ONCE)
    }
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
