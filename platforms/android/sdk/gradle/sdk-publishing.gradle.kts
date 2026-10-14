import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

val descriptions = mapOf(
    "tokens" to "Sea Glass design tokens: palettes, colour schemes, type scale, dimensions, shapes and shadows.",
    "seaglass" to "Sea Glass material, theme and components for Jetpack Compose.",
    "bom" to "Pins the Sea Glass modules to one version.",
)

extensions.configure<PublishingExtension> {
    publications {
        register<MavenPublication>("release") {
            artifactId = project.name
            afterEvaluate { from(components[if (plugins.hasPlugin("java-platform")) "javaPlatform" else "release"]) }
            pom {
                name.set("Sea Glass ${project.name}")
                description.set(descriptions.getValue(project.name))
                licenses {
                    license {
                        name.set("TODO: choose a license")
                        url.set("TODO: license URL")
                    }
                }
            }
        }
    }
}
