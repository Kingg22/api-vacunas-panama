package io.github.kingg22.api.vacunas.panama

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.security.Security
import java.time.ZoneOffset.UTC
import java.util.TimeZone

@SpringBootApplication
class ApiVacunasPanamaApplication

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone(UTC))
    // Temporal fix to: WARNING:
    // Unable to disable JVM DNS caching disabled in favor of link-local DNS caching because /layers/paketo-buildpacks_bellsoft-liberica/java-security-properties/java-security.properties is read-only
    Security.setProperty("networkaddress.cache.ttl", "0")
    runApplication<ApiVacunasPanamaApplication>(*args)
}
