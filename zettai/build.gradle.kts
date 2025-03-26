plugins {
    kotlin("jvm")
}

group = "me"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.http4k/http4k-core
    implementation("org.http4k:http4k-core:5.47.0.0")

    // https://mvnrepository.com/artifact/org.http4k/http4k-server-jetty
    implementation("org.http4k:http4k-server-jetty:5.47.0.0")

    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.15")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
