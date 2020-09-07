import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    `maven-publish`
    signing

    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.allopen")
    kotlin("plugin.noarg")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")

    id("org.jlleitschuh.gradle.ktlint") version "9.4.0"
    id("team.yi.semantic-gitlog") version "0.5.13"
}

group = "team.yi.rsql"
version = "0.1.10"
description = "Integration RSQL query language and Querydsl framework."

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.3.2.RELEASE") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa:2.3.2.RELEASE")

    // https://mvnrepository.com/artifact/com.h2database/h2
    testImplementation("com.h2database:h2:1.4.200")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.2")

    // https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api
    api("javax.persistence:javax.persistence-api:2.2")

    // https://mvnrepository.com/artifact/com.querydsl/querydsl-apt
    api("com.querydsl:querydsl-jpa:4.3.1")
    api("com.querydsl:querydsl-codegen:4.3.1")
    kapt("com.querydsl:querydsl-apt:4.3.1:jpa")

    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    api("org.apache.commons:commons-lang3:3.11")

    // https://mvnrepository.com/artifact/cz.jirutka.rsql/rsql-parser
    api("cz.jirutka.rsql:rsql-parser:2.1.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks {
    jar { enabled = true }
    test { useJUnitPlatform() }

    val kotlinSettings: KotlinCompile.() -> Unit = {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.freeCompilerArgs += listOf(
            "-Xjsr305=strict"
        )
    }

    compileKotlin(kotlinSettings)
    compileTestKotlin(kotlinSettings)
    compileJava { options.encoding = "UTF-8" }
    compileTestJava { options.encoding = "UTF-8" }
    javadoc { options.encoding = "UTF-8" }

    changelog {
        toRef = "master"
        lastVersion = "0.0.0"

        issueUrlTemplate = "https://github.com/ymind/rsql-querydsl/issues/:issueId"
        commitUrlTemplate = "https://github.com/ymind/rsql-querydsl/commit/:commitId"
        mentionUrlTemplate = "https://github.com/:username"

        // jsonFile = file("${project.rootDir}/CHANGELOG.json")
        fileSets = setOf(
            team.yi.gradle.plugin.FileSet(
                file("${project.rootDir}/config/gitlog/CHANGELOG.md.mustache"),
                file("${project.rootDir}/CHANGELOG.md")
            ),
            team.yi.gradle.plugin.FileSet(
                file("${project.rootDir}/config/gitlog/CHANGELOG_zh-cn.md.mustache"),
                file("${project.rootDir}/CHANGELOG_zh-cn.md")
            )
        )
        commitLocales = mapOf(
            "en" to file("${project.rootDir}/config/gitlog/commit-locales.md"),
            "zh-cn" to file("${project.rootDir}/config/gitlog/commit-locales_zh-cn.md")
        )
        scopeProfiles = mapOf(
            "en" to file("${project.rootDir}/config/gitlog/commit-scopes.md"),
            "zh-cn" to file("${project.rootDir}/config/gitlog/commit-scopes_zh-cn.md")
        )

        outputs.upToDateWhen { false }
    }

    derive {
        toRef = "master"
        derivedVersionMark = "NEXT_VERSION:=="

        // jsonFile = file("${project.rootDir}/CHANGELOG.json")
        commitLocales = mapOf(
            "zh-cn" to file("${project.rootDir}/config/gitlog/commit-locales_zh-cn.md")
        )

        outputs.upToDateWhen { false }
    }
}

tasks.register("setNewVersion") {
    doLast {
        val newVersion = project.findProperty("newVersion") as? String

        newVersion?.let {
            logger.info("Set Project to new Version $newVersion")

            val contents = buildFile.readText().replaceFirst("version = \"$version\"", "version = \"$newVersion\"")

            buildFile.writeText(contents)
        }
    }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/ymind/rsql-querydsl")
                inceptionYear.set("2020")

                scm {
                    url.set("https://github.com/ymind/rsql-querydsl")
                    connection.set("scm:git:git@github.com:ymind/rsql-querydsl.git")
                    developerConnection.set("scm:git:git@github.com:ymind/rsql-querydsl.git")
                }

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }

                organization {
                    name.set("Yi.Team")
                    url.set("https://yi.team/")
                }

                developers {
                    developer {
                        name.set("ymind")
                        email.set("ymind@yi.team")
                        url.set("https://yi.team/")
                        organization.set("Yi.Team")
                        organizationUrl.set("https://yi.team/")
                    }
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/ymind/rsql-querydsl/issues")
                }

                ciManagement {
                    system.set("GitHub")
                    url.set("https://github.com/ymind/rsql-querydsl/actions")
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")

            url = if (version.toString().endsWith("SNAPSHOT", true)) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = System.getenv("OSSRH_USERNAME") ?: "${properties["OSSRH_USERNAME"]}"
                password = System.getenv("OSSRH_TOKEN") ?: "${properties["OSSRH_TOKEN"]}"
            }
        }
    }
}

signing {
    extra.set("signing.keyId", System.getenv("OSSRH_GPG_SECRET_ID") ?: "${properties["OSSRH_GPG_SECRET_ID"]}")
    extra.set("signing.secretKeyRingFile", System.getenv("OSSRH_GPG_SECRET_KEY") ?: "${properties["OSSRH_GPG_SECRET_KEY"]}")
    extra.set("signing.password", System.getenv("OSSRH_GPG_SECRET_PASSWORD") ?: "${properties["OSSRH_GPG_SECRET_PASSWORD"]}")

    sign(publishing.publications.getByName("mavenJava"))
}
