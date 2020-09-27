rootProject.name = "cooee"

// settings.gradle.kts
pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "com.squareup.wire") {
        useModule("com.squareup.wire:wire-gradle-plugin:3.4.0")
      }
    }
  }
}
