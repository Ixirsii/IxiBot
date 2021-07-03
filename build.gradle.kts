plugins {
    id("org.jetbrains.dokka") version "1.4.32"

    application
    idea
    jacoco
    kotlin("jvm") version ("1.4.30")
}

group = "com.ixibot"

version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    // Dokka HTML plugin
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.32")

    // Kotlin coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    // Commons IO
    implementation("commons-io:commons-io:2.6")
    // Google Guava
    implementation("com.google.guava:guava:30.1.1-jre")
    // Discord4J
    implementation("com.discord4j:discord4j-core:3.1.6")
    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-core:2.12.+")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.+")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.+")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.+")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.12.+")
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
    toolVersion = "0.8.5"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        useIR = true
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

tasks.compileKotlin {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"

    kotlinOptions {
        javaParameters = true
        jvmTarget = "1.8"
    }
}

val excludePaths: Set<String> = setOf(
    "com/ixibot/api/**",
    "com/ixibot/data/**",
    "com/ixibot/event/**",
    "com/ixibot/exception/**"
)

tasks.jacocoTestReport {
    classDirectories.setFrom(
        classDirectories.files.map {
            fileTree(it).apply {
                exclude(excludePaths)
            }
        }
    )
    reports {
        csv.isEnabled = false
        xml.isEnabled = true
        xml.destination = file("${buildDir}/reports/jacoco/report.xml")
        html.destination = file("${buildDir}/reports/jacoco")
    }
}

tasks.jacocoTestCoverageVerification {
    classDirectories.setFrom(
        classDirectories.files.map {
            fileTree(it).apply {
                exclude(excludePaths)
            }
        }
    )
    violationRules {
        rule {
            limit {
                minimum = 0.1.toBigDecimal()
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

val check: DefaultTask by tasks
check.dependsOn(tasks.jacocoTestCoverageVerification)

val run: JavaExec by tasks
run.standardInput = System.`in`
