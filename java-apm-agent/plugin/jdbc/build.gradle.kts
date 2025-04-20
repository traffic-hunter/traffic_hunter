plugins {
    id("java")
}

group = "org.traffichunter.javaagent.plugin.jdbc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}