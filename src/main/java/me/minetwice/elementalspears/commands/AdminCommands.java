package me.minetwice.elementalspears.commands;

import me.minetwice.elementalspears.listeners.SpearListener;
import me.minetwice.elementalspears.spears.ElementalSpear;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;

public class AdminCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("elementalspears.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission.");
            return true;
        }
        if (label.equalsIgnoreCase("clearcords")) {
            if (args.length != 1) return false;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }
            SpearListener.removeCoordsReveal(target.getUniqueId());
            sender.sendMessage("Coords reveal cleared for " + target.getName());
            return true;
        }
        if (label.equalsIgnoreCase("clearspear")) {
            if (args.length != 1) return false;
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }
            for (int i = 0; i < target.getInventory().getSize(); i++) {
                ItemStack item = target.getInventory().getItem(i);
                if (ElementalSpear.isSpear(item)) {
                    target.getInventory().setItem(i, null);
                }
            }
            sender.sendMessage("All spears removed from " + target.getName());
            return true;
        }
        return false;
    }
}
