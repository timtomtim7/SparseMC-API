package blue.sparse.minecraft.commands

import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmErasure

object CommandReflectionLoader {

	fun loadBukkitCommands(plugin: Plugin, clazz: KClass<*>): Set<BukkitCommand> {
		return emptySet()
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
	) {
//		fun toBukkitCommand(): BukkitCommand {
//			TODO()
//		}
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
	) {
		fun execute(sender: CommandSender, raw: String): ExecuteResult {
			val args = signature.parse(raw)
			return when (args) {
				is Signature.ParseResult.Fail -> ExecuteResult.FailedParse(args)
				is Signature.ParseResult.Success -> execute(sender, args)
			}
		}

		fun execute(sender: CommandSender, args: Signature.ParseResult.Success): ExecuteResult {
			if(args.signature != signature)
				throw IllegalArgumentException("Command arguments provided with wrong signature")

			val params = HashMap<KParameter, Any?>()
			function.instanceParameter?.let { params[it] = parent }

			val context = Execute(command, sender, args.raw.split(' ').toTypedArray())
			params[function.extensionReceiverParameter!!] = context

			params += function.parameters.map {
				it to args.values[it.name]
			}.filter { it.second != null }

			function.callBy(params)
			return ExecuteResult.Success(args.extra)
		}

		sealed class ExecuteResult {
			data class FailedParse(val fail: Signature.ParseResult.Fail): ExecuteResult()
			data class FailedError(val error: Throwable): ExecuteResult()
			data class Success(val extra: String): ExecuteResult()
		}

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
			): ParseResult(signature)

			class Fail(
					signature: Signature,
					val failedAtName: String,
					val failedAtIndex: Int
			): ParseResult(signature)
		}

	}
}