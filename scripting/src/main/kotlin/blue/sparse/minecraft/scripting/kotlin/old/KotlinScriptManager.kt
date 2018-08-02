package blue.sparse.minecraft.scripting.kotlin.old

import blue.sparse.minecraft.scripting.ScriptingModule
import blue.sparse.minecraft.scripting.kotlin.JBCompiledScript
import java.io.File
import javax.script.ScriptContext
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class KotlinScriptManager<T : Any>(
		val template: KClass<T>,
		val maxCacheSize: Int = Int.MAX_VALUE,
		classpath: List<File> = emptyList(),
		appendCurrentClasspath: Boolean = true,
		parentClassLoader: ClassLoader = KotlinScriptClassLoader::class.java.classLoader
) {
//	private val classDirectory = Files.createTempDirectory("kotlin-scripts").toFile()

	private val engine = KotlinScriptEngineFactory(template.java.name, classpath/* + classDirectory*/, appendCurrentClasspath).scriptEngine
	private var classLoader = KotlinScriptClassLoader(parentClassLoader)

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
		val cacheFolder = getCacheFolder(source)
		if(cache) {
			if(cacheFolder.exists()) {
				try {
					val clazz = classLoader.loadCachedScript(cacheFolder).kotlin
					val script = CompiledScript(clazz as KClass<T>)
					if (cache)
						cache(CacheKey.Source(source), script)

					return script
				}catch(t: Throwable) {
					cacheFolder.deleteRecursively()
				}
			}
		}

		engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE)

		val compiled = classLoader.compiledScriptToClass(
				engine.compile(source) as JBCompiledScript,
				className,
				cacheFolder
		).kotlin

		if (!template.isSuperclassOf(compiled))
			throw IllegalStateException("Compiled class was not an instance of the template, this should not happen.")

		val script = CompiledScript(compiled as KClass<T>)
		if (cache)
			cache(CacheKey.Source(source), script)

		return script
	}

	fun compile(file: File, cache: Boolean = true, className: String? = null): CompiledScript<T> {
		//TODO: I changed `cache` from `false` to the actual variable
		// This gets rid of the use of CacheKey.File, but it may not be desired
		val script = compile(file.readText(), cache, className)
//		if (cache)
//			cache(CacheKey.File(file), script)

		return script
	}

	fun run(file: File, className: String? = null, vararg args: Any?): CompiledScript<T>.Result {
		return getOrCompile(file, className).invoke(*args)
	}

	fun run(source: String, className: String? = null, vararg args: Any?): CompiledScript<T>.Result {
		return getOrCompile(source, className).invoke(*args)
	}

	private fun getCacheFolder(source: String): File {
		val hash = hashSHA1(source)
				.replace('/', '.')
		val rootFolder = ScriptingModule.cacheFolder
		return File(rootFolder, hash)
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
