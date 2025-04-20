plugins {
    id("java")
}

group = "org.traffichunter.javaagent.plugin.spring-webmvc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.springframework:spring-webmvc:6.2.0")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
}

tasks.test {
    useJUnitPlatform()
}