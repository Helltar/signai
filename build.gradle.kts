plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
    id("com.gradleup.shadow") version "9.3.0"
    application
}

group = "com.helltar"
version = "0.8.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.4.1")
    implementation("io.ktor:ktor-client-cio:3.4.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.01")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.32")
}

application {
    mainClass.set("com.helltar.signai.MainKt")
}

kotlin {
    jvmToolchain(21)
}
