import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.3.3.RELEASE"
	id("io.spring.dependency-management") version "1.0.10.RELEASE"
	kotlin("jvm") version "1.4.10"
	kotlin("plugin.spring") version "1.4.10"
	id("com.squareup.wire") version "3.4.0"
	id("com.diffplug.spotless") version "5.1.0"
}

group = "com.baulsupp.cooee"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven(url = "https://jitpack.io")
}

extra["testcontainersVersion"] = "1.14.3"

wire {
	kotlin {
		out = "src/main/kotlin"
		javaInterop = true
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
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	implementation("org.thymeleaf:thymeleaf-spring5:3.0.11.RELEASE")

	implementation("com.squareup.okhttp3:okhttp:4.9.0")
	implementation("com.github.yschimke:oksocial-output:5.7")
	implementation("com.github.yschimke:okurl:2.23") {
		isTransitive = false
	}

	implementation("com.squareup.wire:wire-runtime:3.4.0")
	implementation("com.squareup.wire:wire-moshi-adapter:3.4.0")
	implementation("com.squareup.wire:wire-grpc-client:3.4.0") {
		exclude(group = "com.squareup.okhttp3")
	}
	implementation("com.squareup.wire:wire-moshi-adapter:3.4.0")

	implementation("com.squareup.moshi:moshi:1.10.0")
	implementation("com.squareup.moshi:moshi-adapters:1.10.0")

	implementation("com.google.code.gson:gson:2.8.6")

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
		freeCompilerArgs = listOf("-Xjsr305=strict  -Xopt-in=kotlin.RequiresOptIn")
		jvmTarget = "11"
	}
}
