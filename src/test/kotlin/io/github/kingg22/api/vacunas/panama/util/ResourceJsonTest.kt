package io.github.kingg22.api.vacunas.panama.util

import org.springframework.core.io.ClassPathResource

/** Get JSON as String with base path `"src/test/resources/"` */
fun retrieveFileJson(path: String) = ClassPathResource(path).inputStream.bufferedReader().use { it.readText() }

/** Remove all metadata with not testable information */
fun String.removeMetadata() = this.replace(",\\s*\"timestamp\":\\s*\".*?\"".toRegex(), "")

fun String.extractJsonToken(path: String): String {
    val json = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper().readTree(this)
    val keys = path.removePrefix("$.").split(".")
    var node = json
    for (key in keys) {
        node = node[key]
    }
    return node.asText()
}
