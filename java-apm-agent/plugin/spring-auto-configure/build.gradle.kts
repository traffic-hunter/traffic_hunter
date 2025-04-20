plugins {
    id("java")
}

group = "org.traffichunter.javaagent.plugin.spring"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val springBootVersion = "3.2.0"

dependencies {
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor:$springBootVersion")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:$springBootVersion")

    compileOnly("org.springframework:spring-webmvc:6.2.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
    testImplementation("org.springframework:spring-webmvc:6.2.0")
}

tasks.test {
    useJUnitPlatform()
}