package me.minetwice.elementalspears.spears;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;

public class SoulSpear extends ElementalSpear {
    @Override
    public String getName() { return "Soul Spear"; }
    @Override
    public Material getMaterial() { return Material.TRIDENT; }
    @Override
    public void onAttack(Player player, Entity victim) {
        if (victim instanceof Player target) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1));
        }
        player.getWorld().spawnParticle(Particle.SOUL, victim.getLocation(), 15, 0.3, 1, 0.3, 0);
        player.sendMessage("§5Soul drained for 5s!");
    }
    @Override
    public String abilityName() { return "Wither enemy for 5s"; }
}
