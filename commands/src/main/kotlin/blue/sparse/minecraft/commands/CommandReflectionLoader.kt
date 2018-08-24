package blue.sparse.minecraft.commands

import blue.sparse.minecraft.commands.parsing.CharIterator
import blue.sparse.minecraft.commands.parsing.Parser
import blue.sparse.minecraft.core.extensions.sendMessage
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

object CommandReflectionLoader {

	fun loadBukkitCommands(
			plugin: Plugin,
			clazz: KClass<*>,
			parent: Any? = clazz.objectInstance
	): Collection<BukkitCommand> {

		return (generateIntermediates(plugin, parent, clazz)).map(this::createBukkitCommand)
	}

	private fun createBukkitCommand(command: IntermediateCommand): BukkitCommand {
		return object: BukkitCommand(
				command.primaryName,
				"",
				"",
				command.aliases
		) {
			override fun execute(sender: CommandSender, alias: String?, args: Array<out String>): Boolean {
				val result = command.execute(sender, args.joinToString(" "))
				when (result) {
					is ExecuteResult.Success -> return true
					is ExecuteResult.FailedError -> {
						sender.sendMessage(ChatColor.RED, "An internal error occurred while attempting to execute this command.")
					}
					is ExecuteResult.FailedNoMatch -> {
						if(result.parseFails.size == 1) {
							sender.sendMessage(ChatColor.RED, "Invalid usage.")
						}else{
							sender.sendMessage(ChatColor.RED, "Invalid usage (overloads).")
						}
						for(error in result.parseFails) {
							sender.sendMessage(error.signature)
						}
					}
					is ExecuteResult.FailedParse -> {
						sender.sendMessage(ChatColor.RED, "Invalid usage.")
						sender.sendMessage(result.fail.signature)
					}
					is ExecuteResult.FailedNoSubcommand -> {
						sender.sendMessage(ChatColor.RED, "Use a subcommand.")
					}
				}

				return true
			}

			//TODO: autogenerate tab completions
//			override fun tabComplete(sender: CommandSender, alias: String?, args: Array<out String>): MutableList<String> {
//			}
		}
	}

	private fun generateIntermediates(plugin: Plugin, parent: Any?, clazz: KClass<*>): List<IntermediateCommand> {
		val overloads = groupOverloadedSingles(generateIntermediateSingles(plugin, parent, clazz))
		val groups = generateIntermediateGroups(plugin, parent, clazz)

		return overloads + groups
	}

	private fun generateIntermediateGroups(plugin: Plugin, parent: Any?, clazz: KClass<*>): List<IntermediateCommandGroup> {
		val groupParents = clazz.nestedClasses.filter {
			it.isSubclassOf(CommandGroup::class)
		}.map {
			it.objectInstance
			val obj = it.objectInstance
			if(obj != null) {
				obj
			} else {
				val constructor = it.constructors.first()
				if (it.isInner)
					constructor.call(parent)
				else
					constructor.call()
			}
		}

		return groupParents.map {
			val data = getAnnotationData(plugin, it.javaClass.simpleName, it.javaClass.kotlin)
			val intermediates = generateIntermediates(plugin, it, it::class)
			IntermediateCommandGroup(data, intermediates, null)
		}
	}

	private fun groupOverloadedSingles(singles: Collection<IntermediateSingleCommand>): Collection<IntermediateCommandOverloads> {
		val uniqueNames = singles.flatMap { it.names }
		return uniqueNames.map { name ->
			IntermediateCommandOverloads(name, singles.filter { name in it.names })
		}
	}

	private fun generateIntermediateSingles(plugin: Plugin, parent: Any?, clazz: KClass<*>): Collection<IntermediateSingleCommand> {
		return clazz.declaredMemberExtensionFunctions.mapNotNull {
			generateIntermediate(plugin, parent, it)
		}
	}

	private fun generateIntermediate(plugin: Plugin, parent: Any?, function: KFunction<*>): IntermediateSingleCommand? {
		if (function.extensionReceiverParameter?.type?.jvmErasure != Execute::class)
			return null

		val data = getAnnotationData(plugin, function.name, function)
//		val types = function.valueParameters.map {
//			it.name!! to it.type
//		}.toMap()

		return IntermediateSingleCommand(parent, data, Signature(function.valueParameters), function)
	}

	private fun getAnnotationData(plugin: Plugin, originalName: String, element: KAnnotatedElement): Command {
		val name = element.findAnnotation<Command.Name>()?.name ?: originalName
		val aliases = element.findAnnotation<Command.Aliases>()?.aliases?.toList() ?: emptyList()
		val description = element.findAnnotation<Command.Description>()?.description ?: ""
		val permission = element.findAnnotation<Command.Permission>()?.permission

		val default = element.findAnnotation<Command.Default>() != null

		return Command(plugin, name, aliases, description, permission, default)
	}

	data class IntermediateCommandGroup(
			val command: Command,
			val commands: Collection<IntermediateCommand>,
			val default: IntermediateCommand?
	) : IntermediateCommand {

		override val names: List<String>
			get() = command.aliases + command.name

		override fun execute(sender: CommandSender, raw: String): ExecuteResult {
			val targetName = raw.takeWhile { it != ' ' }
			val extra = raw.removePrefix(targetName).trim()

			return if (targetName.isBlank()) {
				default?.execute(sender, extra) ?: ExecuteResult.FailedNoSubcommand
			} else {
				val target = commands.find {
					it.names.any { name -> targetName.equals(name, true)}
				}
				target?.execute(sender, extra) ?: ExecuteResult.FailedNoSubcommand
			}
		}
	}

