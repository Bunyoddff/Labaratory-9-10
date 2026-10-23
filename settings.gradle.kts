pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BookNest"
include(":app")
include(":core:model")
include(":core:util")
include(":core:ui")
include(":core:testing")
include(":data:books-api")
include(":data:books-impl")
include(":data:books-demo")
include(":data:orders")
include(":feature:catalog")
include(":feature:favorites")
include(":feature:checkout")
