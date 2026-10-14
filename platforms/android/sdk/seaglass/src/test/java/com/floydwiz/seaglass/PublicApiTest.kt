package com.floydwiz.seaglass

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PublicApiTest {
    private val entryPoints = listOf(
        "com.floydwiz.seaglass.theme.SeaGlassThemeKt" to "SeaGlassTheme",
        "com.floydwiz.seaglass.material.GlassBackdropKt" to "GlassBackdrop",
        "com.floydwiz.seaglass.material.GlassSurfaceKt" to "GlassSurface",
        "com.floydwiz.seaglass.components.buttons.GlassButtonKt" to "GlassButton",
    )

    @Test
    fun appEntryPointsNeedNoOptIn() {
        entryPoints.forEach { (className, name) ->
            val methods = Class.forName(className).declaredMethods.filter { it.name == name || it.name.startsWith("$name-") }
            assertTrue("$name not found", methods.isNotEmpty())
            methods.forEach { method ->
                assertFalse("$name is a lab API", method.isAnnotationPresent(SeaGlassLabApi::class.java))
                method.parameterTypes.forEach { type ->
                    assertFalse("$name exposes ${type.simpleName}", type.isAnnotationPresent(SeaGlassLabApi::class.java))
                }
            }
        }
    }

    @Test
    fun labControlsAreMarked() {
        listOf(
            "com.floydwiz.seaglass.material.GlassLayers",
            "com.floydwiz.seaglass.material.GlassOverrides",
            "com.floydwiz.seaglass.material.GlassRecipeInfo",
        ).forEach { name ->
            assertTrue("$name is not marked", Class.forName(name).isAnnotationPresent(SeaGlassLabApi::class.java))
        }
    }
}
