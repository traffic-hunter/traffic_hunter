plugins {
    id("java")
}

group = "org.traffichunter.javaagent.plugin.spring.business"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}