package io.github.kingg22.api.vacunas.panama.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Provides a logger for the given class using SLF4J.
 *
 * *Note*: if used inside a companion object, it will resolve the class name as `Class.Companion`.
 * @return Logger instance for the specified class.
 */
inline fun <reified T> T.logger(): Logger = LoggerFactory.getLogger(T::class.java)

fun logger(name: String): Logger = LoggerFactory.getLogger(name)
