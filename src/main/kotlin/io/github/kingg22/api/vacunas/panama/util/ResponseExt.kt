package io.github.kingg22.api.vacunas.panama.util

import jakarta.ws.rs.core.Response
import java.net.URI

/**
 * Creates a permanent HTTP 308 redirect response to the specified URI.
 *
 * @param uri The target URI for the redirect.
 * @return A response object configured with a 308 status and the location header set to the specified URI.
 */
fun permanentRedirect(uri: URI): Response = Response
    .status(308)
    .location(uri)
    .build()

/**
 * Builds a permanent redirect response (HTTP status code 308) with the specified URI as the target location.
 *
 * @param uri The target location to which the client should be redirected. It is expected to be a valid URI string.
 * @return A Response object configured with a 308 status and a "Location" header set to the given URI.
 */
fun permanentRedirect(uri: String): Response = Response
    .status(308)
    .location(uri.toUri())
    .build()
