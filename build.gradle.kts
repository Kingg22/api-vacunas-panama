import com.diffplug.spotless.LineEnding
import com.github.jk1.license.filter.LicenseBundleNormalizer
import com.github.jk1.license.render.InventoryMarkdownReportRenderer
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.allopen") version libs.versions.kotlin
    kotlin("plugin.jpa") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin

    alias(libs.plugins.ksp)
    alias(libs.plugins.spotless)
    alias(libs.plugins.license.report)
    alias(libs.plugins.kover)

    alias(libs.plugins.quarkus)
}

group = "io.github.kingg22"
version = "0.15.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
        vendor.set(JvmVendorSpec.GRAAL_VM)
    }
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
        javaParameters.set(true)
    }
}

dependencies {
    ksp(libs.konvert)

    implementation(enforcedPlatform(libs.quarkus.bom))
    implementation(libs.bundles.quarkusImplementation)
    testImplementation(libs.bundles.quarkusTestImplementation)
}

allOpen {
    annotation("io.quarkus.test.junit.QuarkusTest")
    annotation("jakarta.ws.rs.Path")
    annotation("jakarta.enterprise.context.ApplicationScoped")
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    val agentJar = configurations.testRuntimeClasspath.get().find {
        it.name.contains("byte-buddy-agent")
    } ?: throw GradleException("ByteBuddy agent JAR not found")

    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    jvmArgs("-javaagent:${agentJar.absolutePath}", "-Duser.timezone=UTC")
    testLogging {
        showStandardStreams = true
    }
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
                // Temporally reduce to 20 because have much ignored test
                minBound(20, CoverageUnit.LINE)
            }
            rule("Basic Branch Coverage") {
                // Temporally reduce to 15 because have much ignored test
                minBound(15, CoverageUnit.BRANCH)
            }
        }
        filters.excludes {
            annotatedBy("io.mcarle.konvert.api.GeneratedKonverter")
        }
    }
}

/* Circular dependency between the following tasks:
:kspKotlin
+--- :quarkusGenerateCode
|    \--- :processResources
|         \--- :kspKotlin (*)
 */

project.afterEvaluate {
    getTasksByName("quarkusGenerateCode", true).forEach { task ->
        task.setDependsOn(
            task.dependsOn.filterIsInstance<Provider<Task>>().filter { it.get().name != "processResources" },
        )
    }
    getTasksByName("quarkusGenerateCodeDev", true).forEach { task ->
        task.setDependsOn(
            task.dependsOn.filterIsInstance<Provider<Task>>().filter { it.get().name != "processResources" },
        )
    }
}
