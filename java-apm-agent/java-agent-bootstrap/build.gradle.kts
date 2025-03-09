plugins {
    id("java")
}

group = "org.traffichunter.javaagent.bootstrap"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":java-apm-agent:plugin-sdk"))
}

tasks.test {
    useJUnitPlatform()
}