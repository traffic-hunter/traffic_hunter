plugins {
    id("java")
}

group = "org.traffichunter.javaagent.plugin.hibernate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.hibernate.orm:hibernate-core:6.0.0.Final")
}

tasks.test {
    useJUnitPlatform()
}