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

    // https://plugins.gradle.org/plugin/team.yi.semantic-gitlog
    id("team.yi.semantic-gitlog") version "0.6.5"

    // https://plugins.gradle.org/plugin/se.patrikerdes.use-latest-versions
    id("se.patrikerdes.use-latest-versions") version "0.2.18"
    // https://plugins.gradle.org/plugin/com.github.ben-manes.versions
    id("com.github.ben-manes.versions") version "0.44.0"

    // https://plugins.gradle.org/plugin/io.gitlab.arturbosch.detekt
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

group = "team.yi.rsql"
version = "1.9.2"
description = "Integration RSQL query language and Querydsl framework."

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.1")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.1")

    // https://mvnrepository.com/artifact/com.h2database/h2
    testImplementation("com.h2database:h2:2.1.214")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.1")

    // https://mvnrepository.com/artifact/javax.persistence/javax.persistence-api
    api("javax.persistence:javax.persistence-api:2.2")
    // https://mvnrepository.com/artifact/jakarta.persistence/jakarta.persistence-api
    api("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // https://mvnrepository.com/artifact/com.querydsl/querydsl-apt
    api("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    api("com.querydsl:querydsl-codegen:5.0.0")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    // https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    api("org.apache.commons:commons-lang3:3.12.0")

    // https://mvnrepository.com/artifact/cz.jirutka.rsql/rsql-parser
    api("cz.jirutka.rsql:rsql-parser:2.1.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${detekt.toolVersion}")
}

tasks {
    jar { enabled = true }
    test { useJUnitPlatform() }

    val kotlinSettings: KotlinCompile.() -> Unit = {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.majorVersion
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
        group = "publishing"

        toRef = "main"
        strategy = team.yi.tools.semanticgitlog.VersionStrategy.slow

        minorTypes = "feat,refactor,perf,revert"
        patchTypes = "chore,docs,build,fix"

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
                file("${project.rootDir}/config/gitlog/CHANGELOG.zh-cn.md.mustache"),
                file("${project.rootDir}/CHANGELOG.zh-cn.md")
            )
        )
        commitLocales = mapOf(
            "en" to file("${project.rootDir}/config/gitlog/commit-locales.md"),
            "zh-cn" to file("${project.rootDir}/config/gitlog/commit-locales.zh-cn.md")
        )
        scopeProfiles = mapOf(
            "en" to file("${project.rootDir}/config/gitlog/commit-scopes.md"),
            "zh-cn" to file("${project.rootDir}/config/gitlog/commit-scopes.zh-cn.md")
        )

        outputs.upToDateWhen { false }
    }

    derive {
        group = "publishing"

        toRef = "main"
        strategy = team.yi.tools.semanticgitlog.VersionStrategy.slow
        derivedVersionMark = "NEXT_VERSION:=="

        minorTypes = "feat,refactor,perf,revert"
        patchTypes = "chore,docs,build,fix"

        commitLocales = mapOf(
            "en" to file("${project.rootDir}/config/gitlog/commit-locales.md"),
            "zh-cn" to file("${project.rootDir}/config/gitlog/commit-locales.zh-cn.md")
        )

        outputs.upToDateWhen { false }
    }
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("$rootDir/config/detekt/detekt.yml")
    baseline = file("$rootDir/config/detekt/baseline.xml")
}

tasks.register("bumpVersion") {
    group = "publishing"

    dependsOn(":changelog")

    doLast {
        var newVersion = rootProject.findProperty("newVersion") as? String

        if (newVersion.isNullOrEmpty()) {
            // ^## ([\d\.]+(-SNAPSHOT)?) \(.+\)$
            val changelogContents = file("${rootProject.rootDir}/CHANGELOG.md").readText()
            val versionRegex = Regex("^## ([\\d\\.]+(-SNAPSHOT)?) \\(.+\\)\$", setOf(RegexOption.MULTILINE))
            val changelogVersion = versionRegex.find(changelogContents)?.groupValues?.get(1)

            changelogVersion?.let { newVersion = it }

            logger.warn("changelogVersion: {}", changelogVersion)
            logger.warn("newVersion: {}", newVersion)
        }

        newVersion?.let {
            logger.info("Set Project to new Version $it")

            val contents = buildFile.readText()
                .replaceFirst("version = \"$version\"", "version = \"$newVersion\"")

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
