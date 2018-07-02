package blue.sparse.minecraft.commands

import org.bukkit.plugin.Plugin

data class Command(
		val plugin: Plugin,
		val name: String,
		val aliases: List<String>,
		val description: String,
		val usage: String,
		val permission: String?
) {
	@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
	annotation class Name(val name: String)

	@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
	annotation class Aliases(vararg val aliases: String)

	@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
	annotation class Description(val description: String)

	@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
	annotation class Usage(val usage: String)

	@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
	annotation class Permission(val permission: String)

	/**
	 * Used to indicate the default function for a subcommand group
	 */
	@Target(AnnotationTarget.FUNCTION)
	annotation class Default
}