package io.github.kingg22.api.vacunas.panama.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withImport
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import com.lemonappdev.konsist.api.verify.assertTrue
import io.quarkus.runtime.annotations.RegisterForReflection
import kotlin.test.Test

class KonsistVerifyTest {

    @Test
    fun `In project no import javax`() {
        Konsist
            .scopeFromProject()
            .files
            .withImport { it.name.contains("javax..") }
    }

    @Test
    fun `In project no import panache without kotlin`() {
        Konsist
            .scopeFromProject()
            .files
            .withImport { it.name.contains("io.quarkus.hibernate.reactive.panache..") }
            .assertTrue { d -> d.hasImport { it.name.contains("kotlin") } }
    }

    @Test
    fun `Dto need be register for reflection annotation`() {
        Konsist
            .scopeFromProject()
            .classes(true)
            .withNameEndingWith("Dto")
            .assertTrue { it.hasAnnotationOf(RegisterForReflection::class) }
    }

    @Test
    fun `If use jackson need be register for reflection annotation`() {
        Konsist
            .scopeFromProject()
            .files
            .withImport { it.name.contains("com.fasterxml.jackson..") }
            .assertTrue { it.hasAnnotationOf(RegisterForReflection::class) }
    }

    @Test
    fun `Extension function must be in the same package as the thing it extends`() {
        Konsist
            .scopeFromProject()
            .files
            .withNameEndingWith("Ext")
            .filter { fileDeclaration -> fileDeclaration.packagee?.name?.contains("util") == false }
            .assertTrue {
                val baseName = it.name.removeSuffix("Ext")
                val extPackage = it.packagee?.name
                if (extPackage == null) return@assertTrue null

                val matchingBaseFile = Konsist
                    .scopeFromProject()
                    .classesAndInterfacesAndObjects(true)
                    .firstOrNull { clazz ->
                        clazz.name == baseName && clazz.packagee?.name == extPackage
                    }

                matchingBaseFile != null
            }
    }
}
