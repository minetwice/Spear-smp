package com.minetwice.magicalspears.objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.Arrays;

public enum MagicalSpear {
    
    LIGHTNING_SPEAR("§6Lightning Spear", 
        "§7Shoots lightning bolts and knocks back enemies",
        Particle.ELECTRIC_SPARK,
        Sound.ENTITY_LIGHTNING_BOLT_THUNDER,
        Color.YELLOW,
        25,
        new String[]{
            "§e• Shoots lightning projectiles",
            "§e• High knockback on hit",
            "§e• Chain lightning to nearby enemies", 
            "§e• Stuns enemies for 2 seconds",
            "§e• Cooldown: 25 seconds"
        }),
    
    ICE_SPEAR("§bIce Spear", 
        "§7Freezes enemies and shoots ice shards",
        Particle.SNOWFLAKE,
        Sound.BLOCK_GLASS_BREAK,
        Color.AQUA,
        20,
        new String[]{
            "§e• Shoots freezing projectiles",
            "§e• Slows and freezes targets",
            "§e• Creates ice trail on ground",
            "§e• Ice armor for 5 seconds",
            "§e• Cooldown: 20 seconds"
        }),
    
    FIRE_SPEAR("§cFire Spear", 
        "§7Shoots fireballs and sets enemies ablaze",
        Particle.FLAME,
        Sound.ENTITY_BLAZE_SHOOT,
        Color.RED,
        15,
        new String[]{
            "§e• Shoots explosive fireballs",
            "§e• Sets enemies on fire",
            "§e• Area damage explosion",
            "§e• Fire resistance for 8 seconds",
            "§e• Cooldown: 15 seconds"
        }),
    
    VOID_SPEAR("§5Void Spear", 
        "§7Drains health and teleports enemies",
        Particle.SOUL_FIRE_FLAME,
        Sound.ENTITY_ENDERMAN_TELEPORT,
        Color.PURPLE,
        30,
        new String[]{
            "§e• Drains health from enemies",
            "§e• Teleports targets randomly", 
            "§e• Applies blindness and nausea",
            "§e• Creates void portal trap",
            "§e• Cooldown: 30 seconds"
        }),
    
    LIFE_SPEAR("§dLife Spear", 
        "§7Drains enemy health to heal yourself",
        Particle.HEART,
        Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
        Color.FUCHSIA,
        35,
        new String[]{
            "§e• Drains health from enemies",
            "§e• Heals attacker on hit",
            "§e• Gives regeneration effect",
            "§e• Creates healing aura",
            "§e• Cooldown: 35 seconds"
        }),
    
    POISON_SPEAR("§2Poison Spear", 
        "§7Shoots poison darts and creates toxic clouds",
        Particle.SQUID_INK, // CHANGED FROM SPELL/SPELL_WITCH
        Sound.ENTITY_WITCH_AMBIENT,
        Color.GREEN,
        20,
        new String[]{
            "§e• Shoots poison projectiles",
            "§e• Applies poison and wither",
            "§e• Creates poison cloud area",
            "§e• Poison immunity for 10 seconds",
            "§e• Cooldown: 20 seconds"
        });
    
    private final String displayName;
    private final String description;
    private final Particle particle;
    private final Sound sound;
    private final Color color;
    private final int cooldown;
    private final String[] abilityDetails;
    
    MagicalSpear(String displayName, String description, Particle particle, 
                Sound sound, Color color, int cooldown, String[] abilityDetails) {
        this.displayName = displayName;
        this.description = description;
        this.particle = particle;
        this.sound = sound;
        this.color = color;
        this.cooldown = cooldown;
        this.abilityDetails = abilityDetails;
    }
    
    public ItemStack createItem() {
        ItemStack spear = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = spear.getItemMeta();
        
        meta.displayName(net.kyori.adventure.text.Component.text(displayName));
        
        // Create lore with ability details
        java.util.List<net.kyori.adventure.text.Component> lore = new java.util.ArrayList<>();
        lore.add(net.kyori.adventure.text.Component.text(description));
        lore.add(net.kyori.adventure.text.Component.text(""));
        
        // Add ability details
        for (String detail : abilityDetails) {
            lore.add(net.kyori.adventure.text.Component.text(detail));
        }
        
        lore.add(net.kyori.adventure.text.Component.text(""));
        lore.add(net.kyori.adventure.text.Component.text("§6Right-click to shoot projectile"));
        lore.add(net.kyori.adventure.text.Component.text("§6Left-click for melee attack"));
        lore.add(net.kyori.adventure.text.Component.text("§6Magical Weapon"));
        
        meta.lore(lore);
        
        // Add custom model data for texture differentiation
        meta.setCustomModelData(this.ordinal() + 1000);
        
        spear.setItemMeta(meta);
        return spear;
    }
    
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public Particle getParticle() { return particle; }
    public Sound getSound() { return sound; }
    public Color getColor() { return color; }
    public int getCooldown() { return cooldown; }
    public String[] getAbilityDetails() { return abilityDetails; }
    
    public static MagicalSpear fromItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) return null;
        
        String displayName = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(meta.displayName());
        for (MagicalSpear spear : values()) {
            if (displayName.contains(spear.getDisplayName().replace("§", ""))) {
                return spear;
            }
        }
        return null;
    }
}
