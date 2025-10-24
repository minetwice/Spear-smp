package com.minetwice.magicalspears;

import com.minetwice.magicalspears.commands.SpearCommand;
import com.minetwice.magicalspears.listeners.SpearListener;
import com.minetwice.magicalspears.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class MagicalSpearsPlugin extends JavaPlugin {
    
    private static MagicalSpearsPlugin instance;
    private SpearManager spearManager;
    private CooldownManager cooldownManager;
    private GUIManager guiManager;
    private GraceManager graceManager;
    private ComboManager comboManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        this.spearManager = new SpearManager();
        this.cooldownManager = new CooldownManager();
        this.guiManager = new GUIManager();
        this.graceManager = new GraceManager();
        this.comboManager = new ComboManager();
        
        // Register commands
        getCommand("spear").setExecutor(new SpearCommand());
        getCommand("magicalspears").setExecutor(new SpearCommand());
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new SpearListener(), this);
        
        // Save default config
        saveDefaultConfig();
        
        // Start cooldown update task
        startCooldownUpdateTask();
        
        getLogger().info("MagicalSpears plugin enabled successfully!");
    }
    
    @Override
    public void onDisable() {
        graceManager.cancelGraceTimer();
        getLogger().info("MagicalSpears plugin disabled!");
    }
    
    private void startCooldownUpdateTask() {
        getServer().getScheduler().runTaskTimer(this, () -> {
            cooldownManager.updateCooldowns();
            comboManager.updateCombos();
        }, 0L, 20L); // Update every second
    }
    
    public static MagicalSpearsPlugin getInstance() {
        return instance;
    }
    
    public SpearManager getSpearManager() {
        return spearManager;
    }
    
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
    
    public GUIManager getGuiManager() {
        return guiManager;
    }
    
    public GraceManager getGraceManager() {
        return graceManager;
    }
    
    public ComboManager getComboManager() {
        return comboManager;
    }
}
