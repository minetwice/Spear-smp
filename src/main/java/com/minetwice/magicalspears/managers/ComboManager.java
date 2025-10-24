package com.minetwice.magicalspears.managers;

import com.minetwice.magicalspears.objects.MagicalSpear;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ComboManager {
    
    private final Map<UUID, ComboData> playerCombos = new HashMap<>();
    
    public static class ComboData {
        public MagicalSpear lastSpear;
        public int comboCount;
        public long lastAttackTime;
        public int totalHits;
        
        public ComboData(MagicalSpear spear) {
            this.lastSpear = spear;
            this.comboCount = 1;
            this.lastAttackTime = System.currentTimeMillis();
            this.totalHits = 1;
        }
    }
    
    public void registerHit(Player player, MagicalSpear spear) {
        UUID playerId = player.getUniqueId();
        ComboData combo = playerCombos.get(playerId);
        
        long currentTime = System.currentTimeMillis();
        
        if (combo == null || currentTime - combo.lastAttackTime > 5000) {
            // New combo
            playerCombos.put(playerId, new ComboData(spear));
            showComboMessage(player, 1);
            return;
        }
        
        if (spear == combo.lastSpear && currentTime - combo.lastAttackTime <= 2000) {
            // Continue combo
            combo.comboCount++;
            combo.totalHits++;
            combo.lastAttackTime = currentTime;
            
            showComboMessage(player, combo.comboCount);
            applyComboEffects(player, combo);
            
        } else {
            // Different spear or too slow - reset combo
            playerCombos.put(playerId, new ComboData(spear));
            showComboMessage(player, 1);
        }
    }
    
    private void showComboMessage(Player player, int comboCount) {
        String message = "§6Combo: §e" + comboCount + " hits!";
        if (comboCount >= 3) {
            message += " §c⚡";
        }
        if (comboCount >= 5) {
            message += " §4🔥";
        }
        player.sendActionBar(net.kyori.adventure.text.Component.text(message));
    }
    
    private void applyComboEffects(Player player, ComboData combo) {
        switch (combo.comboCount) {
            case 3:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
                player.sendMessage("§bCombo Bonus: Speed I for 5 seconds!");
                break;
            case 5:
                player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 80, 0)); // CHANGED FROM INCREASE_DAMAGE
                player.sendMessage("§cCombo Bonus: Strength I for 4 seconds!");
                break;
            case 8:
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                player.sendMessage("§aCombo Bonus: Regeneration II for 3 seconds!");
                player.setAbsorptionAmount(player.getAbsorptionAmount() + 4.0);
                break;
        }
        
        // Special combo achievement
        if (combo.totalHits >= 20) {
            player.sendMessage("§6§lCOMBO MASTER! §e20+ total hits in one life!");
            player.getWorld().playSound(player.getLocation(), 
                org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }
    
    public void updateCombos() {
        long currentTime = System.currentTimeMillis();
        playerCombos.entrySet().removeIf(entry -> 
            currentTime - entry.getValue().lastAttackTime > 5000);
    }
    
    public void resetCombo(UUID playerId) {
        playerCombos.remove(playerId);
    }
    
    public int getComboCount(UUID playerId) {
        ComboData combo = playerCombos.get(playerId);
        return combo != null ? combo.comboCount : 0;
    }
}
