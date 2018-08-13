package blue.sparse.minecraft.commands

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
	): Set<BukkitCommand> {
		generateIntermediateSingles(plugin, parent, clazz)
		return emptySet()
	}

	private fun groupOverloadedSingles(singles: Collection<IntermediateSingleCommand>): Collection<IntermediateCommandOverloads> {
		val uniqueNames = singles.flatMapTo(HashSet()) { it.names }
		return uniqueNames.map { name ->
			IntermediateCommandOverloads(name, singles.filterTo(HashSet()) { name in it.names })
		}
	}

	private fun generateIntermediateSingles(plugin: Plugin, parent: Any?, clazz: KClass<*>): Set<IntermediateSingleCommand> {
		return clazz.declaredMemberExtensionFunctions.mapNotNullTo(HashSet()) {
			generateIntermediate(plugin, parent, it)
		}
	}

	private fun generateIntermediate(plugin: Plugin, parent: Any?, function: KFunction<*>): IntermediateSingleCommand? {
		if (function.extensionReceiverParameter?.type?.jvmErasure != Execute::class)
			return null

		val data = getAnnotationData(plugin, function.name, function)
		val types = function.valueParameters.map {
			it.name!! to it.type
		}.toMap()

		return IntermediateSingleCommand(parent, data, Signature(types), function)
	}

	private fun getAnnotationData(plugin: Plugin, originalName: String, element: KAnnotatedElement): Command {
		val name = element.findAnnotation<Command.Name>()?.name ?: originalName
		val aliases = element.findAnnotation<Command.Aliases>()?.aliases?.toList() ?: emptyList()
		val description = element.findAnnotation<Command.Description>()?.description ?: ""
		val usage = element.findAnnotation<Command.Usage>()?.usage ?: "/$name"
		val permission = element.findAnnotation<Command.Permission>()?.permission

//		val default = element.findAnnotation<Command.Default>() != null

		return Command(plugin, name, aliases, description, usage, permission)
	}

	data class IntermediateCommandGroup(
			val command: Command,
			val commands: Set<IntermediateCommand>,
			val default: IntermediateCommand?
	) : IntermediateCommand {

		override val names: List<String>
			get() = command.aliases + command.name

		override fun execute(sender: CommandSender, raw: String): ExecuteResult {
			val targetName = raw.takeWhile { it != ' ' }
			val extra = raw.removePrefix(targetName).trim()

			if (targetName.isBlank()) {
				if (default != null) {
					return default.execute(sender, extra)
				} else {
					//TODO: Send error about no matching subcommand?
				}
			} else {
//				val target = commands.find {
//
//				}
			}
			TODO()
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
			val commands: Set<IntermediateSingleCommand>
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

			val context = Execute(command, sender, args.raw.split(' ').toTypedArray())
			params[function.extensionReceiverParameter!!] = context

			params += function.parameters.map {
				it to args.values[it.name]
			}.filter { it.second != null }

			try {
				function.callBy(params)
			} catch (t: Throwable) {
				return ExecuteResult.FailedError(t)
			}

			return ExecuteResult.Success(args.extra)
		}

	}

	interface IntermediateCommand {
		val names: List<String>

		fun execute(sender: CommandSender, raw: String): ExecuteResult
	}

	sealed class ExecuteResult {
		data class FailedParse(val fail: Signature.ParseResult.Fail) : ExecuteResult()
		data class FailedError(val error: Throwable) : ExecuteResult()
		data class FailedNoMatch(val parseFails: Collection<Signature.ParseResult.Fail>) : ExecuteResult()
		data class Success(val extra: String) : ExecuteResult()
	}

	data class Signature(val types: Map<String, KType>) {

		fun parse(raw: String): ParseResult {
			return ParseResult.Success(this, raw, emptyMap(), raw) //TODO
		}

		sealed class ParseResult(val signature: Signature) {
			class Success(
					signature: Signature,
					val raw: String,
					val values: Map<String, Any?>,
					val extra: String
			) : ParseResult(signature)

			class Fail(
					signature: Signature,
					val failedAtName: String,
					val failedAtIndex: Int
			) : ParseResult(signature)
		}

	}
}