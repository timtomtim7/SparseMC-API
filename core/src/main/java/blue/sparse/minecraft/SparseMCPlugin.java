package blue.sparse.minecraft;

import blue.sparse.minecraft.module.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Plugin class for the SparseMC API
 */
public final class SparseMCPlugin extends JavaPlugin {
	
	private static SparseMCPlugin plugin;
	private static List<Runnable> disableHooks = new ArrayList<>();
	
	/**
	 * @return the instance of SparseMCPlugin
	 */
	public static SparseMCPlugin getPlugin() {
		return plugin;
	}
	
	public static void addDisableHook(Runnable runnable) {
		disableHooks.add(runnable);
	}
	
	public void onLoad() {
		plugin = this;
		KotlinLoader.load(new File(getDataFolder(), "dependencies"));
	}
	
	public void onDisable() {
		ModuleManager.INSTANCE.onPluginDisable();
		disableHooks.forEach(Runnable::run);
	}
	
	public void onEnable() {
		ModuleManager.INSTANCE.onPluginEnable();
	}
	
}
