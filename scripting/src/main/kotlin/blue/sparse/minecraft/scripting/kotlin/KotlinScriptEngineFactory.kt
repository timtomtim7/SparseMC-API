package blue.sparse.minecraft.scripting.kotlin

import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import java.io.File

class KotlinScriptEngineFactory(
		val templateClassName: String = KotlinScriptEngine.BLANK_TEMPLATE,
		classpath: List<File> = emptyList(),
		appendCurrentClasspath: Boolean = true,
		private val disposable: Disposable = Disposer.newDisposable()
): KotlinJsr223JvmScriptEngineFactoryBase()
{
	private val classpath = classpath + if(appendCurrentClasspath) currentClasspath() else emptyList()

	override fun getScriptEngine() = KotlinScriptEngine(
			this,
			classpath,
			templateClassName,
			disposable
	)
}