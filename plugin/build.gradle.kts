plugins {
    `java-library`
    kotlin("jvm") version "1.9.10"
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
}

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor(project(":java-annotation-processor"))
    ksp(project(":ksp-annotation-processor"))
    implementation(gradleApi())
    implementation(project(":model"))
    implementation("com.google.guava:guava:32.1.1-jre")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
