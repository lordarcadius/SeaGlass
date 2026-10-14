plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

subprojects {
    group = "com.floydwiz.seaglass"
    version = providers.gradleProperty("seaglass.version").get()
}

tasks.register<Copy>("collectSdkAars") {
    group = "sea glass"
    description = "Builds the release AARs of the SDK modules and copies them, with the version in the name, to build/sdk-aar."
    val version = providers.gradleProperty("seaglass.version")
    dependsOn(":tokens:assembleRelease", ":seaglass:assembleRelease")
    for (module in listOf("tokens", "seaglass")) {
        from(project(":$module").layout.buildDirectory.file("outputs/aar/$module-release.aar")) {
            rename { "$module-${version.get()}.aar" }
        }
    }
    into(layout.buildDirectory.dir("sdk-aar"))
}
