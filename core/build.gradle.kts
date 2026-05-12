plugins {
    `java-library`
}

dependencies {
    api(project(":api"))

    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:26.0.2")
}
