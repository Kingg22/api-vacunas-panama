package io.github.kingg22.api.vacunas.panama.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

fun permanentRedirect(uri: URI): ResponseEntity<Any> = ResponseEntity
    .status(HttpStatus.PERMANENT_REDIRECT)
    .location(uri)
    .build()
