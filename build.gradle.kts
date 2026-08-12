plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.shadow)
    application
}

group = "com.helltar"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.websockets)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.dotenv.kotlin)
    implementation(libs.kotlin.logging.jvm)
    runtimeOnly(libs.logback.classic)
}

application {
    mainClass.set("com.helltar.signai.MainKt")
}

kotlin {
    jvmToolchain(21)
}
