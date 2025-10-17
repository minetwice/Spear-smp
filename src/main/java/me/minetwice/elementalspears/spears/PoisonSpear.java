package me.minetwice.elementalspears.spears;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;
import org.bukkit.Color;

public class PoisonSpear extends ElementalSpear {
    @Override
    public String getName() { return "Poison Spear"; }
    @Override
    public Material getMaterial() { return Material.TRIDENT; }
    @Override
    public void onAttack(Player player, Entity victim) {
        if (victim instanceof Player target) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 7, 1));
        }
        // Green poison particles for 1.21.1
        Particle.DustOptions dust = new Particle.DustOptions(Color.fromRGB(0, 255, 0), 1.0f);
        player.getWorld().spawnParticle(Particle.DUST, victim.getLocation(), 15, 0.3, 1, 0.3, 0, dust);
        player.sendMessage("§2Poisoned for 7s!");
    }
    @Override
    public String abilityName() { return "Poison enemy for 7s"; }
}
