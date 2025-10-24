package com.minetwice.magicalspears.commands;

import com.minetwice.magicalspears.MagicalSpearsPlugin;
import com.minetwice.magicalspears.objects.MagicalSpear;
import com.minetwice.magicalspears.managers.SpearManager;
import com.minetwice.magicalspears.managers.GUIManager;
import com.minetwice.magicalspears.managers.GraceManager;
import com.minetwice.magicalspears.managers.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SpearCommand implements CommandExecutor, TabCompleter {
    
    private final SpearManager spearManager = MagicalSpearsPlugin.getInstance().getSpearManager();
    private final GUIManager guiManager = MagicalSpearsPlugin.getInstance().getGuiManager();
    private final GraceManager graceManager = MagicalSpearsPlugin.getInstance().getGraceManager();
    private final CooldownManager cooldownManager = MagicalSpearsPlugin.getInstance().getCooldownManager();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("magicalspears.use")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "give":
                handleGive(sender, args);
                break;
            case "take":
                handleTake(sender, args);
                break;
            case "all":
                handleAll(sender, args);
                break;
            case "gui":
                handleGUI(sender);
                break;
            case "grace":
                handleGrace(sender, args);
                break;
            case "reveal":
                handleReveal(sender, args);
                break;
            case "cooldown":
                handleCooldown(sender, args);
                break;
            default:
                sendHelp(sender);
                break;
        }
        
        return true;
    }
    
    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /spear give <player> <spear-name>");
            sender.sendMessage("§cAvailable spears: " + getSpearNames());
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        MagicalSpear spear = getSpearByName(args[2]);
        if (spear == null) {
            sender.sendMessage("§cInvalid spear name! Available: " + getSpearNames());
            return;
        }
        
        spearManager.giveSpear(target, spear);
        sender.sendMessage("§aGiven " + spear.getDisplayName() + " §ato " + target.getName());
    }
    
    private void handleTake(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /spear take <player> <spear-name>");
            sender.sendMessage("§cAvailable spears: " + getSpearNames());
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        MagicalSpear spear = getSpearByName(args[2]);
        if (spear == null) {
            sender.sendMessage("§cInvalid spear name! Available: " + getSpearNames());
            return;
        }
        
        spearManager.takeSpear(target, spear);
        sender.sendMessage("§cTaken " + spear.getDisplayName() + " §cfrom " + target.getName());
    }
    
    private void handleAll(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /spear all <player>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        spearManager.giveAllSpears(target);
        sender.sendMessage("§aGiven all spears to " + target.getName());
    }
    
    private void handleGUI(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return;
        }
        
        guiManager.openSpearGUI((Player) sender);
    }
    
    private void handleGrace(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /spear grace <minutes>");
            return;
        }
        
        try {
            int minutes = Integer.parseInt(args[1]);
            graceManager.startGracePeriod(minutes);
            sender.sendMessage("§aGrace period started for " + minutes + " minutes!");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number format!");
        }
    }
    
    private void handleReveal(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /spear reveal <player> <minutes>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        try {
            int minutes = Integer.parseInt(args[2]);
            spearManager.setRevealCooldown(target, minutes);
            sender.sendMessage("§a" + target.getName() + "'s coordinates will be revealed for " + minutes + " minutes!");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number format!");
        }
    }
    
    private void handleCooldown(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /spear cooldown <player> <clear|status>");
            return;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return;
        }
        
        if (args[2].equalsIgnoreCase("clear")) {
            cooldownManager.clearAllCooldowns(target.getUniqueId());
            sender.sendMessage("§aCleared all cooldowns for " + target.getName());
        } else if (args[2].equalsIgnoreCase("status")) {
            sender.sendMessage("§6Cooldown status for " + target.getName() + ":");
            for (MagicalSpear spear : MagicalSpear.values()) {
                String status = cooldownManager.getCooldownDisplay(target, spear);
                sender.sendMessage("§7- " + spear.getDisplayName() + ": " + status);
            }
        } else {
            sender.sendMessage("§cUsage: /spear cooldown <player> <clear|status>");
        }
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6=== Magical Spears Commands ===");
        sender.sendMessage("§e/spear give <player> <spear> §7- Give a spear to player");
        sender.sendMessage("§e/spear take <player> <spear> §7- Take spear from player");
        sender.sendMessage("§e/spear all <player> §7- Give all spears to player");
        sender.sendMessage("§e/spear gui §7- Open spears selection GUI");
        sender.sendMessage("§e/spear grace <minutes> §7- Start grace period");
        sender.sendMessage("§e/spear reveal <player> <minutes> §7- Reveal player coordinates");
        sender.sendMessage("§e/spear cooldown <player> <clear|status> §7- Manage cooldowns");
        sender.sendMessage("§6Available spears: §f" + getSpearNames());
    }
    
    private String getSpearNames() {
        return Arrays.stream(MagicalSpear.values())
                .map(spear -> spear.getDisplayName().replace(" ", "_").toLowerCase())
                .collect(Collectors.joining(", "));
    }
    
    private MagicalSpear getSpearByName(String name) {
        for (MagicalSpear spear : MagicalSpear.values()) {
            if (spear.getDisplayName().replace(" ", "_").equalsIgnoreCase(name) ||
                spear.name().equalsIgnoreCase(name)) {
                return spear;
            }
        }
        return null;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.addAll(Arrays.asList("give", "take", "all", "gui", "grace", "reveal", "cooldown"));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "give":
                case "take":
                case "all":
                case "reveal":
                case "cooldown":
                    completions.addAll(Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList()));
                    break;
                case "grace":
                    completions.addAll(Arrays.asList("15", "30", "45", "60"));
                    break;
            }
        } else if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "give":
                case "take":
                    completions.addAll(Arrays.stream(MagicalSpear.values())
                            .map(spear -> spear.getDisplayName().replace(" ", "_").toLowerCase())
                            .collect(Collectors.toList()));
                    break;
                case "reveal":
                    completions.addAll(Arrays.asList("3", "5", "10", "15"));
                    break;
                case "cooldown":
                    completions.addAll(Arrays.asList("clear", "status"));
                    break;
            }
        }
        
        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
                  }
