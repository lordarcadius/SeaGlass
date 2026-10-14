package com.floydwiz.seaglass

/** Marks controls that explore or debug the material: layer switches, recipe overrides, the drift clock and board backdrops. */
@RequiresOptIn("This API is for labs and debugging and can change without notice.", RequiresOptIn.Level.ERROR)
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
)
public annotation class SeaGlassLabApi
