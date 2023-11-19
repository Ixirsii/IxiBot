import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.dokka") version "1.9.10"

    application
    idea
    jacoco
    kotlin("jvm") version ("1.9.20")
}

group = "com.ixibot"

version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Dokka HTML plugin
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.31")

    // Commons IO
    implementation("commons-io:commons-io:2.15.0")
    // Google Guava
    implementation("com.google.guava:guava:32.1.3-jre")
    // Discord4J
    implementation("com.discord4j:discord4j-core:3.2.6")
    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.3")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.15.3")
    // SLF4J
    implementation("org.slf4j:slf4j-api:2.0.9")
    // Logback
    implementation("ch.qos.logback:logback-classic:1.4.0")
    // SQLite3 drivers
    implementation("org.xerial:sqlite-jdbc:3.44.0.0")

    // JUnit testing framework
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.1")
    // MockK
    testImplementation("io.mockk:mockk:1.13.8")
}

application {
    mainClass.set("MainKt")
}

jacoco {
    toolVersion = "0.8.11"
}

val excludePaths: Set<String> = setOf(
        "com/ixibot/api/**",
        "com/ixibot/**/*Discord*")

tasks.jacocoTestReport {
    classDirectories.setFrom(
            classDirectories.files.map {
                fileTree(it).apply {
                    exclude(excludePaths)
                }
            }
    )
    reports {
        csv.required = false
        xml.required = true
        xml.outputLocation = file("${buildDir}/reports/jacoco/report.xml")
        html.outputLocation = file("${buildDir}/reports/jacoco")
    }
}

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
