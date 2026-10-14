plugins {
    `java-platform`
    `maven-publish`
}

dependencies {
    constraints {
        api(project(":tokens"))
        api(project(":seaglass"))
    }
}

apply(from = rootProject.file("gradle/sdk-publishing.gradle.kts"))
