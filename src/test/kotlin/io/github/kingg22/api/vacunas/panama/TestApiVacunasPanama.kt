package io.github.kingg22.api.vacunas.panama

import org.springframework.boot.fromApplication
import java.time.ZoneOffset.UTC
import java.util.TimeZone

fun main(args: Array<String>) {
    TimeZone.setDefault(TimeZone.getTimeZone(UTC))
    fromApplication<ApiVacunasPanamaApplication>()
        .with(TestcontainersConfiguration::class.java)
        .withAdditionalProfiles("test")
        .run(*args)
}
