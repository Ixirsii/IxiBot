plugins {
    id("org.jetbrains.dokka") version "0.10.0"

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

    // JUnit testing framework
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.0")
    // MockK
    testImplementation("io.mockk:mockk:1.9.3")
}

application {
    mainClassName = "com.ixibot.Mainkt"
}

jacoco {
    toolVersion = "0.8.4"
}

tasks.compileKotlin {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"

    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
    }
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

tasks.jacocoTestReport {
    sourceDirectories.setFrom(files("${project.projectDir}/src/main"))
    classDirectories.setFrom(
            fileTree("${buildDir}/classes/kotlin/main") {
                setExcludes(setOf("com/ixibot/api/**", "com/ixibot/module/**"))
            }
    )
    reports {
        csv.isEnabled = false
        xml.isEnabled = true
        xml.destination = file("${buildDir}/reports/jacoco/jacocoTestReport.xml")
        html.destination = file("${buildDir}/reports/jacoco")
    }
}

tasks.jacocoTestCoverageVerification {
    sourceDirectories.setFrom(files("${project.projectDir}/src/main"))
    classDirectories.setFrom(
            fileTree("${buildDir}/classes/kotlin/main") {
                setExcludes(setOf("com/ixibot/api/**", "com/ixibot/module/**"))
            }
    )
    violationRules {
        rule {
            limit {
                minimum = 0.5.toBigDecimal()
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

val check: DefaultTask by tasks
check.dependsOn(tasks.jacocoTestCoverageVerification)

val run: JavaExec by tasks
run.standardInput = System.`in`

val test by tasks
test.finalizedBy(project.tasks.jacocoTestReport)
