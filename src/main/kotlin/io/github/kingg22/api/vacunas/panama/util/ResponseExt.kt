package io.github.kingg22.api.vacunas.panama.util

import jakarta.ws.rs.core.Response
import java.net.URI

fun permanentRedirect(uri: URI): Response = Response
    .status(308)
    .location(uri)
    .build()

fun permanentRedirect(uri: String): Response = Response
    .status(308)
    .location(uri.toUri())
    .build()
