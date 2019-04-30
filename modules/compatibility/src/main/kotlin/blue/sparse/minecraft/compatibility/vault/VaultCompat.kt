package blue.sparse.minecraft.compatibility.vault

import blue.sparse.minecraft.compatibility.Compat
import blue.sparse.minecraft.core.extensions.server
import net.milkbowl.vault.chat.Chat
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.permission.Permission

object VaultCompat : Compat {
	val economy: Economy by lazy { server.servicesManager.getRegistration(Economy::class.java).provider }
	val permission: Permission by lazy { server.servicesManager.getRegistration(Permission::class.java).provider }
	val chat: Chat by lazy { server.servicesManager.getRegistration(Chat::class.java).provider }
}