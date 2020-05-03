import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.dokka") version "0.10.0"
    id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
    id("org.jmailen.kotlinter") version "2.3.2"

    application
    idea
    jacoco
    kotlin("jvm") version("1.3.72")
}

group = "com.ixibot"

version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    // Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))
    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")

    // ktlint for program style
    implementation("org.jlleitschuh.gradle:ktlint-gradle:9.2.1")
    // Commons IO
    implementation("commons-io:commons-io:2.6")
    // Google Guava
    implementation("com.google.guava:guava:29.0-jre")
    // Discord4J
    implementation("com.discord4j:discord4j-core:3.0.7")
    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-core:2.10.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.10.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.10.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.1")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.10.2")
    // Log4J-SLF4J
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.12.0")
    // SQLite3 drivers
    implementation("org.xerial:sqlite-jdbc:3.7.2")

    // Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    // Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    // JUnit testing framework
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.0")
    // Mockito
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
    sourceCompatibility = "11"
    targetCompatibility = "11"

    kotlinOptions {
        javaParameters = true
        jvmTarget = "11"
    }
}

val run: JavaExec by tasks
run.standardInput = System.`in`