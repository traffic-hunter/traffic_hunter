plugins {
    id("java")
}

group = "org.traffichunter.javaagent.plugin"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}