package blue.sparse.minecraft.compatibility.vault.extensions

import blue.sparse.minecraft.compatibility.vault.PlayerBalance
import blue.sparse.minecraft.compatibility.vault.VaultCompat
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

/**
 * Economy
 */
val OfflinePlayer.balance get() = PlayerBalance(this)

/**
 * Permission
 */
val Player.groups: Array<String>
	get() {
		return VaultCompat.permission.getPlayerGroups(this)
	}

val Player.primaryGroup: String
	get() {
		return VaultCompat.permission.getPrimaryGroup(this)
	}

/**
 * Chat
 */
var Player.chatPrefix: String
	get() {
		return VaultCompat.chat.getPlayerPrefix(this)
	}
	set(value) {
		VaultCompat.chat.setPlayerPrefix(this, value)
	}

var Player.chatSuffix: String
	get() {
		return VaultCompat.chat.getPlayerSuffix(this)
	}
	set(value) {
		VaultCompat.chat.setPlayerPrefix(this, value)
	}