plugins {
    kotlin("jvm") version Versions.kotlin
    application

    jacoco
    id("org.openjfx.javafxplugin") version Versions.javaFxPlugin
    id("com.github.ben-manes.versions") version Versions.versionsPlugin
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
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(Versions.java))
    }
}

javafx {
    modules("javafx.controls", "javafx.graphics")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("no.tornado:tornadofx:${Versions.tornadoFx}")

    implementation("org.igniterealtime.smack:smack-java8:${Versions.smack}")
    implementation("org.igniterealtime.smack:smack-extensions:${Versions.smack}")
    implementation("org.igniterealtime.smack:smack-tcp:${Versions.smack}")

    implementation(platform("org.apache.logging.log4j:log4j-bom:${Versions.log4j}"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl")
    implementation("org.apache.logging.log4j:log4j-jul")

    testImplementation(platform("org.junit:junit-bom:${Versions.junit}"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.platform:junit-platform-suite")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.assertj:assertj-core:${Versions.assertJ}")

    testImplementation(platform("io.cucumber:cucumber-bom:${Versions.cucumber}"))
    testImplementation("io.cucumber:cucumber-java8")
    testImplementation("io.cucumber:cucumber-junit-platform-engine")

    testImplementation(platform("org.testcontainers:testcontainers-bom:${Versions.testContainers}"))
    testImplementation("org.testcontainers:testcontainers")

    testImplementation("org.testfx:testfx-core:${Versions.testFx}")
    testImplementation("org.testfx:testfx-junit5:${Versions.testFx}")

    testImplementation("io.mockk:mockk:${Versions.mockK}")

    testImplementation("org.awaitility:awaitility-kotlin:${Versions.awaitility}")

    testImplementation("com.github.javafaker:javafaker:${Versions.javaFaker}")
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
