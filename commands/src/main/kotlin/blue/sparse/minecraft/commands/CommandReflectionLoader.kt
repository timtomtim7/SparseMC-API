package blue.sparse.minecraft.commands

import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

object CommandReflectionLoader {

	fun scan(plugin: Plugin, clazz: KClass<*>): Set<org.bukkit.command.Command> {
		val holder = clazz.objectInstance ?: return emptySet()

		val functions = clazz.declaredMemberExtensionFunctions
		val executeFunctions = functions.filter {
			it.extensionReceiverParameter?.type?.jvmErasure == Execute::class
		}

		val tabCompleteFunctions = functions.filter {
			it.extensionReceiverParameter?.type?.jvmErasure == Execute::class &&
					it.returnType.jvmErasure.isSubclassOf(Collection::class)
		}

		val matched = executeFunctions.map { exe ->
			exe to tabCompleteFunctions.find { tab ->
				exe.name == tab.name && exe.valueParameters == tab.valueParameters
			}
		}.toMap()

		val result = HashSet<org.bukkit.command.Command>()

		for ((exe, tab) in matched) {
			val data = getAnnotationData(plugin, exe.name, exe)

			result.add(createBukkitCommand(holder, data, exe, tab))
		}

//		val groupClasses = clazz.nestedClasses.filter {
//			it.objectInstance != null && it.isSubclassOf(CommandGroup::class)
//		}

		return result
	}

	private fun createBukkitCommand(holder: Any, data: Command, exe: KFunction<*>, tab: KFunction<*>?): org.bukkit.command.Command {
		return object : org.bukkit.command.Command(
				data.name,
				data.description,
				data.usage,
				data.aliases
		) {
			override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
				val context = Execute(data, sender)
				exe.call(holder, context)
				return true
			}

			override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
				if (tab == null)
					return ArrayList()
				val context = TabComplete(data, sender)
				//TODO: Make more efficient?
				return (tab.call(holder, context) as Collection<*>).mapTo(ArrayList(), Any?::toString)
			}
		}
	}

	private fun getAnnotationData(plugin: Plugin, originalName: String, element: KAnnotatedElement): Command {

		val name = element.findAnnotation<Command.Name>()?.name ?: originalName

		val aliases = element.findAnnotation<Command.Aliases>()?.aliases?.toList() ?: emptyList()

		val description = element.findAnnotation<Command.Description>()?.description ?: ""

		val usage = element.findAnnotation<Command.Usage>()?.usage ?: "/$name"

//		val default = element.findAnnotation<Command.Default>() != null

		return Command(plugin, name, aliases, description, usage)

	}

//	data class CommandData(
//			val name: String,
//			val aliases: List<String>,
//			val description: String,
//			val usage: String,
//			val default: Boolean
//	)

}