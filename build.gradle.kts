plugins {
	kotlin("jvm") version libs.versions.kotlin.get()
	kotlin("plugin.spring") version libs.versions.kotlin.get()
	kotlin("plugin.jpa") version libs.versions.kotlin.get()
    java
	alias(libs.plugins.spring.boot)
	alias(libs.plugins.spring.dependency.management)
}

group = "io.github.kingg22"
version = "0.2.0"

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
	compileOnly(libs.lombok)
	developmentOnly(libs.spring.boot.devtools)
	runtimeOnly(libs.bundles.runtimeOnly)
	annotationProcessor(libs.lombok)
	annotationProcessor(libs.mapstruct.processor)
	testImplementation(libs.bundles.testImplementation)
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
