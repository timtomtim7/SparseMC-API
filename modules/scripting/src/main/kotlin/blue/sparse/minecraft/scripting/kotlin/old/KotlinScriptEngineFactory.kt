package blue.sparse.minecraft.scripting.kotlin.old

import blue.sparse.minecraft.scripting.kotlin.currentClasspath
import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
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