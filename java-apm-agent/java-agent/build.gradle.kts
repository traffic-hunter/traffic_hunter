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
    javaagentDeps(project(":java-apm-agent:plugin:spring-auto-configure"))
    javaagentDeps(project(":java-apm-agent:plugin:http-client"))
    javaagentDeps(project(":java-apm-agent:plugin:http-url-connection"))
    javaagentDeps(project(":java-apm-agent:plugin:jdbc"))
    javaagentDeps(project(":java-apm-agent:plugin:servlet"))
    javaagentDeps(project(":java-apm-agent:plugin:logger:javaagent"))
    javaagentDeps(project(":java-apm-agent:plugin:logger:shaded-logging-module"))
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

        manifest {
            attributes(
                "Premain-Class" to "org.traffichunter.javaagent.TrafficHunterAgentMain",
                "Can-Redefine-Classes" to true,
                "Can-Retransform-Classes" to true,
                "Permissions" to "all-permissions"
            )
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        archiveFileName.set("javaagent-bootstrap.jar")
    }

    val relocateJavaagentDepsTempTask by registering(ShadowJar::class) {
        configurations = listOf(javaagentDeps)

        excludeBootstrapModuleIncludedClasses()

        mergeServiceFiles()

        relocate("net/bytebuddy", "extension/net/bytebuddy")
        relocate("META-INF/versions", "extension/META-INF/versions")
        relocate("org/yaml/snakeyaml", "extension/org/yaml/snakeyaml")
        relocate("org/slf4j", "extension/org/slf4j")
        relocate("io/github/resilience4j", "extension/io/github/resilience4j")
        relocate("com/fasterxml/jackson/", "extension/com/fasterxml/jackson/")
        relocate("org/java_websocket", "extension/org/java_websocket")

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
        from(zipTree(buildBootstrapTask.get().archiveFile))
    }

    named<ShadowJar>("shadowJar") {
        from(zipTree(relocateJavaagentDepsTask.get().archiveFile))
    }

//    named<ShadowJar>("shadowJar") {
//
//        dependsOn(relocateJavaagentDepsTask)
//
//        from(zipTree(buildBootstrapTask.get().archiveFile))
//
//        from(zipTree(relocateJavaagentDepsTask.get().archiveFile)) {
//            into("extension")
//        }
//
//        duplicatesStrategy = DuplicatesStrategy.FAIL
//
//        manifest {
//            attributes(
//                "Premain-Class" to "org.traffichunter.javaagent.TrafficHunterAgentMain",
//                "Can-Redefine-Classes" to true,
//                "Can-Retransform-Classes" to true,
//                "Permissions" to "all-permissions"
//            )
//        }
//
//        archiveFileName.set("traffic-hunter-agent.jar")
//    }
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