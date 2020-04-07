package grouplightning.artifactlightning;

import org.bukkit.plugin.java.JavaPlugin;

public final class Artifactlightning extends JavaPlugin {
    private CommandHandler commandHandler = new CommandHandler(this);

    @Override
    public void onEnable() {
        getCommand("ligline").setExecutor(commandHandler);
        getCommand("ligstorm").setExecutor(commandHandler);
        getCommand("gaelwave").setExecutor(commandHandler);
        getCommand("lightningstrike").setExecutor(commandHandler);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
