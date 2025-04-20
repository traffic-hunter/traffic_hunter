plugins {
    id("java")
}

group = "org.traffichunter.javaagent.websocket"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":java-apm-agent:java-agent-retry"))
    implementation(project(":java-apm-agent:java-agent-commons"))

    implementation("org.java-websocket:Java-WebSocket:1.5.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.18.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.0")
}

tasks.test {
    useJUnitPlatform()
}