package io.github.kingg22.api.vacunas.panama.util

import io.quarkus.vertx.core.runtime.context.VertxContextSafetyToggle
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.impl.ContextInternal
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

val vertxLogger = logger("VertxExt")

fun getCurrentVertxContext(duplicate: Boolean) = Vertx.currentContext()
    ?: (Vertx.vertx().orCreateContext).also {
        vertxLogger.warn("Vertx current context is null, using orCreateContext")
    }
        ?.let { context: Context ->
            val contextInternal = context as ContextInternal
            if (duplicate) {
                vertxLogger.info("Duplicate Vertx context is needed, duplicating it...")
                val duplicatedContext = if (contextInternal.isDuplicate) {
                    vertxLogger.warn("Vertx context is already duplicated, skipping duplicate creation")
                    contextInternal
                } else {
                    contextInternal.duplicate()
                }
                checkNotNull(duplicatedContext) { "Vertx context duplicated failed, it's null" }
                vertxLogger.debug("Vertx context need mark safe, marking it as safe...")
                if (!VertxContextSafetyToggle.isExplicitlyMarkedAsSafe(duplicatedContext)) {
                    vertxLogger.warn("Vertx context is not marked as safe, marking it as safe...")
                    VertxContextSafetyToggle.setContextSafe(duplicatedContext, true)
                    vertxLogger.info("Vertx context marked as safe")
                } else {
                    vertxLogger.warn("Vertx context is already marked as safe, skipping safe marking")
                }
                return@let duplicatedContext
            } else {
                context
            }
        }

fun getVertxDispatcher(duplicate: Boolean = false) =
    checkNotNull(getCurrentVertxContext(duplicate)) { "vertxDispatcher: Vertx context is null" }.dispatcher()

/**
 * Executes a given block of code within a Vert.x context using its dispatcher for coroutine execution.
 * Ensures the Vert.x context is appropriately created and managed if not already available, providing a safe
 * execution environment.
 *
 * **Important**: When invoking another suspend function inside this block, ensure the original [CoroutineScope] is passed.
 * Losing the scope may break Vert.x context propagation and cause unexpected behavior in Hibernate Reactive.
 *
 * @param duplicate If you need a duplicated Vert.x context or not.
 * @param block A suspending lambda to be executed within the Vert.x context.
 * The `block` receives a [CoroutineScope] as a parameter to execute its operations within.
 * @return The result of the executed block.
 * @throws IllegalStateException if the Vert.x context fails to initialize.
 */
suspend inline fun <T> withVertx(duplicate: Boolean = false, crossinline block: suspend (CoroutineScope) -> T) =
    coroutineScope {
        withContext(getVertxDispatcher(duplicate)) {
            // this is CoroutineScope
            block(this)
        }
    }
