package me.minetwice.elementalspears;

import me.minetwice.elementalspears.commands.AdminCommands;
import me.minetwice.elementalspears.commands.SpearCommands;
import me.minetwice.elementalspears.listeners.PlayerJoinListener;
import me.minetwice.elementalspears.listeners.SpearListener;
import org.bukkit.plugin.java.JavaPlugin;

public class ElementalSpears extends JavaPlugin {
    private static ElementalSpears instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new SpearListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        getCommand("getspear").setExecutor(new SpearCommands());
        getCommand("clearcords").setExecutor(new AdminCommands());
        getCommand("clearspear").setExecutor(new AdminCommands());
    }

    public static ElementalSpears getInstance() { return instance; }
}
