plugins {
    kotlin("jvm") version "1.6.0"
    id("com.bnorm.power.kotlin-power-assert") version "0.11.0"
}

repositories {
    mavenCentral()
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    implementation("org.apache.commons:commons-compress:1.21") //used on day 16
}