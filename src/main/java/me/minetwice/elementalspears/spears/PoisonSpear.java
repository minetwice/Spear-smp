package me.minetwice.elementalspears.spears;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Particle;

public class PoisonSpear extends ElementalSpear {
    @Override
    public String getName() { return "Poison Spear"; }
    @Override
    public Material getMaterial() { return Material.TRIDENT; }
    @));
        }
        player.getWorld().spawnParticle(Particle.SPELL_MOB, victim.getLocation(), 15, 0.3, 1, 0.3, 1);
        player.sendMessage("§2Poisoned for 7s!");
    }
    @Override
    public String abilityName() { return "Poison enemy for 7s"; }
}
