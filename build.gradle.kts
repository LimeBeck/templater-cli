import java.time.Instant

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.versions)
    alias(libs.plugins.shadow)
    alias(libs.plugins.build.time.config)
    id("maven-publish")
    id("signing")
    application
}

group = "dev.limebeck.templater"
version = libs.versions.templater.cli

repositories {
    mavenCentral()
}

application {
    mainClass.set("dev.limebeck.templater.cli.TemplaterCliApplicationKt")
}

kotlin {
    jvmToolchain(17)
}

buildTimeConfig {
    config {
        destination.set(project.layout.buildDirectory.get().asFile)
        objectName.set("TemplaterCliConfig")
        packageName.set("dev.limebeck.templater.cli")
        configProperties {
            val version by string(libs.versions.templater.cli.get())
            val buildTime by string(Instant.now().toString())
        }
    }
}

dependencies {
    implementation("io.ktor:ktor-server-cio:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-status-pages:${libs.versions.ktor.get()}")
    implementation("io.ktor:ktor-server-html-builder-jvm:${libs.versions.ktor.get()}")
    implementation(libs.kxhtml.jvm)
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.coroutines)
    implementation(libs.logback)
    implementation(libs.slf4j)
    implementation(libs.clikt)
    implementation(libs.freemarker)
    implementation(libs.tika)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
