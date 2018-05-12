package blue.sparse.minecraft;

import blue.sparse.minecraft.module.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Plugin class for the SparseMC API
 */
public final class SparseMCPlugin extends JavaPlugin {
	
	private static SparseMCPlugin plugin;
	
	/**
	 * @return the instance of SparseMCPlugin
	 */
	public static SparseMCPlugin getPlugin() {
		return plugin;
	}
	
	public void onLoad() {
		plugin = this;
		KotlinLoader.load(new File(getDataFolder(), "dependencies"));
	}
	
	public void onDisable() {
		ModuleManager.INSTANCE.onPluginDisable();
	}
	
	public void onEnable() {
		ModuleManager.INSTANCE.onPluginEnable();
	}
	
}
