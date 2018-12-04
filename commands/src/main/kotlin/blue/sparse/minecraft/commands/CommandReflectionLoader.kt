package blue.sparse.minecraft.commands

import blue.sparse.minecraft.commands.parsing.CharIterator
import blue.sparse.minecraft.commands.parsing.Parser
import blue.sparse.minecraft.core.extensions.getPluginLocale
import blue.sparse.minecraft.core.extensions.sendMessage
import blue.sparse.minecraft.core.i18n.PluginLocale
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible
import org.bukkit.plugin.Plugin
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

object CommandReflectionLoader {

	fun loadBukkitCommands(
			plugin: Plugin,
			clazz: KClass<*>,
			parent: Any? = clazz.objectInstance
	): Collection<BukkitCommand> {
		return (generateIntermediates(null, plugin, parent, clazz)).map { createBukkitCommand(plugin, it) }
	}

	private fun createBukkitCommand(plugin: Plugin, command: IntermediateCommand): BukkitCommand {
		return object : BukkitCommand(
				command.primaryName,
				"",
				"",
				command.aliases
		) {
			override fun execute(sender: CommandSender, alias: String?, args: Array<out String>): Boolean {
				val result = command.execute(sender, args.joinToString(" "))
				val locale = (sender as? Player)?.getPluginLocale(plugin) ?: PluginLocale.default(plugin)

				when (result) {
					is ExecuteResult.Success -> return true
					is ExecuteResult.FailedError -> {
						val message = locale["error.command.${command.primaryName}.internalError"]
								?: "${ChatColor.RED}An internal error occurred while attempting to execute this command."
						sender.sendMessage(message)
						result.error.printStackTrace()
					}
					is ExecuteResult.FailedNoMatch -> {
						for ((overload, error) in result.parseFails) {
							val message = locale["error.command.${command.primaryName}.${error.failedAtName}"]
							if (message != null) {
								sender.sendMessage(message)
							} else {
								val usage = error.signature.errorString(error.failedAtName)
								sender.sendMessage(
										ChatColor.DARK_RED, ChatColor.BOLD, "Usage: ",
										ChatColor.GREEN, '/', overload.fullName, ' ', usage
								)
							}
						}
					}
					is ExecuteResult.FailedParse -> {
						val message = locale["error.command.${command.primaryName}.${result.fail.failedAtName}"]
						if (message != null) {
							sender.sendMessage(message)
						} else {
							val usage = result.fail.signature.errorString(result.fail.failedAtName)
							sender.sendMessage(
									ChatColor.DARK_RED, ChatColor.BOLD, "Usage: ",
									ChatColor.GREEN, '/', command.fullName, ' ', usage
							)
						}
					}
					is ExecuteResult.FailedNoSubcommand -> {
						val group = result.group
						val message = locale["error.command.${command.primaryName}.help"]
						if(message != null) {
							sender.sendMessage(message)
						}else {
							sender.sendMessage(ChatColor.DARK_RED, ChatColor.BOLD, "Commands:")

							for (subcommand in group.commands) {
								sender.sendMessage(ChatColor.RED, '/', subcommand.fullName)
							}
						}
					}
					is ExecuteResult.FailedNoPermission -> {
						val message = locale["error.command.${command.primaryName}.internalError"]
								?: "${ChatColor.RED}You do not have permission to run that command."
						sender.sendMessage(message)
					}
				}

				return true
			}

			//TODO: autogenerate tab completions
//			override fun tabComplete(sender: CommandSender, alias: String?, args: Array<out String>): MutableList<String> {
//			}
		}
	}

	private fun generateIntermediates(parentCommand: IntermediateCommandGroup?, plugin: Plugin, parent: Any?, clazz: KClass<*>): List<IntermediateCommand> {
		val overloads = groupOverloadedSingles(parentCommand, generateIntermediateSingles(parentCommand, plugin, parent, clazz))
		val groups = generateIntermediateGroups(parentCommand, plugin, parent, clazz)

		return overloads + groups
	}

