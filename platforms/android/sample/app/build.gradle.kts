import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val keystoreProperties = Properties().apply {
    rootProject.file("keystore.properties").takeIf { it.exists() }?.reader()?.use { load(it) }
}

fun signingCredential(property: String, environment: String): String? =
    keystoreProperties.getProperty(property) ?: System.getenv(environment)

android {
    namespace = "com.floydwiz.seaglasssample"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.floydwiz.seaglasssample"
        minSdk = 31
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("keys/ReleaseKey.jks")
            storePassword = signingCredential("storePassword", "SEAGLASS_STORE_PASSWORD")
            keyAlias = signingCredential("keyAlias", "SEAGLASS_KEY_ALIAS") ?: "ReleaseKey"
            keyPassword = signingCredential("keyPassword", "SEAGLASS_KEY_PASSWORD")
        }
    }

    val releaseSigning = signingConfigs.getByName("release").takeIf { it.storePassword != null && it.keyPassword != null }
    if (releaseSigning == null) logger.warn("Release signing credentials not found; add keystore.properties (see keystore.properties.example). Release builds will be unsigned.")

    buildTypes {
        release {
            optimization {
                enable = true
            }
            signingConfig = releaseSigning
        }
        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = releaseSigning
            matchingFallbacks += "release"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        optIn.add("com.floydwiz.seaglass.SeaGlassLabApi")
    }
}

val seaGlassVersion = Properties().apply {
    rootProject.file("../sdk/gradle.properties").reader().use { load(it) }
}.getProperty("seaglass.version")
val seaGlassFromAar = providers.gradleProperty("seaglass.fromAar").orNull == "true"

if (seaGlassFromAar) {
    tasks.matching { it.name == "preBuild" }.configureEach {
        dependsOn(gradle.includedBuild("sdk").task(":collectSdkAar"))
    }
}

dependencies {
    if (seaGlassFromAar) {
        implementation(files("../../sdk/build/sdk-aar/seaglass-$seaGlassVersion.aar"))
        implementation(libs.androidx.graphics.shapes)
    } else {
        implementation("com.floydwiz.seaglass:seaglass:$seaGlassVersion")
    }
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.metrics.performance)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}