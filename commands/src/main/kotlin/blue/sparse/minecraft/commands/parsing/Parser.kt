package blue.sparse.minecraft.commands.parsing

import blue.sparse.minecraft.util.tryOrNull
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure
import kotlin.system.measureTimeMillis

interface Parser {

	val clazz: KClass<*>

	fun canParseTo(target: KType): Boolean {
//		val primitive = Primitives.wrap(target.jvmErasure.java).kotlin
		return canParseTo(target.jvmErasure)
	}

	fun canParseTo(target: KClass<*>): Boolean {
		return target.isSubclassOf(clazz)
	}

	fun parse(target: KType, iterator: CharIterator): Any {
		return parse(target.jvmErasure, iterator)
	}

	fun parse(target: KClass<*>, iterator: CharIterator): Any

	companion object {

		private val pluginRegistered = HashMap<Plugin, MutableSet<Parser>>()
		private val parsers = LinkedHashSet<Parser>()

		fun register(plugin: Plugin, parser: Parser): Boolean {
			if (!parsers.add(parser))
				return false

			val pluginParsers = pluginRegistered.getOrPut(plugin, ::HashSet)
			if (!pluginParsers.add(parser)) {
				parsers.remove(parser)
				return false
			}

			return true
		}

		fun registerAll(plugin: Plugin, vararg parsers: Parser) {
			for (parser in parsers)
				register(plugin, parser)
		}

		fun unregisterAll(plugin: Plugin) {
			parsers.removeAll(pluginRegistered.remove(plugin) ?: return)
		}

		fun findParser(goal: KType): Parser? {
			return parsers.find { it.canParseTo(goal) }
		}

		fun findParser(goal: KClass<*>): Parser? {
//			val primitive = Primitives.wrap(goal.java).kotlin
			return parsers.find { it.canParseTo(goal) }
		}

		fun parse(goal: KType, input: CharIterator): Any? {
			var result: Any? = null
			val parser = findParser(goal) ?: return null
			val ms = measureTimeMillis {
				val index = input.index

				result = tryOrNull { parser.parse(goal, input) }
				if(result == null)
					input.index = index
			}

//			println("Parser $parser took ${ms}ms")

			return result
		}

		fun parse(goal: KClass<*>, input: CharIterator): Any? {
			val parser = findParser(goal) ?: return null
			val index = input.index

			val result = tryOrNull { parser.parse(goal, input) }
			if(result == null)
				input.index = index

			return result
		}

		inline fun <reified T : Any> of(crossinline body: (CharIterator) -> T): Parser {
			return object : Parser {
				override val clazz = T::class

				override fun parse(target: KClass<*>, iterator: CharIterator): T {
					return body(iterator)
				}
			}
		}

		inline fun <reified T : Any> of(crossinline filter: (Char) -> Boolean, crossinline body: (String) -> T): Parser {
			return of { body(it.takeWhile(filter).takeIf(String::isNotBlank)!!) }
		}

	}

}