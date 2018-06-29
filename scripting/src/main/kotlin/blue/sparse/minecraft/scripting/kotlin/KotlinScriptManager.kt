package blue.sparse.minecraft.scripting.kotlin

import java.io.File
import javax.script.ScriptContext
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class KotlinScriptManager<T : Any>(
		val template: KClass<T>,
		val maxCacheSize: Int = Int.MAX_VALUE,
		classpath: List<File> = emptyList(),
		appendCurrentClasspath: Boolean = true
) {
//	private val classDirectory = Files.createTempDirectory("kotlin-scripts").toFile()

	private val engine = KotlinScriptEngineFactory(template.java.name, classpath/* + classDirectory*/, appendCurrentClasspath).scriptEngine
	private var classLoader = KotlinScriptClassLoader()

	private var cache = HashMap<CacheKey, CompiledScript<T>>()

	operator fun get(source: String) = cache[CacheKey.Source(source)]
	operator fun get(file: File): CompiledScript<T>? = cache[CacheKey.File(file)]

	fun getOrCompile(source: String, className: String? = null): CompiledScript<T> {
		return get(source) ?: compile(source, true, className)
	}

	fun getOrCompile(file: File, className: String? = null): CompiledScript<T> {
		val cached = get(file) ?: return compile(file, true, className)
		if (cached.time < file.lastModified())
			return compile(file, true)
		return cached
	}

	@Suppress("UNCHECKED_CAST")
	fun compile(source: String, cache: Boolean = true, className: String? = null): CompiledScript<T> {
		engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE)

		val compiled = classLoader.compiledScriptToClass(engine.compile(source) as JBCompiledScript, className/*, classDirectory*/).kotlin

		if (!template.isSuperclassOf(compiled))
			throw IllegalStateException("Compiled class was not an instance of the template, this should not happen.")

		val script = CompiledScript(compiled as KClass<T>)
		if (cache)
			cache(CacheKey.Source(source), script)

		return script
	}

	fun compile(file: File, cache: Boolean = true, className: String? = null): CompiledScript<T> {
		val script = compile(file.readText(), false, className)
		if (cache)
			cache(CacheKey.File(file), script)

		return script
	}

	fun run(file: File, className: String? = null, vararg args: Any?): CompiledScript<T>.Result {
		return getOrCompile(file, className).invoke(*args)
	}

	fun run(source: String, className: String? = null, vararg args: Any?): CompiledScript<T>.Result {
		return getOrCompile(source, className).invoke(*args)
	}

	private fun cache(key: CacheKey, script: CompiledScript<T>) {
		while (cache.size >= maxCacheSize)
			cache.keys.remove(cache.keys.first())
		cache[key] = script
	}

	internal sealed class CacheKey {
		internal data class Source(val source: String) : CacheKey()
		internal data class File(val file: java.io.File) : CacheKey()
	}
}
