import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.InventoryMarkdownReportRenderer
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import java.time.Instant

plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    kotlin("plugin.spring") version libs.versions.kotlin.get()
    kotlin("plugin.jpa") version libs.versions.kotlin.get()
    java
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.ksp)
    alias(libs.plugins.spotless)
    alias(libs.plugins.license.report)
    alias(libs.plugins.kover)
}

group = "io.github.kingg22"
version = "0.13.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    implementation(libs.bundles.implementation)

    runtimeOnly(libs.bundles.runtimeOnly)

    ksp(libs.konvert)

    testImplementation(libs.bundles.testImplementation)
    testImplementation(libs.spring.boot.starter.test) {
        exclude(module = "mockito-core")
    }

    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    encoding("UTF-8")
    java {
        target("src/*/java/**/*.java")
        encoding("Cp1252")
        palantirJavaFormat("2.61.0").formatJavadoc(true)
        importOrder().semanticSort()
        removeUnusedImports()
    }
    kotlin {
        target("src/*/kotlin/**/*.kt")
        ktlint()
    }
    kotlinGradle {
        targetExclude("build/generated/**")
        ktlint()
    }
    sql {
        target("src/main/resources/**/*.sql", "*.sql", "containers/database/*.sql")
    }
}

licenseReport {
    renderers = arrayOf(InventoryMarkdownReportRenderer("THIRD-PARTY.md"))
    filters = arrayOf(LicenseBundleNormalizer())
}

kover {
    reports.filters.excludes {
        annotatedBy("io.mcarle.konvert.api.GeneratedKonverter")
    }
}

tasks.named<BootBuildImage>("bootBuildImage") {
    createdDate.set(Instant.now().toString())
    tags.add("api-vacunas-panama:latest")
    builder.set(
        "paketobuildpacks/builder-jammy-java-tiny@sha256:1f2bd39426f8e462f6d6177cb1504cf01211a134d51e2674f97176a8b17d8a55",
    )
}

tasks.test {
    testLogging {
        showStandardStreams = true
    }
}
