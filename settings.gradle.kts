pluginManagement {
    val kotlinVersion = "1.7.21"

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("kapt") version kotlinVersion
        kotlin("plugin.allopen") version kotlinVersion
        kotlin("plugin.noarg") version kotlinVersion

        kotlin("plugin.jpa") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
    }
}

rootProject.name = "rsql-querydsl"
