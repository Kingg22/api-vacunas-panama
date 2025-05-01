package io.github.kingg22.api.vacunas.panama.util

import io.restassured.path.json.JsonPath
import java.io.File
import java.nio.file.Paths

/** Get JSON as String with base path `"src/test/resources/"` without using Spring */
fun retrieveFileJson(path: String): String {
    val resourcesPath = Paths.get("src", "test", "resources").toAbsolutePath().toString()
    val filePath = Paths.get(resourcesPath, path).toString()
    return File(filePath).readText()
}

/** Remove all metadata with not testable information */
fun String.removeMetadata() = this.replace(",\\s*\"timestamp\":\\s*\".*?\"".toRegex(), "")
    .replace("\\s*\"code\":\\s*200".toRegex(), "\"code\": \"OK\"")

/**
 * Extracts a JSON token value based on the provided JSONPath from the string.
 *
 * @param path The JSONPath expression used to locate the desired value in the JSON content.
 * The path should start with "$." and conform to JSONPath syntax.
 * @return The value found at the specified JSONPath within the JSON content as a string.
 */
fun String.extractJsonToken(path: String): String {
    val jsonPath = JsonPath(this)
    return jsonPath.getString(path.removePrefix("$."))
}
