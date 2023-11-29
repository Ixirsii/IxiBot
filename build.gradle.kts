plugins {
    kotlin("jvm") version "1.9.20"

    id("org.jetbrains.dokka") version "1.9.10"

    application
    idea
    jacoco
}

group = "com.ixibot"
version = "1.0.0"

repositories {
    mavenCentral()
}

val arrowVersion: String by project
val commonsIOVersion: String by project
val discord4JVersion: String by project
val dokkaVersion: String by project
val guavaVersion: String by project
val jacksonVersion: String by project
val junitVersion: String by project
val koinVersion: String by project
val kotlinxVersion: String by project
val logbackVersion: String by project
val mockkVersion: String by project
val slf4JVersion: String by project
val sqliteVersion: String by project

dependencies {
    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")

    // Dokka HTML plugin
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:$dokkaVersion")

    // Arrow
    implementation("io.arrow-kt:arrow-core:$arrowVersion")
    // Commons IO
    implementation("commons-io:commons-io:$commonsIOVersion")
    // Google Guava
    implementation("com.google.guava:guava:$guavaVersion")
    // Discord4J
    implementation("com.discord4j:discord4j-core:$discord4JVersion")
    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:$jacksonVersion")
    // Koin
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    // SLF4J
    implementation("org.slf4j:slf4j-api:$slf4JVersion")
    // Logback
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    // SQLite3 drivers
    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")

    // JUnit testing framework
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    // Koin
    testImplementation("io.insert-koin:koin-test-junit5:$koinVersion")
    testImplementation("io.insert-koin:koin-test:$koinVersion")
    // MockK
    testImplementation("io.mockk:mockk:$mockkVersion")
}

application {
    mainClass.set("MainKt")
}

jacoco {
    toolVersion = "0.8.11"
}

val excludePaths: List<String> = listOf(
        "com/ixibot/api/**",
        "com/ixibot/data/**")

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

    useJUnitPlatform() {
        excludeTags("integration")
    }
}

val check: DefaultTask by tasks
check.dependsOn(tasks.jacocoTestCoverageVerification)

val run: JavaExec by tasks
run.standardInput = System.`in`
