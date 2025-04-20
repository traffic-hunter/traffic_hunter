plugins {
    id("java")
}

group = "org.traffichunter.javaagent"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.jar {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
}