package me.minetwice.elementalspears.spears;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;

public class IceSpear extends ElementalSpear {
    @Override
    public String getName() { return "Ice Spear"; }
    @Override
    public Material getMaterial() { return Material.TRIDENT; }
    @Override
    public void onAttack(Player player, Entity victim) {
        if (victim instanceof Player target) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 6, 3));
        }
        player.getWorld().spawnParticle(Particle.SNOWFLAKE, victim.getLocation(), 20, 0.5, 1, 0.5, 0.2);
        player.sendMessage("§bFrozen for 6s!");
    }
    @Override
    public String abilityName() { return "Freeze enemy for 6s"; }
}
