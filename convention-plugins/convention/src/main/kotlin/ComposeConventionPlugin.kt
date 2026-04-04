import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import ru.itis.android.core.plugin.ext.libs

class ComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            apply(plugin = libs.findPlugin("kotlin-compose").get().get().pluginId)

            extensions.configure<CommonExtension> {
                buildFeatures.compose = true
            }

            dependencies {
                "implementation"(libs.findBundle("compose.ui").get())
                "implementation"(libs.findLibrary("compose.foundation").get())
                "implementation"(libs.findLibrary("compose.material3").get())
            }
        }
    }
}