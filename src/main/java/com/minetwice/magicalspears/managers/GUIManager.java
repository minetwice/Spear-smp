package com.minetwice.magicalspears.managers;

import com.minetwice.magicalspears.objects.MagicalSpear;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class GUIManager {
    
    public void openSpearGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, 
            net.kyori.adventure.text.Component.text("§6Magical Spears §7- Click to Get"));
        
        // Add spears to GUI
        for (int i = 0; i < MagicalSpear.values().length; i++) {
            if (i >= 27) break;
            
            MagicalSpear spear = MagicalSpear.values()[i];
            ItemStack displayItem = createDisplayItem(player, spear);
            gui.setItem(i, displayItem);
        }
        
        // Add info item
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.displayName(net.kyori.adventure.text.Component.text("§ePlugin Information"));
        infoMeta.lore(Arrays.asList(
            net.kyori.adventure.text.Component.text("§7Magical Spears v1.0.0"),
            net.kyori.adventure.text.Component.text("§7by minetwice"),
            net.kyori.adventure.text.Component.text(""),
            net.kyori.adventure.text.Component.text("§eFeatures:"),
            net.kyori.adventure.text.Component.text("§7• 6 Unique Magical Spears"),
            net.kyori.adventure.text.Component.text("§7• Combo System"),
            net.kyori.adventure.text.Component.text("§7• Level Progression"),
            net.kyori.adventure.text.Component.text("§7• Coordinate Reveal")
        ));
        infoItem.setItemMeta(infoMeta);
        gui.setItem(22, infoItem);
        
        // Add close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.displayName(net.kyori.adventure.text.Component.text("§cClose"));
        closeButton.setItemMeta(closeMeta);
        gui.setItem(26, closeButton);
        
        player.openInventory(gui);
    }
    
    private ItemStack createDisplayItem(Player player, MagicalSpear spear) {
        ItemStack item = spear.createItem();
        ItemMeta meta = item.getItemMeta();
        
        // Get player's level with this spear
        int level = com.minetwice.magicalspears.MagicalSpearsPlugin.getInstance()
            .getSpearManager().getSpearLevel(player.getUniqueId(), spear);
        
        java.util.List<net.kyori.adventure.text.Component> lore = meta.lore();
        if (lore != null) {
            lore.add(net.kyori.adventure.text.Component.text(""));
            lore.add(net.kyori.adventure.text.Component.text("§eYour Level: §6" + level));
            lore.add(net.kyori.adventure.text.Component.text("§aClick to get this spear!"));
            
            meta.lore(lore);
        }
        
        item.setItemMeta(meta);
        return item;
    }
}
