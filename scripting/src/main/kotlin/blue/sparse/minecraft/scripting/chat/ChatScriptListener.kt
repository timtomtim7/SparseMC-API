package blue.sparse.minecraft.scripting.chat

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.extensions.event.cancel
import blue.sparse.minecraft.core.extensions.sendColoredMessage
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.scripting.ScriptingModule
import blue.sparse.minecraft.scripting.kotlin.old.KotlinScriptManager
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault
import java.io.File

object ChatScriptListener : Listener {

	val permission = Permission(
			"sparsemc.api.scripting",
			if (ScriptingModule.Config.chatScriptDefaultsToOperators) PermissionDefault.OP
			else PermissionDefault.FALSE
	)

	private val pluginJars = SparseMCAPIPlugin.getPlugin()
			.dataFolder.parentFile.listFiles().filter { it.extension == "jar" }

	private val moduleJars = SparseMCAPIPlugin.getModulesFolder()
			.listFiles().filter { it.extension == "jar" }

	private val dependencyJars = SparseMCAPIPlugin.getDependenciesFolder()
			.listFiles().filter { it.extension == "jar" }

	val manager = KotlinScriptManager(ChatScriptTemplate::class, classpath = pluginJars + moduleJars + dependencyJars)

	init {
		server.pluginManager.registerEvents(this, SparseMCAPIPlugin.getPlugin())
	}

	@EventHandler
	fun onPlayerChat(e: AsyncPlayerChatEvent) {
		if (!ScriptingModule.Config.enableChatScripting)
			return

		val message = e.message
		if (!message.startsWith("$"))
			return

		if (!e.player.hasPermission(permission))
			return

		e.cancel()
		val code = message.removePrefix("$")
		val file = File("scripts/$code").takeIf { it.exists() }
		val scheduler = Bukkit.getScheduler()
		val plugin = SparseMCAPIPlugin.getPlugin()
		scheduler.runTaskAsynchronously(plugin) {
			val compiled = try {
				if(file != null) {
					manager.getOrCompile(file)
				}else{
					manager.getOrCompile(code)
				}
			} catch (t: Throwable) {
				System.err.println("Error compiling chat script:")
				t.printStackTrace()
				e.player.sendColoredMessage("&c${t.message}")
				return@runTaskAsynchronously
			}

			scheduler.runTask(plugin) {
				val result = try {
					compiled.invoke(e.player).result
				} catch (t: Throwable) {
					System.err.println("Error running chat script:")
					t.printStackTrace()
					e.player.sendColoredMessage("&c${t.message}")
					return@runTask
				}

				e.player.sendMessage(result.toString())
			}
		}
	}

}