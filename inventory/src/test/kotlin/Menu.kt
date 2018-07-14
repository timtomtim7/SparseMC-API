import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class Menu(val player: Player, private val setup: Menu.() -> Unit) : InventoryHolder {
    private var inventory: Inventory
    private var name: String = "Inventory"

    var rows: Int = 3
        private set
    val slots: Int
        get() = rows * 9

    private val _menuItems = mutableSetOf<MenuItem>()
    val menuItems: Set<MenuItem> = _menuItems

    init {
        inventory = server.createInventory(this, 9, "Inventory")
        setup()
        player.openInventory(inventory)
    }

    override fun getInventory(): Inventory {
        return inventory
    }

    fun name(name: String) {
        this.name = name
        inventory = server.createInventory(this, slots, name)
    }

    fun size(size: Int) {
        this.rows = size
        inventory = server.createInventory(this, slots, name)
    }

    inline fun item(
            slot: Int,
            type: Material = Material.AIR,
            amount: Int = 1,
            damage: Short = 0,
            removable: Boolean = false,
            body: MenuItem.() -> Unit
    ): MenuItem {
        val menuItem = MenuItem(slot, type, amount, damage, removable).apply(body)
        item(menuItem)
        getInventory().setItem(slot, menuItem)
        return menuItem
    }

    infix fun Int.x(num: Int): Int {
        val x = this
        val y = num

        if (x > 8)
            throw IllegalStateException("x component cannot be greater than 8.")
        if (y > inventory.size / 9)
            throw IllegalStateException("y component cannot be greater than number of rows in the inventory.")

        val bottomLeftCorner = inventory.size - 9
        var slot: Int = bottomLeftCorner
        slot += x
        slot -= y * 9
        return slot
    }

    fun item(
            slot: Int,
            type: Material = Material.AIR,
            amount: Int = 1,
            damage: Short = 0,
            removable: Boolean = false
    ) {
        item(slot, type, amount, damage, removable) {}
    }

    fun item(menuItem: MenuItem) {
        val menuItemInList = _menuItems.find { it.slot == menuItem.slot }

        if (menuItemInList != null) {
            _menuItems.remove(menuItemInList)
        }

        _menuItems.add(menuItem)
    }

    fun refresh() {
        player.inventory(setup)
    }
}