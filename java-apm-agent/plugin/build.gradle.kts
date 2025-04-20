plugins {
    id("java")
}

group = "org.traffichunter.javaagent.plugin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.10.0"))
        testImplementation("org.junit.jupiter:junit-jupiter")

        implementation(project(":java-apm-agent:java-agent-extension"))
        compileOnly(project(":java-apm-agent:plugin-sdk"))

        compileOnly("net.bytebuddy:byte-buddy:1.15.5")
        compileOnly("io.opentelemetry:opentelemetry-api:1.45.0")
    }
}

tasks.test {
    useJUnitPlatform()
}