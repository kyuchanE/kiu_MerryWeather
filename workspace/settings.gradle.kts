pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "MerryWeather"
include(":app")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":feature:weather")
include(":feature:setting")
include(":feature:main")
