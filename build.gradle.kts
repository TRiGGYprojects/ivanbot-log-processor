import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinversion = "1.9.23"
    id("org.jetbrains.kotlin.plugin.spring") version kotlinversion
    kotlin("jvm") version kotlinversion
    id("org.springframework.boot") version "3.2.5"
    kotlin("kapt") version kotlinversion
}

group = "app.eelman.ivanbot"
version = "1.0.0"

repositories {
    mavenCentral()
}
val kmongoVersion = "4.9.0"
val kotlinVersion = "kotlinVersion"
val springversion = "3.2.5"

dependencies {
    implementation("org.litote.kmongo:kmongo-jackson-mapping:${kmongoVersion}")
    implementation("org.litote.kmongo:kmongo-coroutine:${kmongoVersion}")
    kapt("org.springframework.boot:spring-boot-configuration-processor:$springversion")
    implementation("org.springframework.boot:spring-boot-starter-actuator:$springversion")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.bootJar {
    archiveFileName.set("ivanbot-log-processor.jar")
}
