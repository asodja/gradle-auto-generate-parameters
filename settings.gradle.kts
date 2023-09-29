plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

rootProject.name = "com.github.asodja.task.api"
include(
    "java-annotation-processor",
    "ksp-annotation-processor",
    "model",
    "plugin"
)
