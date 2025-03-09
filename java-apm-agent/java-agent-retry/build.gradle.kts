plugins {
    id("java")
}

group = "org.traffichunter.javaagent.retry"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("io.github.resilience4j:resilience4j-retry:2.2.0")
}

tasks.test {
    useJUnitPlatform()
}