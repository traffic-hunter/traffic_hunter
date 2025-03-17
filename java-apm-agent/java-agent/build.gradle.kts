import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.6"
}

group = "org.traffichunter.javaagent"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val bootstrapDeps by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

val javaagentDeps by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    bootstrapDeps(project(":java-apm-agent:java-agent-bootstrap"))

    javaagentDeps(project(":java-apm-agent:java-agent-extension"))
    javaagentDeps(project(":java-apm-agent:plugin:jdbc"))
    javaagentDeps(project(":java-apm-agent:plugin:spring-webmvc"))
    javaagentDeps(project(":java-apm-agent:plugin:spring-business"))
}

tasks.test {
    useJUnitPlatform()
}

tasks {

    jar {
        enabled = false
    }

    val buildBootstrapTask by registering(ShadowJar::class) {
        configurations = listOf(bootstrapDeps)

        exclude("org/traffichunter/javaagent/extension/**")

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        archiveFileName.set("javaagent-bootstrap.jar")
    }

    val relocateJavaagentDepsTempTask by registering(ShadowJar::class) {
        configurations = listOf(javaagentDeps)

        excludeBootstrapModuleIncludedClasses()

        mergeServiceFiles()

        duplicatesStrategy = DuplicatesStrategy.FAIL
        archiveFileName.set("javaagent-extension-tmp.jar")
    }

    val relocateJavaagentDepsTask by registering(Jar::class) {
        dependsOn(relocateJavaagentDepsTempTask)

        from(zipTree(relocateJavaagentDepsTempTask.get().archiveFile))

        duplicatesStrategy = DuplicatesStrategy.FAIL
        archiveFileName.set("javaagent-extension.jar")
    }

    named<ShadowJar>("shadowJar") {

        dependsOn(relocateJavaagentDepsTask)

        from(zipTree(buildBootstrapTask.get().archiveFile))

        from(zipTree(relocateJavaagentDepsTask.get().archiveFile)) {
            into("extension")
        }

        duplicatesStrategy = DuplicatesStrategy.FAIL

        manifest {
            attributes(
                "Premain-Class" to "org.traffichunter.javaagent.TrafficHunterAgentMain",
                "Can-Redefine-Classes" to true,
                "Can-Retransform-Classes" to true,
                "Permissions" to "all-permissions"
            )
        }

        archiveFileName.set("traffic-hunter-agent.jar")
    }
}

fun ShadowJar.excludeBootstrapModuleIncludedClasses() {
    dependencies {
        exclude(project(":java-apm-agent:java-agent-bootstrap"))
        exclude(project(":java-apm-agent:plugin-sdk"))
        exclude(
            "io/opentelemetry/api/**",
            "io/opentelemetry/context/**",
            "io/opentelemetry/internal/**"
        )
    }
}