import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import ru.itis.android.core.plugin.ext.libs

class DaggerConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = libs.findPlugin("ksp").get().get().pluginId)

            dependencies {
                "implementation"(libs.findLibrary("dagger").get())
                "ksp"(libs.findLibrary("dagger.compiler").get())
            }
        }
    }
}