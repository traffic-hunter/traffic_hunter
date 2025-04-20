plugins {
    id("java")
}

group = "org.traffichunter.javaagent.extension"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation(project(":java-apm-agent:java-agent-retry"))
    implementation(project(":java-apm-agent:java-agent-bootstrap"))
    implementation(project(":java-apm-agent:java-agent-websocket"))
    implementation(project(":java-apm-agent:java-agent-event"))
    implementation(project(":java-apm-agent:java-agent-commons"))
    implementation(project(":java-apm-agent:java-agent-jmx"))

    compileOnly(project(":java-apm-agent:plugin-sdk"))

    compileOnly("io.opentelemetry:opentelemetry-api:1.45.0")
    implementation("io.opentelemetry:opentelemetry-sdk:1.45.0")
    implementation("io.opentelemetry:opentelemetry-exporter-zipkin:1.45.0")
    implementation("org.yaml:snakeyaml:2.3")
    implementation("net.bytebuddy:byte-buddy:1.15.5")
}

tasks.test {
    useJUnitPlatform()
}