plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.0"
    application
}

group = "com.helltar"
version = "0.7.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.4.0")
    implementation("io.ktor:ktor-client-cio:3.4.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.27")
}

application {
    mainClass.set("com.helltar.signai.MainKt")
}

kotlin {
    jvmToolchain(21)
}
