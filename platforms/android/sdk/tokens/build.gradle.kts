plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
}

android {
    namespace = "com.floydwiz.seaglass.tokens"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 31
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    explicitApi()
}

apply(from = rootProject.file("../../core/codegen/kotlin/seaglass-tokens.gradle.kts"))
apply(from = rootProject.file("gradle/sdk-publishing.gradle.kts"))

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.graphics)
    api(libs.androidx.compose.ui.text)
    api(libs.androidx.compose.ui.unit)
}