	private fun generateIntermediateGroups(parentCommand: IntermediateCommandGroup?, plugin: Plugin, parent: Any?, clazz: KClass<*>): List<IntermediateCommandGroup> {
		val groupParents = clazz.nestedClasses.filter {
			it.isSubclassOf(CommandGroup::class)
		}.map {
			it.objectInstance
			val obj = it.objectInstance
			if (obj != null) {
				obj
			} else {
				val constructor = it.constructors.first()
				if (it.isInner)
					constructor.call(parent)
				else
					constructor.call()
			}
		}

		return groupParents.map { groupParent ->
			val data = getAnnotationData(plugin, groupParent.javaClass.simpleName, groupParent.javaClass.kotlin)
			val intermediates = ArrayList<IntermediateCommand>()
			val group = IntermediateCommandGroup(parent, data, intermediates, null, parentCommand)

			intermediates.addAll(generateIntermediates(group, plugin, groupParent, groupParent::class))
			val default: IntermediateCommand? = intermediates
					.asSequence()
					.filterIsInstance<IntermediateCommandGroup>()
					.find { it.command.isDefaultOfGroup }
					?: intermediates
							.filterIsInstance<IntermediateCommandOverloads>()
							.flatMap { it.commands }
							.find { it.command.isDefaultOfGroup }
			group.default = default

			group
		}
	}

	private fun groupOverloadedSingles(parentCommand: IntermediateCommandGroup?, singles: Collection<IntermediateSingleCommand>): Collection<IntermediateCommand> {
		val uniqueNames = singles.flatMap { it.names }
		return uniqueNames.map { name ->
			IntermediateCommandOverloads(name, singles.filter { name in it.names }, parentCommand)
		}
	}

	private fun generateIntermediateSingles(parentCommand: IntermediateCommandGroup?, plugin: Plugin, parent: Any?, clazz: KClass<*>): Collection<IntermediateSingleCommand> {
		return clazz.declaredMemberExtensionFunctions.mapNotNull {
			generateIntermediate(parentCommand, plugin, parent, it)
		}
	}

