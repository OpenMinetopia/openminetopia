plugins {
    id("io.freefair.lombok") version "9.5.0" apply false
    id("com.gradleup.shadow") version "9.4.1" apply false
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
    id("xyz.jpenilla.run-paper") version "3.0.2" apply false
}

allprojects {
    group = "nl.openminetopia"
    version = "2.0.0-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.freefair.lombok")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.aikar.co/content/groups/aikar/")
        maven("https://jitpack.io")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://repo.skriptlang.org/releases")
        maven("https://maven.citizensnpcs.co/repo")
        maven("https://repo.fancyplugins.de/releases")
        maven("https://dist.labymod.net/api/v1/maven/release/")
        maven("https://repo.triumphteam.dev/snapshots/")
        maven("https://repo.codemc.io/repository/maven-public/")
    }

    val targetJavaVersion = 25
    extensions.configure<JavaPluginExtension> {
        val javaVersion = JavaVersion.toVersion(targetJavaVersion)
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
        if (JavaVersion.current() < javaVersion) {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
            }
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
}
