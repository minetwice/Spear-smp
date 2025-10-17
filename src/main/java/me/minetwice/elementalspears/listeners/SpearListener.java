package me.minetwice.elementalspears.listeners;

import me.minetwice.elementalspears.ElementalSpears;
import me.minetwice.elementalspears.spears.*;
import me.minetwice.elementalspears.utils.CooldownManager;
import me.minetwice.elementalspears.utils.ActionBarUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SpearListener implements Listener {
    private static final Map<UUID, Long> coordsReveal = new HashMap<>();

    public static void setCoordsReveal(UUID uuid) {
        coordsReveal.put(uuid, System.currentTimeMillis());
    }

    public static boolean isCoordsReveal(UUID uuid) {
        Long start = coordsReveal.get(uuid);
        if (start == null) return false;
        long duration = ElementalSpears.getInstance().getConfig().getInt("coords-reveal-duration") * 1000L;
        if (System.currentTimeMillis() - start > duration) {
            coordsReveal.remove(uuid);
            return false;
        }
        return true;
    }

    public static void removeCoordsReveal(UUID uuid) {
        coordsReveal.remove(uuid);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) return;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!ElementalSpear.isSpear(item)) return;

        if (!CooldownManager.canUse(player)) {
            ActionBarUtils.sendActionBar(player, "§cAbility on cooldown!");
            e.setCancelled(true);
            return;
        }

        CooldownManager.setCooldown(player);

        ElementalSpear spear = ElementalSpear.getSpear(item);
        if (spear != null) {
            spear.onAttack(player, e.getEntity());
            setCoordsReveal(player.getUniqueId());
            revealCoords(player, e.getEntity());
        }
    }

    private void revealCoords(Player attacker, Entity victim) {
        Location loc = victim.getLocation();
        String msg = "§e" + attacker.getName() + " used a spear! Cords: X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ();
        Bukkit.broadcastMessage(msg);
    }
}
