package com.floydwiz.seaglass

import com.floydwiz.seaglass.material.blurKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class BackdropKeyTest {
    @Test
    fun nearbyValuesShareOneVariant() {
        assertEquals(blurKey(18f, 1.7f, 1.04f, false), blurKey(18.2f, 1.704f, 1.041f, false))
    }

    @Test
    fun distinctRecipesGetDistinctVariants() {
        val base = blurKey(18f, 1.7f, 1.04f, false)
        assertNotEquals(base, blurKey(18.5f, 1.7f, 1.04f, false))
        assertNotEquals(base, blurKey(18f, 1.75f, 1.04f, false))
        assertNotEquals(base, blurKey(18f, 1.7f, 1.05f, false))
        assertNotEquals(base, blurKey(18f, 1.7f, 1.04f, true))
    }

    @Test
    fun blurIsKeptToHalfADp() {
        assertEquals(18.5f, blurKey(18.4f, 1f, 1f, false).blur, 0f)
        assertEquals(18f, blurKey(18.2f, 1f, 1f, false).blur, 0f)
    }
}
