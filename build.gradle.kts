import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import java.io.FileInputStream
import java.util.*

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.jcraft:jsch:0.1.55")
    }
}

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
val author = "isnow"

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
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

    compileOnly("org.hibernate.orm:hibernate-core:6.2.8.Final")
    compileOnly("jakarta.validation:jakarta.validation-api:3.0.2")
    compileOnly("jakarta.persistence:jakarta.persistence-api:3.1.0")
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.2.0")
    compileOnly("com.h2database:h2:2.2.220")

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

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

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks {
    processResources {
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "author" to author
        )
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveFileName.set("${project.name}.jar")
        transform(Log4j2PluginsCacheFileTransformer::class.java)
    }

    build {
        dependsOn(shadowJar)
    }

    register("deploy") {
        group = "Deployment"
        description = "Deploys the plugin JAR to the custom server via SCP."

        onlyIf {
            project.hasProperty("UPLOAD.USERNAME") &&
            project.hasProperty("UPLOAD.IP") &&
            project.hasProperty("UPLOAD.PASSWORD") &&
            project.hasProperty("UPLOAD.PATH")
        }

        doFirst {
            val user = project.property("UPLOAD.USERNAME") as String
            val host = project.property("UPLOAD.IP") as String
            val password = project.property("UPLOAD.PASSWORD") as String
            val remotePath = project.property("UPLOAD.PATH") as String

            val shadowJarTask = project.tasks.named("shadowJar").get()
            val localFile = shadowJarTask.outputs.files.singleFile

            val jsch = JSch()
            val session: Session = jsch.getSession(user, host, 22)
            session.setPassword(password)

            val config = Properties()
            config["StrictHostKeyChecking"] = "no"
            session.setConfig(config)

            session.connect(30000)

            val command = "scp -t $remotePath"

            val channel = session.openChannel("exec") as ChannelExec
            channel.setCommand(command)

            val out = channel.outputStream
            val input = channel.inputStream

            channel.connect()

            if (checkAck(input) != 0) {
                throw RuntimeException("SCP failed during init")
            }

            val file = localFile
            val filesize = file.length()
            val fileName = file.name

            val commandLine = "C0644 $filesize $fileName\n"
            out.write(commandLine.toByteArray())
            out.flush()

            if (checkAck(input) != 0) {
                throw RuntimeException("SCP failed during file info")
            }

            val fis = FileInputStream(file)
            val buffer = ByteArray(1024)
            while (true) {
                val len = fis.read(buffer)
                if (len <= 0) break
                out.write(buffer, 0, len)
            }
            fis.close()

            // Send zero byte to indicate EOF
            out.write(0)
            out.flush()

            if (checkAck(input) != 0) {
                throw RuntimeException("SCP failed during file transfer")
            }

            out.close()
            channel.disconnect()
            session.disconnect()

            println("Deployed ${file.name} to $user@$host:$remotePath")
        }
    }
}

fun checkAck(input: java.io.InputStream): Int {
    val b = input.read()
    when (b) {
        0 -> return 0
        -1 -> return -1
        1, 2 -> {
            val sb = StringBuilder()
            var c = input.read()
            while (c != '\n'.code && c != -1) {
                sb.append(c.toChar())
                c = input.read()
            }
            if (b == 1) { // error
                System.err.println(sb.toString())
            }
            if (b == 2) { // fatal error
                System.err.println(sb.toString())
            }
            return b
        }
        else -> return b
    }
}
