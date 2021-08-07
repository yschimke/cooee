import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.google.cloud.tools.jib") version "3.1.2"
	kotlin("jvm") version "1.5.21"
	kotlin("plugin.spring") version "1.5.21"
	id("com.squareup.wire") version "4.0.0-alpha.3"
	id("com.diffplug.spotless") version "5.1.0"
	id("com.apollographql.apollo").version("2.5.5")
}

group = "com.baulsupp.cooee"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(11))
	}
}

repositories {
	mavenCentral()
	maven(url = "https://jitpack.io") {
		group = "com.github.yschimke"
	}
}

extra["springCloudGcpVersion"] = "2.0.3"
extra["springCloudVersion"] = "2020.0.3"
extra["testcontainersVersion"] = "1.15.3"

dependencyManagement {
	imports {
		mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:${property("springCloudGcpVersion")}")
		mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

wire {
	kotlin {
		out = "src/main/kotlin"
		javaInterop = true
	}
}

apollo {
	generateKotlinModels.set(true)

	service("github") {
		sourceFolder.set("com/github/cooee")
		rootPackageName.set("com.github.cooee")
	}
}

jib {
	from.image = "gcr.io/distroless/java:11"

	to {
		image = "gcr.io/coo-ee/app"
		credHelper = "gcr"
		auth {
			username = "oauth2accesstoken"
			password = gcloudAuthToken()
		}
	}
	container {
		ports = listOf("8080")
		mainClass = "com.baulsupp.cooee.CooeeApplicationKt"

		// good defauls intended for Java 8 (>= 8u191) containers
		jvmFlags = listOf(
			"-server",
			"-Djava.awt.headless=true",
			"-XX:+UseG1GC",
			"-XX:MaxGCPauseMillis=100",
		)
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-rsocket")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.security:spring-security-rsocket")
	implementation("org.springframework.security:spring-security-messaging")
	implementation("org.springframework.boot:spring-boot-starter-logging")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("com.google.cloud:spring-cloud-gcp-starter-secretmanager")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	// The core runtime dependencies
	implementation("com.apollographql.apollo:apollo-runtime:2.5.5")
	// Coroutines extensions for easier asynchronicity handling
	implementation("com.apollographql.apollo:apollo-coroutines-support:2.5.5")

	implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
	implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
	implementation("com.github.yschimke:oksocial-output:6.2")
	implementation("com.github.yschimke:okurl:2.31") {
		isTransitive = false
	}

	implementation("com.squareup.wire:wire-runtime:3.7.0")
	implementation("com.squareup.wire:wire-moshi-adapter:3.7.0")
	implementation("com.squareup.wire:wire-grpc-client:3.7.0") {
		exclude(group = "com.squareup.okhttp3")
	}
	implementation("com.squareup.wire:wire-moshi-adapter:3.7.0")

	implementation("com.squareup.moshi:moshi:1.12.0")
	implementation("com.squareup.moshi:moshi-adapters:1.12.0")

	implementation("com.google.code.gson:gson:2.8.6")

	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.testcontainers:junit-jupiter")
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict -Xopt-in=kotlin.RequiresOptIn")
		jvmTarget = "11"
	}
}

fun gcloudAuthToken() = try {
	ProcessBuilder("gcloud auth print-access-token".split(' '))
		.redirectOutput(ProcessBuilder.Redirect.PIPE)
		.start()
		.inputStream.bufferedReader().readText().trim()
} catch (e: Exception) {
	e.printStackTrace()
	null
}
