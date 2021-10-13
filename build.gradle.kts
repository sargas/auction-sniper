plugins {
    kotlin("jvm") version "1.5.31"
    java

    application

    jacoco

    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "net.neoturbine"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("net.neoturbine.auction.sniper.AuctionSniperApplicationKt")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(16)) // "8"
    }
}

javafx {
    modules("javafx.controls", "javafx.graphics")
}

dependencies {
    implementation(kotlin("stdlib", "1.5.31"))

    implementation("no.tornado:tornadofx:1.7.20")


    implementation("org.igniterealtime.smack:smack-java8:4.4.3")
    implementation("org.igniterealtime.smack:smack-extensions:4.4.3")
    implementation("org.igniterealtime.smack:smack-tcp:4.4.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.junit.platform:junit-platform-suite:1.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")

    testImplementation("org.assertj:assertj-core:3.21.0")

    testImplementation("io.cucumber:cucumber-java8:6.11.0")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:6.11.0")

    testImplementation("org.testcontainers:testcontainers:1.16.0")
    testImplementation("org.testfx:testfx-core:4.0.16-alpha")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}
