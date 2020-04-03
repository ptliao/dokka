package utils

import org.jetbrains.dokka.DokkaConfigurationImpl
import org.jetbrains.dokka.model.DModuleView
import org.jetbrains.dokka.plugability.DokkaPlugin

abstract class AbstractModelTest(val path: String? = null, val pkg: String) : ModelDSL(), AssertDSL {

    fun inlineModelTest(
        query: String,
        platform: String = "jvm",
        targetList: List<String> = listOf("jvm"),
        prependPackage: Boolean = true,
        cleanupOutput: Boolean = true,
        pluginsOverrides: List<DokkaPlugin> = emptyList(),
        configuration: DokkaConfigurationImpl? = null,
        block: DModuleView.() -> Unit
    ) {
        val testConfiguration = configuration ?: dokkaConfiguration {
            passes {
                pass {
                    sourceRoots = listOf("src/")
                    analysisPlatform = platform
                    targets = targetList
                }
            }
        }
        val prepend = path.let { p -> p?.let { "|$it\n" } ?: "" } + if (prependPackage) "|package $pkg" else ""

        testInline(
            query = ("$prepend\n$query").trim().trimIndent(),
            configuration = testConfiguration,
            cleanupOutput = cleanupOutput,
            pluginOverrides = pluginsOverrides
        ) {
            documentablesTransformationStage = block
        }
    }
}
