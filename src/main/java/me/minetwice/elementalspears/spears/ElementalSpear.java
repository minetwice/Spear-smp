package me.minetwice.elementalspears.spears;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class ElementalSpear {
    public abstract String getName();
    public abstract Material getMaterial();
    public abstract void onAttack(Player player, Entity victim);
    public ItemStack getItem() {
        ItemStack spear = new ItemStack(getMaterial());
        ItemMeta meta = spear.getItemMeta();
        meta.setDisplayName(getName());
        meta.setLore(List.of("Elemental Spear", "Ability: " + abilityName()));
        spear.setItemMeta(meta);
        return spear;
    }
    public abstract String abilityName();

    public static boolean isSpear(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().endsWith("Spear");
    }

    public static ElementalSpear getSpear(ItemStack item) {
        if (!isSpear(item)) return null;
        String name = item.getItemMeta().getDisplayName();
        return switch (name) {
            case "Time Spear" -> new TimeSpear();
            case "Lava Spear" -> new LavaSpear();
            case "Ice Spear" -> new IceSpear();
            case "Poison Spear" -> new PoisonSpear();
            case "Soul Spear" -> new SoulSpear();
            default -> null;
        };
    }
}
