plugins {
    id("java")
}

group = "org.traffichunter.javaagent.plugin"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ch.qos.logback:logback-classic:1.5.0")
}

tasks.test {
    useJUnitPlatform()
}