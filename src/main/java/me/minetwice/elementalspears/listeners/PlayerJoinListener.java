package me.minetwice.elementalspears.listeners;

import me.minetwice.elementalspears.ElementalSpears;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String msg = ElementalSpears.getInstance().getConfig().getString("join-message");
        e.getPlayer().sendMessage(msg);
    }
}
