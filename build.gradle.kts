import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version ("1.5.31")

    application
    idea
    jacoco

    id("org.jetbrains.dokka") version "1.5.31"
}

group = "com.ixibot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    // Dokka HTML plugin
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.31")

    // Commons IO
    implementation("commons-io:commons-io:2.11.0")
    // Google Guava
    implementation("com.google.guava:guava:31.0.1-jre")
    // Discord4J
    implementation("com.discord4j:discord4j-core:3.2.1")
    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.13.0")
    // Logback
    implementation("ch.qos.logback:logback-classic:1.2.7")
    // SQLite3 drivers
    implementation("org.xerial:sqlite-jdbc:3.36.0.2")

    // JUnit testing framework
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")
    // MockK
    testImplementation("io.mockk:mockk:1.12.0")
}

application {
    mainClass.set("Mainkt")
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.withType<KotlinCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"

    kotlinOptions {
        javaParameters = true
        jvmTarget = "11"
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

val excludePaths: List<String> = listOf(
    "com.ixibot.api.*",
    "com.ixibot.data.*",
    "com.ixibot.event.*",
    "com.ixibot.exception.*"
)

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            excludes = excludePaths
            limit {
                minimum = 0.1.toBigDecimal()
            }
        }
    }
}

tasks.jacocoTestReport {
    description = "Generates Code coverage report."
    dependsOn(tasks.test)

    val reportExclusions = excludePaths.map {
        it.replace('.', '/')
    }

    classDirectories.setFrom(
        classDirectories.files.map {
            fileTree(it).apply {
                exclude(reportExclusions)
            }
        }
    )

    reports {
        csv.required.set(false)
        html.required.set(true)
        xml.required.set(true)
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)

    systemProperty("junit.jupiter.execution.parallel.enabled", true)
    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")

    useJUnitPlatform() {
        excludeTags("integration")
    }
}

val check: DefaultTask by tasks
check.dependsOn(tasks.jacocoTestCoverageVerification)

val run: JavaExec by tasks
run.standardInput = System.`in`
