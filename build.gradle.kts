import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.3"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("com.google.cloud.tools.appengine") version "2.4.1"
	kotlin("jvm") version "1.4.31"
	kotlin("plugin.spring") version "1.4.30"
	id("com.squareup.wire") version "3.4.0"
	id("com.diffplug.spotless") version "5.1.0"
	id("com.apollographql.apollo").version("2.5.5")
}

group = "com.baulsupp.cooee"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
	maven(url = "https://jitpack.io") {
		group = "com.github.yschimke"
	}
}

appengine {
	deploy {
		version = "GCLOUD_CONFIG"
		projectId = "GCLOUD_CONFIG"
	}
}

extra["springCloudGcpVersion"] = "2.0.1"
extra["springCloudVersion"] = "2020.0.1"
extra["testcontainersVersion"] = "1.15.1"

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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-rsocket")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
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
	implementation("com.github.yschimke:oksocial-output:5.7")
	implementation("com.github.yschimke:okurl:2.29") {
		isTransitive = false
	}

	implementation("com.squareup.wire:wire-runtime:3.4.0")
	implementation("com.squareup.wire:wire-moshi-adapter:3.4.0")
	implementation("com.squareup.wire:wire-grpc-client:3.4.0") {
		exclude(group = "com.squareup.okhttp3")
	}
	implementation("com.squareup.wire:wire-moshi-adapter:3.4.0")

	implementation("com.squareup.moshi:moshi:1.11.0")
	implementation("com.squareup.moshi:moshi-adapters:1.11.0")

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
		jvmTarget = "1.8"
	}
}
