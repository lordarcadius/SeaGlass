plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

subprojects {
    group = "com.floydwiz.seaglass"
    version = providers.gradleProperty("seaglass.version").get()
}

tasks.register<Copy>("collectSdkAar") {
    group = "sea glass"
    description = "Builds the release AAR of the SDK and copies it, with the version in the name, to build/sdk-aar."
    val version = providers.gradleProperty("seaglass.version")
    dependsOn(":seaglass:assembleRelease")
    from(project(":seaglass").layout.buildDirectory.file("outputs/aar/seaglass-release.aar")) {
        rename { "seaglass-${version.get()}.aar" }
    }
    into(layout.buildDirectory.dir("sdk-aar"))
}
