pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "com.google.cloud.tools.appengine") {
        useModule("com.google.cloud.tools:appengine-gradle-plugin:2.0.0-rc3")
      } else if (requested.id.id == "org.akhikhl.gretty") {
        useModule("org.akhikhl.gretty:gretty:2.0.0")
      }
    }
  }
}

//sourceControl {
//  gitRepository(uri("https://github.com/yschimke/okurl.git")) {
//    producesModule("com.baulsupp:okurl")
//  }
//}

rootProject.name = "cooee"

