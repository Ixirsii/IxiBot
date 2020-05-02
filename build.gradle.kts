import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("idea")
    id("jacoco")
    id("org.jetbrains.dokka") version "0.10.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("org.jmailen.kotlinter") version "2.3.2"

    application
    kotlin("jvm") version("1.3.72")
}

group = "com.ixibot"

version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("org.jlleitschuh.gradle:ktlint-gradle:9.2.1")
    implementation(kotlin("stdlib"))
    implementation("commons-io:commons-io:2.6")
    implementation("com.google.guava:guava:28.0-jre")
    implementation("com.discord4j:discord4j-core:3.0.7")
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.10.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.10.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.12.0")
    implementation("org.xerial:sqlite-jdbc:3.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.0")
    testImplementation("org.mockito:mockito-core:3.1.0")
    testImplementation("org.mockito:mockito-junit-jupiter:3.1.0")
}

application {
    mainClassName = "com.ixibot.Mainkt"
}

jacoco {
    toolVersion = "0.8.4"
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

tasks.compileKotlin {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    kotlinOptions {
        javaParameters = true
        jvmTarget = "11"
    }
}