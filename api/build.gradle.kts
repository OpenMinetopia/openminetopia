plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:26.0.2")
}

publishing {
    publications {
        create<MavenPublication>("openminetopiaApi") {
            groupId = "nl.openminetopia"
            artifactId = "openminetopia-api"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
