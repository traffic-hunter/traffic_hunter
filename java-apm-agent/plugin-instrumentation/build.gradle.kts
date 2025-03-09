plugins {
    id("java")
}

group = "org.traffichunter.plugin.instrumentation"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("net.bytebuddy:byte-buddy:1.15.5")
}

tasks.test {
    useJUnitPlatform()
}