	/**
	 * This class represents multiple command functions
	 * in the same scope with the same name but differing parameters and annotation data.
	 *
	 * @Command.Permission("command.hello")
	 * fun Execute.hello(player: Player) {}
	 *
	 * @Command.Permission("command.hello")
	 * fun Execute.hello(number: Int) {}
	 *
	 * @Command.Permission("command.hello")
	 * @Command.Name("hello")
	 * fun Execute.greet(username: String) {}
	 */
	data class IntermediateCommandOverloads(
			val name: String,
			val commands: Collection<IntermediateSingleCommand>
	) : IntermediateCommand {

		override val names = listOf(name)

		override fun execute(sender: CommandSender, raw: String): ExecuteResult {
			val parsed = commands
					.map { it to it.signature.parse(raw) }

			val matches = parsed
					.filter { it.second is Signature.ParseResult.Success }
					.map { it.first to (it.second as Signature.ParseResult.Success) }

			if (matches.isEmpty())
				return ExecuteResult.FailedNoMatch(parsed.map { it.second as Signature.ParseResult.Fail })

			val (command, parseSuccess) = matches.minBy { it.second.extra.length }!!
			return command.execute(sender, parseSuccess)
		}
	}

	/**
	 * This class represents a single command function with annotation data and parameters
	 *
	 * @Command.Permission("command.hello")
	 * fun Execute.hello(player: Player) {}
	 */
	data class IntermediateSingleCommand(
			val parent: Any?,
			val command: Command,
			val signature: Signature,
			val function: KFunction<*>
	) : IntermediateCommand {

		override val names: List<String>
			get() = command.aliases + command.name

		override fun execute(sender: CommandSender, raw: String): ExecuteResult {
			val args = signature.parse(raw)
			return when (args) {
				is Signature.ParseResult.Fail -> ExecuteResult.FailedParse(args)
				is Signature.ParseResult.Success -> execute(sender, args)
			}
		}

		fun execute(sender: CommandSender, args: Signature.ParseResult.Success): ExecuteResult {
			if (args.signature != signature)
				throw IllegalArgumentException("Command arguments provided with wrong signature")

			val params = HashMap<KParameter, Any?>()
			function.instanceParameter?.let { params[it] = parent }

			val context = Execute(command.name, command.plugin, sender, args.raw.split(' ').toTypedArray())
			params[function.extensionReceiverParameter!!] = context

			params += args.values
//			params += function.parameters.map {
//				it to args.values[it.name]
//			}.filter { it.second != null }

			try {
				function.callBy(params)
			} catch (t: Throwable) {
				return ExecuteResult.FailedError(t)
			}

			return ExecuteResult.Success(args.extra)
		}

	}

	interface IntermediateCommand {
		val primaryName get() = names.first()
		val aliases get() = names - primaryName
		val names: List<String>

		fun execute(sender: CommandSender, raw: String): ExecuteResult
	}

	sealed class ExecuteResult {
		data class FailedParse(val fail: Signature.ParseResult.Fail) : ExecuteResult()
		data class FailedError(val error: Throwable) : ExecuteResult()
		data class FailedNoMatch(val parseFails: Collection<Signature.ParseResult.Fail>) : ExecuteResult()
		object FailedNoSubcommand : ExecuteResult()
		data class Success(val extra: String) : ExecuteResult()
	}

	data class Signature(val params: List<KParameter>) {

		fun parse(raw: String): ParseResult {
			val iterator = CharIterator(raw)
			val result = HashMap<KParameter, Any?>()

			for (param in params) {
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
							return ParseResult.Fail(this, param.name!!)
//							return Right(param)
					}

					result[param] = parsed
				}

				iterator.takeWhile(Char::isWhitespace)
			}
			return ParseResult.Success(this, raw, result, iterator.takeWhile { true })

//			val result = LinkedHashMap<String, Any?>()
//			for((name, type) in types) {
//
//			}
//			return ParseResult.Success(this, raw, emptyMap(), raw) //TODO
		}

		override fun toString(): String {
			val args = StringBuilder()

			for (param in params) {
//				if (param == failedAt) {
//					args.append(ChatColor.RED.toString())
//				} else {
//					args.append(ChatColor.LIGHT_PURPLE.toString())
//				}

				val optional = param.isOptional || param.type.isMarkedNullable || param.isVararg
				if (optional) args.append('[') else args.append('<')
				args.append(param.name)
				if (param.isVararg)
					args.append("...")
				if (optional) args.append(']') else args.append('>')

				args.append(' ')
			}

			return args.toString()

//			context.replyRaw(ChatColor.GRAY, ChatColor.BOLD, "Usage: ", ChatColor.LIGHT_PURPLE, '/', context.commandName, ' ', args)
//			context.replyRaw(ChatColor.GRAY, ChatColor.BOLD, "Expected: ", ChatColor.RED, failedAt.name!!)
		}

		fun errorString(failedAt: String): String {
			return toString()
		}

		sealed class ParseResult(val signature: Signature) {
			class Success(
					signature: Signature,
					val raw: String,
					val values: Map<KParameter, Any?>,
					val extra: String
			) : ParseResult(signature)

			class Fail(
					signature: Signature,
					val failedAtName: String
			) : ParseResult(signature)
		}

	}
}