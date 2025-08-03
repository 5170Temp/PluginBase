import org.apache.tools.ant.filters.ReplaceTokens
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.Properties

val envFile = rootProject.file(".env")
if (envFile.exists()) {
    val props = Properties()
    envFile.inputStream().use { props.load(it) }

    props.forEach { key, value ->
        if (!project.hasProperty(key.toString())) {
            project.extensions.extraProperties[key.toString()] = value
        }
    }
}

plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.isnow"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

    compileOnly("com.google.common:google-collect:0.5")

    compileOnly("org.hibernate.orm:hibernate-core:6.2.8.Final")
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.1.0")
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.2.0")
    compileOnly("com.h2database:h2:2.2.220")

    implementation("com.github.Mqzn:Lotus:1.6.0")

    compileOnly("dev.velix:imperat-core:1.9.7")
    compileOnly("dev.velix:imperat-bukkit:1.9.7")

    compileOnly("de.exlll:configlib-paper:4.6.1")

    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8")
    compileOnly("org.hibernate.orm:hibernate-jcache:6.2.8.Final")

    compileOnly("com.github.retrooper:packetevents-spigot:2.9.3")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks {
    processResources {
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "author" to project.name
        )
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveFileName.set("${project.name}.jar")
    }

    build {
        dependsOn(shadowJar)
    }

    register<Exec>("deploy") {
        dependsOn(shadowJar)
        group = "Deployment"
        description = "Deploys the plugin JAR to the custom server via SCP."

        onlyIf { project.hasProperty("UPLOAD.USERNAME") && project.hasProperty("UPLOAD.IP") }

        doFirst {
            val scpUser = project.property("UPLOAD.USERNAME") as String
            val buildIp = project.property("UPLOAD.IP") as String
            val remotePath = project.property("UPLOAD.PATH") as String

            val shadowJarTask = project.tasks.named("shadowJar", ShadowJar::class.java).get()
            val jarFile = shadowJarTask.archiveFile.get().asFile

            logger.lifecycle("Deploying ${jarFile.name} to $scpUser@$buildIp:$remotePath")

            commandLine("scp", jarFile.absolutePath, "$scpUser@$buildIp:$remotePath")
        }
    }
}