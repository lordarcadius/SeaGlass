package com.floydwiz.seaglass

import androidx.compose.ui.unit.dp
import com.floydwiz.seaglass.shape.SeaGlassShape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SeaGlassShapeTest {
    @Test
    fun shapesWithTheSameKeyAreEqual() {
        assertEquals(SeaGlassShape.Pane(20.dp), SeaGlassShape.Pane(20.dp))
        assertEquals(SeaGlassShape.Pane(20.dp).hashCode(), SeaGlassShape.Pane(20.dp).hashCode())
        assertEquals(SeaGlassShape.Capsule, SeaGlassShape.Capsule)
    }

    @Test
    fun shapesWithDifferentKeysDiffer() {
        assertNotEquals(SeaGlassShape.Pane(20.dp), SeaGlassShape.Pane(21.dp))
        assertNotEquals(SeaGlassShape.Lens, SeaGlassShape.Pebble)
        assertNotEquals(SeaGlassShape.Capsule, SeaGlassShape.Round)
    }
}
