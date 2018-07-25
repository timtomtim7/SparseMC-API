package blue.sparse.minecraft.compatibility.vault.extensions

import blue.sparse.minecraft.compatibility.vault.VaultCompat
import org.bukkit.Server

val Server.vaultEconomy get() = VaultCompat.economy
val Server.vaultPermission get() = VaultCompat.permission
val Server.vaultChat get() = VaultCompat.chat