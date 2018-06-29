package blue.sparse.minecraft;

import blue.sparse.minecraft.module.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Plugin class for the SparseMC API
 */
public final class SparseMCAPIPlugin extends JavaPlugin {
	
	private static final List<Runnable>    disableHooks = new ArrayList<>();
	private static       SparseMCAPIPlugin plugin;
	
	public static void addDisableHook(Runnable runnable) {
		disableHooks.add(runnable);
	}
	
	public static File getDependenciesFolder() {
		return new File(getPlugin().getDataFolder(), "dependencies");
	}
	
	public static File getModulesFolder() {
		return new File(getPlugin().getDataFolder(), "modules");
	}
	
	/**
	 * @return the instance of SparseMCAPIPlugin
	 */
	public static SparseMCAPIPlugin getPlugin() {
		return plugin;
	}
	
	public void onLoad() {
		plugin = this;
		KotlinLoader.load(getDependenciesFolder());
	}
	
	public void onEnable() {
		ModuleManager.INSTANCE.onPluginEnable();
	}
	
	public void onDisable() {
		ModuleManager.INSTANCE.onPluginDisable();
		disableHooks.forEach(Runnable::run);
		System.gc();
	}
	
	
}
