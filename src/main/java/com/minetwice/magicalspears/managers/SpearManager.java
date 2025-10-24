package com.minetwice.magicalspears.managers;

import com.minetwice.magicalspears.objects.MagicalSpear;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpearManager {
    
    private final Map<UUID, Long> revealCooldowns = new HashMap<>();
    private final Map<UUID, Integer> spearLevels = new HashMap<>(); // NEW FEATURE 2: Level System
    
    public void giveSpear(Player player, MagicalSpear spear) {
        PlayerInventory inventory = player.getInventory();
        ItemStack spearItem = createLeveledSpear(player, spear);
        
        HashMap<Integer, ItemStack> leftover = inventory.addItem(spearItem);
        if (!leftover.isEmpty()) {
            player.getWorld().dropItem(player.getLocation(), spearItem);
        }
        
        player.sendMessage("§aYou received the " + spear.getDisplayName() + "§a!");
    }
    
    private ItemStack createLeveledSpear(Player player, MagicalSpear spear) {
        ItemStack spearItem = spear.createItem();
        ItemMeta meta = spearItem.getItemMeta();
        
        int level = getSpearLevel(player.getUniqueId(), spear);
        if (level > 0) {
            // Add level to display name
            String leveledName = spear.getDisplayName() + " §7[Lv." + level + "]";
            meta.displayName(net.kyori.adventure.text.Component.text(leveledName));
            
            // Add level info to lore
            java.util.List<net.kyori.adventure.text.Component> lore = meta.lore();
            if (lore != null) {
                lore.add(net.kyori.adventure.text.Component.text("§eLevel: §6" + level));
                lore.add(net.kyori.adventure.text.Component.text("§eExperience: §b" + (level * 10) + "/100"));
                meta.lore(lore);
            }
        }
        
        spearItem.setItemMeta(meta);
        return spearItem;
    }
    
    public void giveAllSpears(Player player) {
        for (MagicalSpear spear : MagicalSpear.values()) {
            giveSpear(player, spear);
        }
        player.sendMessage("§aYou received all magical spears!");
    }
    
    public void takeSpear(Player player, MagicalSpear spear) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();
        
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            MagicalSpear itemSpear = MagicalSpear.fromItem(item);
            if (itemSpear == spear) {
                inventory.setItem(i, null);
                player.sendMessage("§c" + spear.getDisplayName() + " §chas been taken from your inventory!");
                return;
            }
        }
        player.sendMessage("§cPlayer doesn't have the " + spear.getDisplayName() + "§c!");
    }
    
    public void takeAllSpears(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] contents = inventory.getContents();
        int count = 0;
        
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (MagicalSpear.fromItem(item) != null) {
                inventory.setItem(i, null);
                count++;
            }
        }
        player.sendMessage("§c" + count + " magical spears have been taken from your inventory!");
    }
    
    // NEW FEATURE 2: Level System
    public void addSpearExperience(Player player, MagicalSpear spear, int exp) {
        UUID playerId = player.getUniqueId();
        int key = spear.ordinal();
        
        int currentLevel = spearLevels.getOrDefault(playerId, 0);
        int newLevel = Math.min(currentLevel + exp / 10, 10); // Max level 10
        
        if (newLevel > currentLevel) {
            spearLevels.put(playerId, newLevel);
            player.sendMessage("§6🎉 " + spear.getDisplayName() + " §6leveled up to §e" + newLevel + "§6!");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        }
    }
    
    public int getSpearLevel(UUID playerId, MagicalSpear spear) {
        return spearLevels.getOrDefault(playerId, 0);
    }
    
    // Coordinate reveal system
    public void setRevealCooldown(Player player, int minutes) {
        long cooldownTime = System.currentTimeMillis() + (minutes * 60 * 1000L);
        revealCooldowns.put(player.getUniqueId(), cooldownTime);
    }
    
    public void removeRevealCooldown(Player player) {
        revealCooldowns.remove(player.getUniqueId());
    }
    
    public boolean hasRevealCooldown(Player player) {
        if (!revealCooldowns.containsKey(player.getUniqueId())) return false;
        
        long cooldownTime = revealCooldowns.get(player.getUniqueId());
        if (System.currentTimeMillis() > cooldownTime) {
            revealCooldowns.remove(player.getUniqueId());
            return false;
        }
        return true;
    }
    
    public long getRemainingRevealTime(Player player) {
        if (!revealCooldowns.containsKey(player.getUniqueId())) return 0;
        return revealCooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
    }
}
