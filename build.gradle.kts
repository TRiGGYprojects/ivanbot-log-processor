import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinversion = "1.7.10"
    id("org.jetbrains.kotlin.plugin.spring") version kotlinversion
    kotlin("jvm") version "1.7.10"
    id("org.springframework.boot") version "2.6.4"
    kotlin("kapt") version kotlinversion
}

group = "app.eelman.ivanbot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
val kmongoVersion = "4.7.0"
val kotlinVersion = "kotlinVersion"
val springversion = "2.7.3"

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
    kotlinOptions.jvmTarget = "1.8"
}

tasks.bootJar {
    archiveFileName.set("ivanbot-log-processor.jar")
}
