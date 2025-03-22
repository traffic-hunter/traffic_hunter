plugins {
    id("java")
}

group = "org.traffichunter.javaagent.plugin.plugin-sdk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("io.opentelemetry:opentelemetry-api:1.45.0")
    implementation("io.opentelemetry:opentelemetry-api-incubator:1.45.0-alpha")
}

tasks.test {
    useJUnitPlatform()
}