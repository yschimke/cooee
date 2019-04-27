import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.3.31"
  war
  id("com.github.ben-manes.versions") version "0.21.0"
  id("com.google.cloud.tools.appengine") version "2.0.0-rc6"
  id("com.palantir.consistent-versions") version "1.5.0"
  id("com.diffplug.gradle.spotless") version "3.23.0"
}

group = "cooee"
version = "0.0.1-SNAPSHOT"

appengine {
  stage {
    enableJarClasses = true
  }
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
  maven { url = uri("https://dl.bintray.com/konrad-kaminski/maven") }
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
  implementation("ch.qos.logback:logback-classic")
  implementation("com.baulsupp:okurl")
  implementation("com.google.cloud:google-cloud-logging-logback")
  implementation("com.google.appengine:appengine-api-1.0-sdk")
  implementation("io.lettuce:lettuce-core")
  implementation("com.ryanharter.ktor:ktor-moshi")
  implementation("com.squareup.moshi:moshi-adapters")
  implementation("com.squareup.okhttp3:logging-interceptor")
  implementation("com.squareup.okhttp3:okhttp")
  implementation("com.squareup.okio:okio")
  implementation("io.ktor:ktor-auth-jwt")
  implementation("io.ktor:ktor-auth-ldap")
  implementation("io.ktor:ktor-auth")
  implementation("io.ktor:ktor-client-auth-basic")
  implementation("io.ktor:ktor-client-cio")
  implementation("io.ktor:ktor-client-core-jvm")
  implementation("io.ktor:ktor-client-core")
  implementation("io.ktor:ktor-client-gson")
  implementation("io.ktor:ktor-client-json-jvm")
  implementation("io.ktor:ktor-client-okhttp")
  implementation("io.ktor:ktor-client-websocket")
  implementation("io.ktor:ktor-html-builder")
  implementation("io.ktor:ktor-http-jvm")
  implementation("io.ktor:ktor-http")
  implementation("io.ktor:ktor-jackson")
  implementation("io.ktor:ktor-locations")
  implementation("io.ktor:ktor-metrics")
  implementation("io.ktor:ktor-server-core")
  implementation("io.ktor:ktor-server-host-common")
  implementation("io.ktor:ktor-server-netty")
  implementation("io.ktor:ktor-server-servlet")
  implementation("io.ktor:ktor-server-sessions")
  implementation("io.ktor:ktor-utils-jvm")
  implementation("io.ktor:ktor-utils")
  implementation("io.ktor:ktor-webjars")
  implementation("io.ktor:ktor-websockets")
  implementation("io.netty:netty-buffer")
  implementation("io.netty:netty-codec-dns")
  implementation("io.netty:netty-codec-http2")
  implementation("io.netty:netty-codec-http")
  implementation("io.netty:netty-codec")
  implementation("io.netty:netty-common")
  implementation("io.netty:netty-handler")
  implementation("io.netty:netty-resolver-dns")
  implementation("io.netty:netty-resolver")
  implementation("io.netty:netty-transport")
  implementation("io.projectreactor:reactor-core")
  implementation("io.projectreactor:reactor-core")
  implementation("org.conscrypt:conscrypt-openjdk-uber")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
  implementation("org.jetbrains:kotlin-css-jvm")
  implementation("org.mongodb:mongodb-driver-reactivestreams")
  implementation("org.mongodb:mongodb-driver-sync")
  implementation("org.webjars:jquery")
  implementation("org.litote.kmongo:kmongo-coroutine")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug")
  implementation("org.ff4j:ff4j-core")
  implementation("org.ff4j:ff4j-store-mongodb-v3")
  implementation("org.ff4j:ff4j-web")
  implementation("javax.servlet:javax.servlet-api")
  testCompile("io.ktor:ktor-server-tests")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src/main/kotlin")
kotlin.sourceSets["test"].kotlin.srcDirs("src/test/kotlin")

sourceSets["main"].resources.srcDirs("src/main/resources")
sourceSets["test"].resources.srcDirs("src/test/resources")

war.webAppDirName = "src/main/webapp"

val dependencyUpdates = tasks["dependencyUpdates"] as DependencyUpdatesTask
dependencyUpdates.resolutionStrategy {
  componentSelection {
    all {
      if (candidate.group == "io.netty" && candidate.version.startsWith("5.")) {
        reject("Alpha")
      } else if (candidate.version.contains("lpha")) {
        reject("Alpha")
      }
    }
  }
}

spotless {
  kotlinGradle {
    ktlint("0.31.0").userData(mutableMapOf("indent_size" to "2", "continuation_indent_size" to "2"))
    trimTrailingWhitespace()
    endWithNewline()
  }
}
