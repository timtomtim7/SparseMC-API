package blue.sparse.minecraft.inventory.extensions

import blue.sparse.minecraft.inventory.inventory.Menu
import org.bukkit.entity.Player

fun Player.inventory(setup: Menu.() -> Unit): Menu {
    return Menu(this, setup)
}