	private fun generateIntermediate(parentCommand: IntermediateCommandGroup?, plugin: Plugin, parent: Any?, function: KFunction<*>): IntermediateSingleCommand? {
		if (function.extensionReceiverParameter?.type?.jvmErasure != Execute::class)
			return null

		val data = getAnnotationData(plugin, function.name, function)

		return IntermediateSingleCommand(parent, data, Signature(function.valueParameters), function, parentCommand)
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

	data class IntermediateCommandGroup(
			val parent: Any?,
			val command: Command,
			val commands: Collection<IntermediateCommand>,
			var default: IntermediateCommand?,
			override var parentCommand: IntermediateCommandGroup?
	) : IntermediateCommand {

		override val names: List<String>
			get() = mutableListOf(command.name).apply { addAll(command.aliases) }

		fun hasPermission(sender: Permissible): Boolean {
			return command.permission == null || sender.hasPermission(command.permission)
		}

		override fun execute(sender: CommandSender, raw: String): ExecuteResult {
			if (!hasPermission(sender))
				return ExecuteResult.FailedNoPermission(command.permission!!)

			val targetName = raw.takeWhile { it != ' ' }
			val extra = raw.removePrefix(targetName).trim()

			return if (targetName.isBlank()) {
				default?.execute(sender, extra) ?: ExecuteResult.FailedNoSubcommand(this)
			} else {
				val target = commands.find {
					it.names.any { name -> targetName.equals(name, true) }
				}
				target?.execute(sender, extra) ?: ExecuteResult.FailedNoSubcommand(this)
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
			val commands: Collection<IntermediateSingleCommand>,
			override var parentCommand: IntermediateCommandGroup?
	) : IntermediateCommand {

		override val names = listOf(name)

		override fun execute(sender: CommandSender, raw: String): ExecuteResult {
			val parsed = commands
					.map { it to it.signature.parse(raw) }

			val matches = parsed
					.asSequence()
					.filter { it.second is Signature.ParseResult.Success }
					.filter { it.first.hasPermission(sender) }
					.map { it.first to (it.second as Signature.ParseResult.Success) }
					.toList()

			if (matches.isEmpty())
				return ExecuteResult.FailedNoMatch(parsed.map { it.first to it.second as Signature.ParseResult.Fail }.toMap())

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
			val function: KFunction<*>,
			override var parentCommand: IntermediateCommandGroup?
	) : IntermediateCommand {

		override val names: List<String>
			get() = mutableListOf(command.name).apply { addAll(command.aliases) }

		override fun execute(sender: CommandSender, raw: String): ExecuteResult {
			val args = signature.parse(raw)
			return when (args) {
				is Signature.ParseResult.Fail -> ExecuteResult.FailedParse(args)
				is Signature.ParseResult.Success -> execute(sender, args)
			}
		}

		fun hasPermission(sender: Permissible): Boolean {
			return command.permission == null || sender.hasPermission(command.permission)
		}

		fun execute(sender: CommandSender, args: Signature.ParseResult.Success): ExecuteResult {
			if (args.signature != signature)
				throw IllegalArgumentException("Command arguments provided with wrong signature")

			if (!hasPermission(sender))
				return ExecuteResult.FailedNoPermission(command.permission!!)

			val params = HashMap<KParameter, Any?>()
			function.instanceParameter?.let { params[it] = parent }

			val context = Execute(command.name, command.plugin, sender, args.raw.split(' ').toTypedArray())
			params[function.extensionReceiverParameter!!] = context

			params += args.values
			try {
				function.callBy(params)
			} catch (t: Throwable) {
				return if (t is ContextEscape || (t is InvocationTargetException && t.targetException is ContextEscape)) {
					ExecuteResult.Success(args.extra)
				} else {
					ExecuteResult.FailedError(t)
				}
			}

			return ExecuteResult.Success(args.extra)
		}

	}

	interface IntermediateCommand {
		val primaryName get() = names.first()
		val aliases get() = names - primaryName
		val names: List<String>

		var parentCommand: IntermediateCommandGroup?

		val fullName: String
			get() {
				return buildString {
					val parent = parentCommand
					if (parent != null) {
						append(parent.fullName)
						append(' ')
					}
					append(primaryName)
				}
			}

		fun execute(sender: CommandSender, raw: String): ExecuteResult
	}

	sealed class ExecuteResult {
		data class FailedParse(val fail: Signature.ParseResult.Fail) : ExecuteResult()
		data class FailedError(val error: Throwable) : ExecuteResult()
		data class FailedNoMatch(val parseFails: Map<IntermediateCommand, Signature.ParseResult.Fail>) : ExecuteResult()
		data class FailedNoSubcommand(val group: IntermediateCommandGroup) : ExecuteResult()
		data class FailedNoPermission(val permission: String) : ExecuteResult()
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
				} else {
					val parsed = Parser.parse(param.type, iterator)
					if (parsed == null) {
						if (param.isOptional)
							continue
						if (!param.type.isMarkedNullable)
							return ParseResult.Fail(this, param.name!!)
					}

					result[param] = parsed
				}

				iterator.takeWhile(Char::isWhitespace)
			}
			return ParseResult.Success(this, raw, result, iterator.takeWhile { true })
		}

		override fun toString(): String {
			val args = StringBuilder()

			for (param in params) {

				val optional = param.isOptional || param.type.isMarkedNullable || param.isVararg
				if (optional) args.append('[') else args.append('<')
				args.append(param.name)
				if (param.isVararg)
					args.append("...")
				if (optional) args.append(']') else args.append('>')

				args.append(' ')
			}

			return args.toString()
		}

		fun errorString(failedAt: String): String {
			val args = StringBuilder()

			var foundFail = false
			for (param in params) {
				when {
					param.name == failedAt -> {
						foundFail = true
						args.append(ChatColor.RED.toString())
						args.append(ChatColor.BOLD.toString())
					}
					foundFail -> args.append(ChatColor.RED.toString())
					else -> args.append(ChatColor.GREEN.toString())
				}

				val optional = param.isOptional || param.type.isMarkedNullable || param.isVararg
				if (optional) args.append('[') else args.append('<')
				args.append(param.name)
				if (param.isVararg)
					args.append("...")
				if (optional) args.append(']') else args.append('>')

				args.append(ChatColor.RED.toString())
				args.append(' ')
			}

			return args.toString()
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