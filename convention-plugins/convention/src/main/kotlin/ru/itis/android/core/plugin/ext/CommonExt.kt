package ru.itis.android.core.plugin.ext

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.PluginInstantiationException
import org.gradle.kotlin.dsl.findByType

val Project.commonExtension: CommonExtension
    get() = applicationExtension
        ?: libraryExtension
        ?: throw PluginInstantiationException("Can be applied only on android Application or Library")

val Project.applicationExtension: ApplicationExtension?
    get() = extensions.findByType<ApplicationExtension>()

val Project.libraryExtension: LibraryExtension?
    get() = extensions.findByType<LibraryExtension>()

val Project.libs
    get(): VersionCatalog = extensions
        .findByType<VersionCatalogsExtension>()
        ?.named("libs")
        ?: throw PluginInstantiationException("Version catalog file not found")