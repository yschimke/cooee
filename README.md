# cooee

[![CircleCI](https://circleci.com/gh/yschimke/cooee.svg?style=svg&circle-token=3c8cea878ad9569c6702cb5f336e05f645ac256b)](https://circleci.com/gh/yschimke/cooee)

API website deployment (currently WWW also)

##  Setup

* Use IntelliJ Community
* Run `./gradlew build` or press the refresh icon in the Gradle menu in IntelliJ"
* Run class `src/test/kotlin/com/baulsupp/cooee/main.kt`
* Browse to http://localhost:8080

NOTE: Do not to run with `./gradlew appengineRun` or equivalent as it will try and apply environment parameters that are only suitable for GCP deployment (e.g. redirect from http to https) 

## Deployment

* `./gradlew appengineDeploy`

![Coo.ee Boomerang](https://coo.ee/images/boomerang.ico/android-icon-192x192.png)
