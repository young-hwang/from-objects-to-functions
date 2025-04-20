plugins {
    kotlin("jvm")
}

group = "me.zettai"
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

    // https://mvnrepository.com/artifact/org.http4k/http4k-client-jetty
    testImplementation("org.http4k:http4k-client-jetty:5.47.0.0")

    testImplementation("io.strikt:strikt-core:0.34.1")

    testImplementation(kotlin("test"))

    testImplementation("com.ubertob.pesticide:pesticide-core:1.6.6")

    testImplementation("org.jsoup:jsoup:1.12.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
