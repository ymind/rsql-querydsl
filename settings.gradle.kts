pluginManagement {
    val kotlinPluginVersion = "1.4.21-2"

    plugins {
        kotlin("jvm") version kotlinPluginVersion
        kotlin("kapt") version kotlinPluginVersion
        kotlin("plugin.allopen") version kotlinPluginVersion
        kotlin("plugin.noarg") version kotlinPluginVersion

        kotlin("plugin.jpa") version kotlinPluginVersion
        kotlin("plugin.spring") version kotlinPluginVersion
    }
}

rootProject.name = "rsql-querydsl"
