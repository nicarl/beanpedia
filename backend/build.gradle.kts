import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val compileKotlin: KotlinCompile by tasks

plugins {
    application
    kotlin("jvm") version "1.6.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.20"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
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
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
    implementation("io.ktor:ktor-server-core:1.6.8")
    implementation("io.ktor:ktor-serialization:1.6.8")
    implementation("io.ktor:ktor-server-netty:1.6.8")
    implementation("ch.qos.logback:logback-classic:1.2.11")

    implementation("com.h2database:h2:2.1.212")
    implementation("org.jetbrains.exposed:exposed-core:0.38.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.38.1")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.flywaydb:flyway-core:8.5.8")

    testImplementation("io.ktor:ktor-server-tests:1.6.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.6.20")
//    testImplementation("org.assertj:assertj-core:3.19.0")
//    testImplementation("io.rest-assured:rest-assured:4.4.0")
    testImplementation("io.github.serpro69:kotlin-faker:1.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

detekt {
    autoCorrect = true
}

tasks.test {
    useJUnitPlatform()
}
