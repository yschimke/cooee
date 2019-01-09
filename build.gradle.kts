import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
//  application
  kotlin("jvm") version Versions.kotlin
  war
  id("com.github.ben-manes.versions") version "0.20.0"
  id ("com.google.cloud.tools.appengine") version "2.0.0-rc4"
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
  implementation("ch.qos.logback:logback-classic:${Versions.logback}")
  implementation("com.baulsupp:okurl:${Versions.okurl}")
  implementation("com.google.cloud:google-cloud-logging-logback:0.73.0-alpha")
  implementation("com.ryanharter.ktor:ktor-moshi:1.0.1")
  implementation("com.squareup.moshi:moshi-adapters:1.8.0")
  implementation("com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}")
  implementation("com.squareup.okhttp3:okhttp:${Versions.okhttp}")
  implementation("com.squareup.okio:okio:${Versions.okio}")
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
  implementation("io.ktor:ktor-http-jvm:${Versions.ktor}")
  implementation("io.ktor:ktor-http:${Versions.ktor}")
  implementation("io.ktor:ktor-jackson:${Versions.ktor}")
  implementation("io.ktor:ktor-locations:${Versions.ktor}")
  implementation("io.ktor:ktor-metrics:${Versions.ktor}")
  implementation("io.ktor:ktor-server-core:${Versions.ktor}")
  implementation("io.ktor:ktor-server-host-common:${Versions.ktor}")
  implementation("io.ktor:ktor-server-netty:${Versions.ktor}")
  implementation("io.ktor:ktor-server-servlet:${Versions.ktor}")
  implementation("io.ktor:ktor-server-sessions:${Versions.ktor}")
  implementation("io.ktor:ktor-utils-jvm:${Versions.ktor}")
  implementation("io.ktor:ktor-utils:${Versions.ktor}")
  implementation("io.ktor:ktor-webjars:${Versions.ktor}")
  implementation("io.ktor:ktor-websockets:${Versions.ktor}")
  implementation("io.netty:netty-buffer:${Versions.netty}") { setForce(true) }
  implementation("io.netty:netty-codec-dns:${Versions.netty}") { setForce(true) }
  implementation("io.netty:netty-codec-http2:${Versions.netty}") { setForce(true) }
  implementation("io.netty:netty-codec-http:${Versions.netty}") { setForce(true) }
  implementation("io.netty:netty-codec:${Versions.netty}") { setForce(true) }
  implementation("io.netty:netty-common:${Versions.netty}") { setForce(true) }
  implementation("io.netty:netty-handler:${Versions.netty}") { setForce(true) }
  implementation("io.netty:netty-resolver-dns:${Versions.netty}") { setForce(true) }
  implementation("io.netty:netty-resolver:${Versions.netty}") { setForce(true) }
  implementation("io.netty:netty-transport:${Versions.netty}") { setForce(true) }
  implementation("io.projectreactor:reactor-core:${Versions.reactor}")
  implementation("io.projectreactor:reactor-core:3.2.3.RELEASE")
  implementation("org.conscrypt:conscrypt-openjdk-uber:${Versions.conscrypt}")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${Versions.kotlinCoroutines}")
  implementation("org.jetbrains:kotlin-css-jvm:1.0.0-pre.59-kotlin-1.3.0")
  implementation("org.mongodb:mongodb-driver-reactivestreams:1.10.0")
  implementation("org.webjars:jquery:3.2.1")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:${Versions.kotlinCoroutines}")
  testCompile("io.ktor:ktor-server-tests:${Versions.ktor}")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src/main/kotlin")
kotlin.sourceSets["test"].kotlin.srcDirs("src/test/kotlin")

sourceSets["main"].resources.srcDirs("src/main/resources")
sourceSets["test"].resources.srcDirs("src/test/resources")

war.webAppDirName = "src/main/webapp"
