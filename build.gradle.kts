import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.3.10"
}

group = "cooee"
version = "0.0.1-SNAPSHOT"

application {
    mainClassName = "io.ktor.server.cio.EngineMain"
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
    maven { url = uri("https://kotlin.bintray.com/kotlinx") }
    maven { url = uri("https://kotlin.bintray.com/kotlin-js-wrappers") }
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    compile("io.ktor:ktor-server-cio:${Versions.ktor}")
    compile("ch.qos.logback:logback-classic:${Versions.logback}")
    compile("io.ktor:ktor-client-core:${Versions.ktor}")
    compile("io.ktor:ktor-client-core-jvm:${Versions.ktor}")
    compile("io.ktor:ktor-client-auth-basic:${Versions.ktor}")
    compile("io.ktor:ktor-client-json-jvm:${Versions.ktor}")
    compile("io.ktor:ktor-client-gson:${Versions.ktor}")
    compile("io.ktor:ktor-client-cio:${Versions.ktor}")
    compile("io.ktor:ktor-server-core:${Versions.ktor}")
    compile("io.ktor:ktor-websockets:${Versions.ktor}")
    compile("io.ktor:ktor-client-websocket:${Versions.ktor}")
    compile("io.ktor:ktor-html-builder:${Versions.ktor}")
    compile("org.jetbrains:kotlin-css-jvm:1.0.0-pre.31-kotlin-1.2.41")
    compile("io.ktor:ktor-auth:${Versions.ktor}")
    compile("io.ktor:ktor-auth-jwt:${Versions.ktor}")
    compile("io.ktor:ktor-auth-ldap:${Versions.ktor}")
    compile("io.ktor:ktor-jackson:${Versions.ktor}")
    compile("io.ktor:ktor-locations:${Versions.ktor}")
    compile("io.ktor:ktor-metrics:${Versions.ktor}")
    compile("io.ktor:ktor-server-sessions:${Versions.ktor}")
    compile("io.ktor:ktor-server-host-common:${Versions.ktor}")
    compile("io.ktor:ktor-webjars:${Versions.ktor}")
    compile("org.webjars:jquery:3.2.1")
    testCompile("io.ktor:ktor-server-tests:${Versions.ktor}")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
