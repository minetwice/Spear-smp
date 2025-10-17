package me.minetwice.elementalspears.spears;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TimeSpear extends ElementalSpear {
    @Override
    public String getName() { return "Time Spear"; }
    @Override
    public Material getMaterial() { return Material.TRIDENT; }
    @Override
    public void onAttack(Player player, Entity victim) {
        if (victim instanceof Player target) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 5, 2));
            player.sendMessage("§bTime slowed for 5s!");
        }
    }
    @Override
    public String abilityName() { return "Slow enemy for 5s"; }
}
