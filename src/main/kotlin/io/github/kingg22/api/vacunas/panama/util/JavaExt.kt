package io.github.kingg22.api.vacunas.panama.util

import java.net.URI
import java.net.URI.create

/**
 * Creates a URI by parsing the given string.
 * IF any URISyntaxException thrown by the constructor is caught and wrapped in a new IllegalArgumentException object,
 * which is then thrown.
 *
 * @see URI.create
 */
fun String.toUri(): URI = create(this)
