package blue.sparse.minecraft.core.extensions

import org.bukkit.block.Block
import org.bukkit.block.BlockState

inline fun <reified T : BlockState> Block.editState(body: T.() -> Unit): T? {
	val updated = state as? T ?: return null
	return updated.apply { body(); update() }
}