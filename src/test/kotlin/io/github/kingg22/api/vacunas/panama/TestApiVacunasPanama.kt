package io.github.kingg22.api.vacunas.panama

import org.springframework.boot.fromApplication

fun main(args: Array<String>) {
    fromApplication<ApiVacunasPanamaApplication>()
        .with(TestcontainersConfiguration::class.java)
        .withAdditionalProfiles("test")
        .run(*args)
}
