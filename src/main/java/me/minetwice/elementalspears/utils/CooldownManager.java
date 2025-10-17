package me.minetwice.elementalspears.utils;

import me.minetwice.elementalspears.ElementalSpears;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    public static boolean canUse(Player player) {
        long now = System.currentTimeMillis();
        long cd = ElementalSpears.getInstance().getConfig().getInt("cooldown") * 1000L;
        Long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
        return (now - last) > cd;
    }

    public static void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
