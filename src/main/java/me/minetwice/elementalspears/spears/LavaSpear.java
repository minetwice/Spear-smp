package me.minetwice.elementalspears.spears;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.Particle;

public class LavaSpear extends ElementalSpear {
    @Override
    public String getName() { return "Lava Spear"; }
    @Override
    public Material getMaterial() { return Material.TRIDENT; }
    @Override
    public void onAttack(Player player, Entity victim) {
        victim.setFireTicks(20 * 5);
        player.getWorld().spawnParticle(Particle.LAVA, victim.getLocation(), 20, 0.5, 1, 0.5, 0.2);
        player.sendMessage("§cLava burn for 5s!");
    }
    @Override
    public String abilityName() { return "Set enemy on fire for 5s"; }
}
