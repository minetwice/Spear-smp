package me.minetwice.elementalspears.commands;

import me.minetwice.elementalspears.spears.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class SpearCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        player.getInventory().addItem(new TimeSpear().getItem());
        player.getInventory().addItem(new LavaSpear().getItem());
        player.getInventory().addItem(new IceSpear().getItem());
        player.getInventory().addItem(new PoisonSpear().getItem());
        player.getInventory().addItem(new SoulSpear().getItem());

        player.sendTitle("§6Elemental Spears §bObtained!", "§aUse your powers wisely!", 10, 60, 10);
        player.sendMessage(ChatColor.AQUA + "You received all elemental spears!");

        return true;
    }
}
