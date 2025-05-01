package io.github.kingg22.api.vacunas.panama.util

import org.springframework.http.ResponseEntity
import java.net.URI

fun permanentRedirect(uri: URI): ResponseEntity<Any> = ResponseEntity
    .status(308)
    .location(uri)
    .build()
