package com.minetwice.magicalspears.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GraceManager {
    
    private BukkitTask graceTask;
    private long graceEndTime = 0;
    private final Map<UUID, Long> combatLog = new HashMap<>();
    
    public void startGracePeriod(int minutes) {
        if (graceTask != null) {
            graceTask.cancel();
        }
        
        graceEndTime = System.currentTimeMillis() + (minutes * 60 * 1000L);
        
        graceTask = new BukkitRunnable() {
            @Override
            public void run() {
                long remaining = graceEndTime - System.currentTimeMillis();
                
                if (remaining <= 0) {
                    Bukkit.broadcast(net.kyori.adventure.text.Component.text("§cGrace period has ended! PvP is now enabled."));
                    this.cancel();
                    graceEndTime = 0;
                    return;
                }
                
                // Show action bar to all players
                int secondsLeft = (int) (remaining / 1000);
                int minutesLeft = secondsLeft / 60;
                int secs = secondsLeft % 60;
                
                String timeString = String.format("§aGrace Period: §f%02d:%02d", minutesLeft, secs);
                
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendActionBar(net.kyori.adventure.text.Component.text(timeString));
                }
            }
        }.runTaskTimer(com.minetwice.magicalspears.MagicalSpearsPlugin.getInstance(), 0L, 20L);
        
        Bukkit.broadcast(net.kyori.adventure.text.Component.text(
            "§aGrace period started for " + minutes + " minutes! PvP is disabled."));
    }
    
    public void cancelGraceTimer() {
        if (graceTask != null) {
            graceTask.cancel();
            graceTask = null;
        }
        graceEndTime = 0;
    }
    
    public boolean isGracePeriodActive() {
        return graceEndTime > System.currentTimeMillis();
    }
    
    public long getRemainingGraceTime() {
        return Math.max(0, graceEndTime - System.currentTimeMillis());
    }
    
    public void setCombat(Player player) {
        combatLog.put(player.getUniqueId(), System.currentTimeMillis() + (5 * 60 * 1000L)); // 5 minutes combat
    }
    
    public boolean isInCombat(Player player) {
        if (!combatLog.containsKey(player.getUniqueId())) return false;
        
        long combatTime = combatLog.get(player.getUniqueId());
        if (System.currentTimeMillis() > combatTime) {
            combatLog.remove(player.getUniqueId());
            return false;
        }
        return true;
    }
    
    public long getCombatTimeLeft(Player player) {
        if (!combatLog.containsKey(player.getUniqueId())) return 0;
        return combatLog.get(player.getUniqueId()) - System.currentTimeMillis();
    }
}
