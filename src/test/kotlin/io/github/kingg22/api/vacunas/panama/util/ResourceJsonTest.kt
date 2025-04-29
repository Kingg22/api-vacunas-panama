package io.github.kingg22.api.vacunas.panama.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.core.io.ClassPathResource

/** Get JSON as String with base path `"src/test/resources/"` */
@Deprecated("Use retrieveFileJson without spring")
fun retrieveFileJson(path: String) = ClassPathResource(path).inputStream.bufferedReader().use { it.readText() }

/** Remove all metadata with not testable information */
fun String.removeMetadata() = this.replace(",\\s*\"timestamp\":\\s*\".*?\"".toRegex(), "")
    .replace("\\s*\"code\":\\s*200".toRegex(), "\"code\": \"OK\"")

// Maybe deprecated if change to use kotlin serialization instead of jackson
fun String.extractJsonToken(path: String): String {
    val json = jacksonObjectMapper().readTree(this)
    val keys = path.removePrefix("$.").split(".")
    var node = json
    for (key in keys) {
        node = node[key]
    }
    return node.asText()
}
