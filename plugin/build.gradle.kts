plugins {
    java
    id("com.gradleup.shadow")
    id("io.papermc.paperweight.userdev")
    id("xyz.jpenilla.run-paper")
}

dependencies {
    /* Paper */
    paperweight.paperDevBundle("26.1.2.build.+")

    /* Configuration */
    compileOnly("org.spongepowered:configurate-yaml:4.2.0")
    compileOnly("org.spongepowered:configurate-core:4.2.0")

    /* Database */
    compileOnly("com.zaxxer:HikariCP:7.0.2")
    compileOnly("mysql:mysql-connector-java:8.0.33")
    compileOnly("org.mariadb.jdbc:mariadb-java-client:3.5.3")
    compileOnly("org.xerial:sqlite-jdbc:3.49.1.0")
    implementation("com.github.Mindgamesnl:storm:e1f961b480") {
        exclude(group = "org.projectlombok", module = "lombok-maven")
    }

    /* Command Framework */
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

    /* Scoreboard */
    val scoreboardLibraryVersion = "2.6.0"
    implementation("net.megavex:scoreboard-library-api:$scoreboardLibraryVersion")
    runtimeOnly("net.megavex:scoreboard-library-implementation:$scoreboardLibraryVersion")
    runtimeOnly("net.megavex:scoreboard-library-modern:$scoreboardLibraryVersion:mojmap")

    /* PlaceholderAPI */
    compileOnly("me.clip:placeholderapi:2.11.6")

    /* WorldGuard */
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.15") {
        exclude("com.google.guava", "guava")
        exclude("com.google.code.gson", "gson")
        exclude("it.unimi.dsi", "fastutil")
    }

    /* Module Manager */
    implementation("com.github.duranaaron.ModuleManager:spigot:287350ddac")

    /* PersistentData addons */
    implementation("com.jeff-media:custom-block-data:2.2.5")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")

    /* TriumphGUI */
    implementation("dev.triumphteam:triumph-gui-paper:3.1.13-SNAPSHOT")

    /* bStats */
    implementation("org.bstats:bstats-bukkit:3.1.0")

    /* Vault */
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1") {
        exclude(group = "org.bukkit", module = "bukkit")
    }

    /* Skript */
    compileOnly("com.github.SkriptLang:Skript:2.10.2")

    /* Rest API & Portal dependencies */
    val vertxVersion = "5.0.5"
    compileOnly("io.vertx:vertx-core:$vertxVersion")
    compileOnly("io.vertx:vertx-web:$vertxVersion")
    compileOnly("io.vertx:vertx-web-client:$vertxVersion")

    compileOnly("net.objecthunter:exp4j:0.4.8")

    /* Npcs */
    compileOnly("net.citizensnpcs:citizensapi:2.0.38-SNAPSHOT")
    compileOnly("de.oliver:FancyNpcs:2.6.0")

    /* Labymod */
    compileOnly("net.labymod.serverapi:server-bukkit:1.0.6")

    /* QualityArmory Compatibility */
    compileOnly("me.zombie_striker:QualityArmory:2.1.2")
}

tasks {
    runServer {
        minecraftVersion("1.21.11")
        jvmArgs("-Dcom.mojang.eula.agree=true", "-Dfile.encoding=UTF-8")
        downloadPlugins {
            github("MilkBowl", "Vault", "1.7.3", "Vault.jar")
            hangar("PlaceholderAPI", "2.11.6")
            modrinth("WorldGuard", "7.0.16-beta-01")
            modrinth("WorldEdit", "CkT32vix")
            modrinth("qualityarmory", "2.1.2")
        }
    }

    processResources {
        val props = mapOf("version" to project.version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    shadowJar {
        archiveFileName.set(rootProject.name + "-" + project.version + ".jar")

        relocate("co.aikar.commands", "nl.openminetopia.shaded.acf")
        relocate("co.aikar.locales", "nl.openminetopia.shaded.locales")
        relocate("net.megavex.scoreboardlibrary", "nl.openminetopia.shaded.scoreboard")
        relocate("com.jeff_media.customblockdata", "nl.openminetopia.shaded.customblockdata")
        relocate("com.jeff_media.morepersistentdatatypes", "nl.openminetopia.shaded.morepersistentdatatypes")
        relocate("dev.triumphteam.gui", "nl.openminetopia.shaded.gui")
        relocate("org.bstats", "nl.openminetopia.shaded.bstats")
    }

    build {
        dependsOn(shadowJar)
    }
}
