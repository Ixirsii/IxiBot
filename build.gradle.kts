plugins {
    id("java")
    id("application")
    id("checkstyle")
    id("jacoco")

    alias(libs.plugins.axion)
    alias(libs.plugins.lombok)
}

group = "tech.ixirsii"
version = scmVersion.version

repositories {
    mavenCentral()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    implementation(libs.discord4j.core)
    implementation(libs.guava)
    implementation(libs.guice)
    implementation(libs.bundles.jackson)
    implementation(libs.logback.classic)
    implementation(platform(libs.reactor.bom))
    implementation(libs.reactor.core)
    implementation(libs.slf4j.api)

    mockitoAgent(libs.mockito.core) { isTransitive = false }

    testImplementation(libs.bundles.mockito)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)

    testRuntimeOnly(libs.junit.platform.launcher)
}

checkstyle {
    toolVersion = "10.23.0"
}

jacoco {
    toolVersion = "0.8.13"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}

tasks.checkstyleTest {
    configFile = file("${rootDir}/config/checkstyle/checkstyle-test.xml")
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        html.required = true
        xml.required = false
    }
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        val coverageExclusions = listOf(
            "tech.ixirsii.clash.internal"
        )

        rule {
            excludes = coverageExclusions
            limit {
                counter = "CLASS"
                element = "CLASS"
                minimum = 1.0.toBigDecimal()
            }
        }
        rule {
            excludes = coverageExclusions
            limit {
                counter = "METHOD"
                element = "CLASS"
                minimum = 1.0.toBigDecimal()
            }
        }
        rule {
            excludes = coverageExclusions
            limit {
                counter = "LINE"
                element = "CLASS"
                minimum = 0.70.toBigDecimal()
            }
        }
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        csv.required = false
        html.required = true
        xml.required = true
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
    useJUnitPlatform()
}
