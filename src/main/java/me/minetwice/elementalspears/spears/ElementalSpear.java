package me.minetwice.elementalspears.spears;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class ElementalSpear {
    private final String name;
    public ElementalSpear(String name) {
        this.name = name;
    }
    public String getName() { return name; }

    public abstract void activate(Player player, LivingEntity target);
}
