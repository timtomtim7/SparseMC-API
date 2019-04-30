package blue.sparse.minecraft.compatibility.vault

import org.bukkit.OfflinePlayer

data class PlayerBalance(val player: OfflinePlayer) : Number() {
	private var money
		get() = VaultCompat.economy.getBalance(player)
		set(value) {
			val difference = value - money
			if (difference <= 0)
				VaultCompat.economy.withdrawPlayer(player, -difference)
			else
				VaultCompat.economy.depositPlayer(player, difference)
		}

	override fun toByte() = money.toByte()
	override fun toChar() = money.toChar()
	override fun toDouble() = money
	override fun toFloat() = money.toFloat()
	override fun toInt() = money.toInt()
	override fun toLong() = money.toLong()
	override fun toShort() = money.toShort()

	operator fun contains(amount: Number) = money >= amount.toDouble()
	operator fun minusAssign(amount: Number) {
		money -= amount.toDouble()
	}

	operator fun plusAssign(amount: Number) {
		money += amount.toDouble()
	}

	infix fun has(amount: Number): Boolean = amount in this
}