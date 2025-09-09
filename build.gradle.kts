import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
    id("org.jlleitschuh.gradle.ktlint") version "13.0.0"
}

group = "no.fintlabs"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.fintlabs.no/releases")
    }
    mavenLocal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("no.fintlabs:fint-kafka:5.0.0-rc-8")
    implementation("no.fintlabs:fint-flyt-kafka:1.0-SNAPSHOT")
    implementation("no.fintlabs:fint-flyt-web-instance-gateway:1.0-SNAPSHOT")
    implementation("no.fintlabs:fint-flyt-web-resource-server:1.0-SNAPSHOT")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

ktlint {
    version.set("1.7.1")
    ignoreFailures.set(false)
    outputToConsole.set(true)
    filter {
        exclude("**/generated/**")
    }
}

tasks.named("check") {
    dependsOn("ktlintCheck")
}
