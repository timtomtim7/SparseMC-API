import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class MenuItem(
        val slot: Int,
        type: Material = Material.AIR,
        amount: Int = 1,
        damage: Short = 0,
        val removable: Boolean = false
) : ItemStack(type, amount, damage) {

    internal var clickCallback: () -> Unit = {}
        private set

    fun onClick(body: () -> Unit) {
        clickCallback = body
    }
}