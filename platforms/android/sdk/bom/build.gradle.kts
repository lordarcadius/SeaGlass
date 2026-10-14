plugins {
    `java-platform`
    `maven-publish`
}

dependencies {
    constraints {
        api(project(":sdk:tokens"))
        api(project(":sdk:seaglass"))
    }
}

apply(from = rootProject.file("gradle/sdk-publishing.gradle.kts"))
