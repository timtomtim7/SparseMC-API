package blue.sparse.minecraft.commands

import blue.sparse.minecraft.commands.parsing.CharIterator
import blue.sparse.minecraft.commands.parsing.Parser
import blue.sparse.minecraft.core.extensions.color
import blue.sparse.minecraft.util.*
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

@Deprecated("Replacing")
object OldCommandReflectionLoader {

	fun scan(plugin: Plugin, clazz: KClass<*>): Set<org.bukkit.command.Command> {
		val holder = clazz.objectInstance ?: return emptySet()

		val functions = clazz.declaredMemberExtensionFunctions
		val executeFunctions = functions.filter {
			it.extensionReceiverParameter?.type?.jvmErasure == Execute::class
		}

		val tabCompleteFunctions = functions.filter {
			it.extensionReceiverParameter?.type?.jvmErasure == TabComplete::class &&
					it.returnType.jvmErasure.isSubclassOf(Collection::class)
		}

		val matched = executeFunctions.map { exe ->
			exe to tabCompleteFunctions.find { tab ->
				exe.name == tab.name /*&& exe.valueParameters == tab.valueParameters*/
			}
		}.toMap()

		if(holder is CommandGroup) {

		}else{
			val result = HashSet<org.bukkit.command.Command>()

			for ((exe, tab) in matched) {
				val data = getAnnotationData(plugin, exe.name, exe)

				result.add(createBukkitCommand(holder, data, exe, tab))
			}

			return result
		}

		val groupClasses = clazz.nestedClasses.filter {
			it.objectInstance != null && it.isSubclassOf(CommandGroup::class)
		}

		return groupClasses.flatMapTo(HashSet()) { scan(plugin, it) }
	}


	private fun createBukkitCommand(holder: Any, data: Command, exe: KFunction<*>, tab: KFunction<*>?): org.bukkit.command.Command {
		return object : org.bukkit.command.Command(
				data.name,
				data.description,
				"/${data.name} [args]",
				data.aliases
		) {
			override fun execute(sender: CommandSender, commandLabel: String, args: Array<out String>): Boolean {
				val context = Execute(data.name, data.plugin, sender, args)

				val permission = data.permission
				if(permission != null && !sender.hasPermission(permission)) {
					val message = context.locale["error.command.${data.name}.noPermission"] ?: color("&cYou do not have permission.")
					sender.sendMessage(message)
					return true
				}

				val parsedArgs = parseCommandArguments(exe.valueParameters, args.joinToString(" "))

				if (parsedArgs is Right) {
					val param = parsedArgs.right
					sendErrorMessage(context, exe.valueParameters, param)
				} else {
					val params = parsedArgs.left
					params[exe.instanceParameter!!] = holder
					params[exe.extensionReceiverParameter!!] = context

					try {
						exe.isAccessible = true
						exe.callBy(params)
					} catch (t: ContextEscape) {
					} catch (t: InvocationTargetException) {
						if (t.targetException !is ContextEscape)
							throw t
					}
				}

				return true
			}

			override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
				if (tab == null)
					return ArrayList()
				val context = TabComplete(data.name, data.plugin, sender, args)
				//TODO: Make more efficient?
				return (tab.call(holder, context) as Collection<*>).mapTo(ArrayList(), Any?::toString)
			}
		}
	}

	private fun typeToString(type: KType?): String {
		if (type == null)
			return "*"

		val result = StringBuilder()
		result.append(type.jvmErasure.simpleName)

		val arguments = type.arguments
		if (arguments.isNotEmpty()) {
			result.append('<')
			result.append(arguments.joinToString { typeToString(it.type) })
			result.append('>')
		}

		return result.toString()
	}

	private fun sendErrorMessage(context: Execute, params: List<KParameter>, failedAt: KParameter) {
		val message = context.locale["error.command.${context.commandName}.${failedAt.name}", emptyMap()]
		if (message != null) {
			context.replyRaw(message)
			return
		}

		val args = StringBuilder()

		for (param in params) {
			if (param == failedAt) {
				args.append(ChatColor.RED.toString())
			} else {
				args.append(ChatColor.LIGHT_PURPLE.toString())
			}

			val optional = param.isOptional || param.type.isMarkedNullable || param.isVararg
			if (optional) args.append('[') else args.append('<')
			args.append(param.name)
			if (param.isVararg)
				args.append("...")
			if (optional) args.append(']') else args.append('>')

			args.append(' ')
		}

		context.replyRaw(ChatColor.GRAY, ChatColor.BOLD, "Usage: ", ChatColor.LIGHT_PURPLE, '/', context.commandName, ' ', args)
		context.replyRaw(ChatColor.GRAY, ChatColor.BOLD, "Expected: ", ChatColor.RED, failedAt.name!!)
	}

	private fun parseCommandArguments(goal: List<KParameter>, input: String): Either<MutableMap<KParameter, Any?>, KParameter> {
		val iterator = CharIterator(input)

		val result = HashMap<KParameter, Any?>()

		for (param in goal) {
			if (param.isVararg) {
				val type = param.type.arguments.first().type!!
				var parsed = Parser.parse(type, iterator)

				val vararg = ArrayList<Any>()
				while (parsed != null) {
					vararg.add(parsed)
					iterator.takeWhile(Char::isWhitespace)
					parsed = Parser.parse(type, iterator)
				}

				val array = java.lang.reflect.Array.newInstance(type.jvmErasure.java, vararg.size)
				for ((i, value) in vararg.withIndex()) {
					java.lang.reflect.Array.set(array, i, value)
				}

				result[param] = array
//				result[param] = vararg.toArray()
			} else {
				val parsed = Parser.parse(param.type, iterator)
				if (parsed == null) {
					if (param.isOptional)
						continue
					if (!param.type.isMarkedNullable)
						return Right(param)
				}

				result[param] = parsed
			}

			iterator.takeWhile(Char::isWhitespace)
		}

		if (iterator.hasNext()) {
			println("Command arguments \"${iterator.source}\" had trailing (unused): \"${iterator.takeWhile { true }}\"")
		}

		return Left(result)
	}

	private fun getAnnotationData(plugin: Plugin, originalName: String, element: KAnnotatedElement): Command {
		val name = element.findAnnotation<Command.Name>()?.name ?: originalName
		val aliases = element.findAnnotation<Command.Aliases>()?.aliases?.toList() ?: emptyList()
		val description = element.findAnnotation<Command.Description>()?.description ?: ""
		val permission = element.findAnnotation<Command.Permission>()?.permission
		val overwrite = element.findAnnotation<Command.Addon>() != null
		val default = element.findAnnotation<Command.Default>() != null

		return Command(plugin, name, aliases, description, permission, default, overwrite)
	}

}