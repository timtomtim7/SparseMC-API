package blue.sparse.minecraft.scripting.kotlin.old

import com.intellij.openapi.Disposable
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.common.repl.*
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.cli.jvm.repl.GenericReplCompiler
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import org.jetbrains.kotlin.script.KotlinScriptDefinitionFromAnnotatedTemplate
import org.jetbrains.kotlin.utils.PathUtil
import java.io.File
import java.net.URLClassLoader
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.script.ScriptContext
import javax.script.ScriptEngineFactory

class KotlinScriptEngine(
		factory: ScriptEngineFactory,
		val templateClasspath: List<File>,
		templateClassName: String,
		disposable: Disposable
) : KotlinJsr223JvmScriptEngineBase(factory), KotlinJsr223JvmInvocableScriptEngine {

	override val replCompiler = GenericReplCompiler(
			disposable,
			makeScriptDefinition(templateClasspath, templateClassName),
			makeCompilerConfiguration(),
			PrintingMessageCollector(System.out, MessageRenderer.WITHOUT_PATHS, false)
	)

	private val localEvaluator = GenericReplCompilingEvaluator(
			replCompiler,
			templateClasspath,
			Thread.currentThread().contextClassLoader,
			null,
			ReplRepeatingMode.NONE
	)

	override val replEvaluator: ReplFullEvaluator get() = localEvaluator

	override val state: IReplStageState<*> get() = getCurrentState(getContext())

	override fun createState(lock: ReentrantReadWriteLock): IReplStageState<*> = replEvaluator.createState(lock)

	override fun overrideScriptArgs(context: ScriptContext): ScriptArgsWithTypes? = null//getScriptArgs(context, scriptArgsTypes)

	private fun makeScriptDefinition(templateClasspath: List<File>, templateClassName: String): KotlinScriptDefinition {
		val classloader = URLClassLoader(templateClasspath.map { it.toURI().toURL() }.toTypedArray(), this.javaClass.classLoader)
		val cls = classloader.loadClass(templateClassName)
//		return KotlinScriptDefinitionFromAnnotatedTemplate(cls.kotlin, null, null, emptyMap())
		return KotlinScriptDefinitionFromAnnotatedTemplate(cls.kotlin, null, emptyList())
	}

	private fun makeCompilerConfiguration() = CompilerConfiguration().apply {
		addJvmClasspathRoots(PathUtil.getJdkClassesRootsFromCurrentJre())
		addJvmClasspathRoots(templateClasspath)
		put(CommonConfigurationKeys.MODULE_NAME, "kotlin-script")
		languageVersionSettings = LanguageVersionSettingsImpl(
				LanguageVersion.LATEST_STABLE, ApiVersion.LATEST_STABLE, mapOf(AnalysisFlags.skipMetadataVersionCheck to true)
		)
	}

	init {
	}

	companion object {
		const val BLANK_TEMPLATE = "blue.sparse.minecraft.scripting.kotlin.BlankScriptTemplate"

		init {
			System.setProperty("idea.io.use.nio2", "true")
		}
	}
}