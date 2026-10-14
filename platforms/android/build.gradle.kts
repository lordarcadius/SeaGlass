// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

subprojects {
    if (path.startsWith(":sdk:")) {
        group = "com.floydwiz.seaglass"
        version = providers.gradleProperty("seaglass.version").get()
    }
}

tasks.register<Copy>("collectSdkAars") {
    group = "sea glass"
    description = "Builds the release AARs of the SDK modules and copies them, with the version in the name, to build/sdk-aar."
    val version = providers.gradleProperty("seaglass.version")
    dependsOn(":sdk:tokens:assembleRelease", ":sdk:seaglass:assembleRelease")
    for (module in listOf("tokens", "seaglass")) {
        from(project(":sdk:$module").layout.buildDirectory.file("outputs/aar/$module-release.aar")) {
            rename { "$module-${version.get()}.aar" }
        }
    }
    into(layout.buildDirectory.dir("sdk-aar"))
}

