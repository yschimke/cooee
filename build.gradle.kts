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

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
  withType(KotlinCompile::class) {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.apiVersion = "1.3"
    kotlinOptions.languageVersion = "1.3"
  }
}

repositories {
  mavenLocal()
  jcenter()
  maven { url = uri("https://kotlin.bintray.com/ktor") }
  maven { url = uri("https://kotlin.bintray.com/kotlinx") }
  maven { url = uri("https://kotlin.bintray.com/kotlin-js-wrappers") }
  maven { url = uri("https://dl.bintray.com/yschimke/baulsupp.com/") }
}

tasks.create("downloadDependencies") {
  description = "Downloads dependencies"

  doLast {
    configurations.forEach {
      if (it.isCanBeResolved) {
        it.resolve()
      }
    }
  }
}

dependencies {
  implementation("org.conscrypt:conscrypt-openjdk-uber:1.4.1")
  implementation("io.honeycomb.libhoney:libhoney-java:1.0.2")
  implementation("com.baulsupp:okurl:1.66.0")
  implementation("com.squareup.okhttp3:okhttp:3.12.0")
  implementation("com.squareup.okhttp3:logging-interceptor:3.12.0")
  implementation("ch.qos.logback:logback-classic:${Versions.logback}")
  implementation("com.ryanharter.ktor:ktor-moshi:1.0.1")
  implementation("com.squareup.moshi:moshi-adapters:1.8.0")
  implementation("io.ktor:ktor-auth-jwt:${Versions.ktor}")
  implementation("io.ktor:ktor-auth-ldap:${Versions.ktor}")
  implementation("io.ktor:ktor-auth:${Versions.ktor}")
  implementation("io.ktor:ktor-client-auth-basic:${Versions.ktor}")
  implementation("io.ktor:ktor-client-cio:${Versions.ktor}")
  implementation("io.ktor:ktor-client-core-jvm:${Versions.ktor}")
  implementation("io.ktor:ktor-client-core:${Versions.ktor}")
  implementation("io.ktor:ktor-client-gson:${Versions.ktor}")
  implementation("io.ktor:ktor-client-json-jvm:${Versions.ktor}")
  implementation("io.ktor:ktor-client-okhttp:${Versions.ktor}")
  implementation("io.ktor:ktor-client-websocket:${Versions.ktor}")
  implementation("io.ktor:ktor-html-builder:${Versions.ktor}")
  implementation("io.ktor:ktor-jackson:${Versions.ktor}")
  implementation("io.ktor:ktor-locations:${Versions.ktor}")
  implementation("io.ktor:ktor-metrics:${Versions.ktor}")
  implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
  implementation("io.ktor:ktor-server-core:${Versions.ktor}")
  implementation("io.ktor:ktor-server-host-common:${Versions.ktor}")
  implementation("io.ktor:ktor-server-sessions:${Versions.ktor}")
  implementation("io.ktor:ktor-server-servlet:${Versions.ktor}")
  implementation("io.ktor:ktor-webjars:${Versions.ktor}")
  implementation("io.ktor:ktor-websockets:${Versions.ktor}")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
  implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.59-kotlin-1.3.0")
  implementation("org.webjars:jquery:3.2.1")
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
