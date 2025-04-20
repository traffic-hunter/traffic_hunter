plugins {
    id("java")
}

group = "org.traffichunter.javaagent.plugin"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":java-apm-agent:plugin:logger:shaded-logging-module"))
}

tasks.test {
    useJUnitPlatform()
}