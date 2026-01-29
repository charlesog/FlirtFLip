pluginManagement {
    repositories {
        google()           // ✅ Necesario para Firebase y Android plugins
        mavenCentral()     // ✅ Para otras librerías
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()           // ✅ Aquí también para Firebase
        mavenCentral()
    }
}

rootProject.name = "FlirtFlip"
include(":app")
