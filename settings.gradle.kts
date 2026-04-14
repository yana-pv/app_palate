pluginManagement {
    includeBuild("convention-plugins")
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

rootProject.name = "Palate"
include(":app")
include(":core:data")
include(":core:domain")
include(":core:design")
include(":core:network")
include(":core:di")
include(":feature")
include(":feature:auth")
include(":feature:home")
include(":feature:recipe_detail")
include(":feature:my_recipes")
include(":feature:create_recipe")
include(":feature:shopping_list")
include(":feature:profile")
include(":core:network")
include(":core:utils")
