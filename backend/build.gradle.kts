import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val compileKotlin: KotlinCompile by tasks

plugins {
    application
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "17"
    }
}

group = "com.beanpedia"
version = "0.0.1"
application {
    mainClass.set("com.beanpedia.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:1.6.7")
    implementation("io.ktor:ktor-serialization:1.6.7")
    implementation("io.ktor:ktor-server-netty:1.6.7")
    implementation("ch.qos.logback:logback-classic:1.2.8")

    implementation("com.h2database:h2:1.4.200")
    implementation("org.jetbrains.exposed:exposed-core:0.36.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.36.2")
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("org.flywaydb:flyway-core:8.2.2")

    testImplementation("io.ktor:ktor-server-tests:1.6.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.10")
//    testImplementation("org.assertj:assertj-core:3.19.0")
//    testImplementation("io.rest-assured:rest-assured:4.4.0")
    testImplementation("io.github.serpro69:kotlin-faker:1.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}
