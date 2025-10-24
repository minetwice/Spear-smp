package com.minetwice.magicalspears.managers;

import com.minetwice.magicalspears.objects.MagicalSpear;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    
    private final Map<UUID, Map<MagicalSpear, Long>> cooldowns = new HashMap<>();
    private final Map<UUID, Map<MagicalSpear, Long>> meleeCooldowns = new HashMap<>();
    
    public void setCooldown(UUID playerId, MagicalSpear spear, boolean isProjectile) {
        Map<MagicalSpear, Long> playerCooldowns = isProjectile ? 
            cooldowns.getOrDefault(playerId, new HashMap<>()) :
            meleeCooldowns.getOrDefault(playerId, new HashMap<>());
            
        long cooldownTime = System.currentTimeMillis() + (spear.getCooldown() * 1000L);
        playerCooldowns.put(spear, cooldownTime);
        
        if (isProjectile) {
            cooldowns.put(playerId, playerCooldowns);
        } else {
            meleeCooldowns.put(playerId, playerCooldowns);
        }
    }
    
    public boolean hasCooldown(UUID playerId, MagicalSpear spear, boolean isProjectile) {
        Map<MagicalSpear, Long> playerCooldowns = isProjectile ? cooldowns.get(playerId) : meleeCooldowns.get(playerId);
        if (playerCooldowns == null || !playerCooldowns.containsKey(spear)) return false;
        
        long cooldownTime = playerCooldowns.get(spear);
        if (System.currentTimeMillis() > cooldownTime) {
            playerCooldowns.remove(spear);
            return false;
        }
        return true;
    }
    
    public long getRemainingCooldown(UUID playerId, MagicalSpear spear, boolean isProjectile) {
        if (!hasCooldown(playerId, spear, isProjectile)) return 0;
        
        Map<MagicalSpear, Long> playerCooldowns = isProjectile ? cooldowns.get(playerId) : meleeCooldowns.get(playerId);
        return playerCooldowns.get(spear) - System.currentTimeMillis();
    }
    
    public void removeCooldown(UUID playerId, MagicalSpear spear, boolean isProjectile) {
        Map<MagicalSpear, Long> playerCooldowns = isProjectile ? cooldowns.get(playerId) : meleeCooldowns.get(playerId);
        if (playerCooldowns != null) {
            playerCooldowns.remove(spear);
        }
    }
    
    public void clearAllCooldowns(UUID playerId) {
        cooldowns.remove(playerId);
        meleeCooldowns.remove(playerId);
    }
    
    public void updateCooldowns() {
        long currentTime = System.currentTimeMillis();
        
        // Update projectile cooldowns
        for (Map<MagicalSpear, Long> playerCooldowns : cooldowns.values()) {
            playerCooldowns.entrySet().removeIf(entry -> currentTime > entry.getValue());
        }
        
        // Update melee cooldowns
        for (Map<MagicalSpear, Long> playerCooldowns : meleeCooldowns.values()) {
            playerCooldowns.entrySet().removeIf(entry -> currentTime > entry.getValue());
        }
    }
    
    public String getCooldownDisplay(Player player, MagicalSpear spear) {
        long projectileCD = getRemainingCooldown(player.getUniqueId(), spear, true);
        long meleeCD = getRemainingCooldown(player.getUniqueId(), spear, false);
        
        if (projectileCD <= 0 && meleeCD <= 0) {
            return "§aReady!";
        }
        
        StringBuilder display = new StringBuilder("§cCooldown: ");
        if (projectileCD > 0) {
            display.append("Shoot(").append(projectileCD / 1000).append("s) ");
        }
        if (meleeCD > 0) {
            display.append("Melee(").append(meleeCD / 1000).append("s)");
        }
        
        return display.toString();
    }
}
