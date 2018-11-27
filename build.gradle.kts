import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//  application
  kotlin("jvm") version "1.3.10"
  war
  id("com.github.ben-manes.versions") version "0.20.0"
  id ("com.google.cloud.tools.appengine") version "2.0.0-rc3"
  id("org.akhikhl.gretty") version "2.0.0"
}

group = "cooee"
version = "0.0.1-SNAPSHOT"

//application {
//  mainClassName = "io.ktor.server.cio.EngineMain"
//}

appengine {
  deploy {
    version = "1"
    projectId = "coo-ee"
  }
}

repositories {
  mavenLocal()
  jcenter()
  maven { url = uri("https://kotlin.bintray.com/ktor") }
  maven { url = uri("https://kotlin.bintray.com/kotlinx") }
  maven { url = uri("https://kotlin.bintray.com/kotlin-js-wrappers") }
}

dependencies {
  compile("ch.qos.logback:logback-classic:${Versions.logback}")
  compile("com.ryanharter.ktor:ktor-moshi:1.0.0")
  compile("com.squareup.moshi:moshi-adapters:1.8.0")
  compile("io.ktor:ktor-auth-jwt:${Versions.ktor}")
  compile("io.ktor:ktor-auth-ldap:${Versions.ktor}")
  compile("io.ktor:ktor-auth:${Versions.ktor}")
  compile("io.ktor:ktor-client-auth-basic:${Versions.ktor}")
  compile("io.ktor:ktor-client-cio:${Versions.ktor}")
  compile("io.ktor:ktor-client-core-jvm:${Versions.ktor}")
  compile("io.ktor:ktor-client-core:${Versions.ktor}")
  compile("io.ktor:ktor-client-gson:${Versions.ktor}")
  compile("io.ktor:ktor-client-json-jvm:${Versions.ktor}")
  compile("io.ktor:ktor-client-okhttp:${Versions.ktor}")
  compile("io.ktor:ktor-client-websocket:${Versions.ktor}")
  compile("io.ktor:ktor-html-builder:${Versions.ktor}")
  compile("io.ktor:ktor-jackson:${Versions.ktor}")
  compile("io.ktor:ktor-locations:${Versions.ktor}")
  compile("io.ktor:ktor-metrics:${Versions.ktor}")
  compile("io.ktor:ktor-server-netty:${Versions.ktor}")
  compile("io.ktor:ktor-server-core:${Versions.ktor}")
  compile("io.ktor:ktor-server-host-common:${Versions.ktor}")
  compile("io.ktor:ktor-server-sessions:${Versions.ktor}")
  compile("io.ktor:ktor-server-servlet:${Versions.ktor}")
  compile("io.ktor:ktor-webjars:${Versions.ktor}")
  compile("io.ktor:ktor-websockets:${Versions.ktor}")
  compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
  compile("org.jetbrains:kotlin-css-jvm:1.0.0-pre.59-kotlin-1.3.0")
  compile("org.webjars:jquery:3.2.1")
  testCompile("io.ktor:ktor-server-tests:${Versions.ktor}")
}

gretty {
  contextPath = '/'
}

kotlin.sourceSets["main"].kotlin.srcDirs("src/main/kotlin")
kotlin.sourceSets["test"].kotlin.srcDirs("src/test/kotlin")

sourceSets["main"].resources.srcDirs("src/main/resources")
sourceSets["test"].resources.srcDirs("src/test/resources")

war.webAppDirName = "src/main/webapp"
