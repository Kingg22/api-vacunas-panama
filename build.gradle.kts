import com.diffplug.spotless.LineEnding
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.InventoryMarkdownReportRenderer
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import java.time.Instant

plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.spring") version libs.versions.kotlin
    kotlin("plugin.jpa") version libs.versions.kotlin
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.ksp)
    alias(libs.plugins.spotless)
    alias(libs.plugins.license.report)
    alias(libs.plugins.kover)
}

group = "io.github.kingg22"
version = "0.14.6"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.JETBRAINS)
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        javaParameters.set(true)
    }
}

dependencies {
    implementation(libs.bundles.implementation)

    runtimeOnly(libs.bundles.runtimeOnly)

    ksp(libs.konvert)

    testImplementation(libs.bundles.testImplementation)
    testImplementation(libs.spring.boot.starter.test) {
        exclude(group = "org.mockito")
    }

    testRuntimeOnly(libs.junit.platform.launcher)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    val agentJar = configurations.testRuntimeClasspath.get().find {
        it.name.contains("byte-buddy-agent")
    } ?: throw GradleException("ByteBuddy agent JAR not found")

    jvmArgs("-javaagent:${agentJar.absolutePath}", "-Duser.timezone=UTC")
}

spotless {
    encoding("UTF-8")
    lineEndings = LineEnding.PRESERVE
    kotlin {
        target("src/*/kotlin/**/*.kt")
        ktlint()
    }
    kotlinGradle {
        targetExclude("build/generated/**")
        ktlint()
    }
    sql {
        targetExclude("build/generated/**")
        target("src/**/*.sql", "containers/database/**/*.sql")
    }
    yaml {
        targetExclude("build/generated/**")
        target("src/**/*.yaml", "*.yaml")
    }
    json {
        targetExclude("build/generated/**")
        target("src/**/*.json")
    }
}

licenseReport {
    renderers = arrayOf(InventoryMarkdownReportRenderer("THIRD-PARTY.md"))
    filters = arrayOf(LicenseBundleNormalizer())
}

kover {
    reports {
        total.verify {
            rule("Basic Line Coverage") {
                minBound(60, CoverageUnit.LINE)
            }
            rule("Basic Branch Coverage") {
                minBound(20, CoverageUnit.BRANCH)
            }
        }
        filters.excludes {
            annotatedBy("io.mcarle.konvert.api.GeneratedKonverter")
        }
    }
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("kingg22/api-vacunas-panama:${project.version}")
    createdDate.set(Instant.now().toString())
    tags.add("kingg22/api-vacunas-panama:latest")
    builder.set(
        "paketobuildpacks/builder-jammy-java-tiny@sha256:1f2bd39426f8e462f6d6177cb1504cf01211a134d51e2674f97176a8b17d8a55",
    )
}

tasks.test {
    testLogging {
        showStandardStreams = true
    }
}
