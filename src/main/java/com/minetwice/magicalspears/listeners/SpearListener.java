package com.minetwice.magicalspears.listeners;

import com.minetwice.magicalspears.MagicalSpearsPlugin;
import com.minetwice.magicalspears.objects.MagicalSpear;
import com.minetwice.magicalspears.managers.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpearListener implements Listener {
    
    private final SpearManager spearManager = MagicalSpearsPlugin.getInstance().getSpearManager();
    private final CooldownManager cooldownManager = MagicalSpearsPlugin.getInstance().getCooldownManager();
    private final GraceManager graceManager = MagicalSpearsPlugin.getInstance().getGraceManager();
    private final ComboManager comboManager = MagicalSpearsPlugin.getInstance().getComboManager();
    
    // NEW FEATURE 3: Ultimate Ability Charge
    private final Map<UUID, Integer> ultimateCharge = new HashMap<>();
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null) return;
        
        MagicalSpear spear = MagicalSpear.fromItem(item);
        if (spear == null) return;
        
        // Check if it's a right-click (shooting projectile)
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            
            // Check cooldown
            if (cooldownManager.hasCooldown(player.getUniqueId(), spear, true)) {
                long remaining = cooldownManager.getRemainingCooldown(player.getUniqueId(), spear, true) / 1000;
                showActionBar(player, "§cShoot cooldown: " + remaining + "s");
                return;
            }
            
            // Shoot projectile based on spear type
            shootProjectile(player, spear);
            
            // Set cooldown
            cooldownManager.setCooldown(player.getUniqueId(), spear, true);
            
            // Add ultimate charge
            addUltimateCharge(player);
        }
    }
    
    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player attacker = (Player) event.getDamager();
        Player target = (Player) event.getEntity();
        
        // Check grace period
        if (graceManager.isGracePeriodActive()) {
            event.setCancelled(true);
            showActionBar(attacker, "§cPvP is disabled during grace period!");
            return;
        }
        
        ItemStack weapon = attacker.getInventory().getItemInMainHand();
        MagicalSpear spear = MagicalSpear.fromItem(weapon);
        
        if (spear == null) return;
        
        // Check melee cooldown - BLOCK ATTACK if on cooldown
        if (cooldownManager.hasCooldown(attacker.getUniqueId(), spear, false)) {
            long remaining = cooldownManager.getRemainingCooldown(attacker.getUniqueId(), spear, false) / 1000;
            showActionBar(attacker, "§cMelee cooldown: " + remaining + "s");
            event.setCancelled(true);
            return;
        }
        
        // Set melee cooldown
        cooldownManager.setCooldown(attacker.getUniqueId(), spear, false);
        
        // Melee attack effects
        applyMeleeEffects(attacker, target, spear);
        
        // Register combo
        comboManager.registerHit(attacker, spear);
        
        // Add experience
        spearManager.addSpearExperience(attacker, spear, 5);
        
        // Add ultimate charge
        addUltimateCharge(attacker);
        
        // Show coordinates if reveal is active
        if (spearManager.hasRevealCooldown(target)) {
            showCoordinates(target);
        }
        
        // Set combat timer
        graceManager.setCombat(attacker);
        graceManager.setCombat(target);
    }
    
    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        
        if (newItem != null) {
            MagicalSpear spear = MagicalSpear.fromItem(newItem);
            if (spear != null) {
                // Show cooldown status when switching to spear
                String status = cooldownManager.getCooldownDisplay(player, spear);
                showActionBar(player, status);
                
                // Show ultimate charge
                int charge = ultimateCharge.getOrDefault(player.getUniqueId(), 0);
                if (charge >= 100) {
                    player.sendMessage("§6⚡ Ultimate Ability Ready! §eRight-click while sneaking!");
                }
            }
        }
    }
    
    private void addUltimateCharge(Player player) {
        UUID playerId = player.getUniqueId();
        int currentCharge = ultimateCharge.getOrDefault(playerId, 0);
        int newCharge = Math.min(currentCharge + 15, 100);
        ultimateCharge.put(playerId, newCharge);
        
        if (newCharge == 100) {
            player.sendMessage("§6⚡ Ultimate Ability Charged! §eRight-click while sneaking to use!");
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
        }
    }
    
    private void shootProjectile(Player player, MagicalSpear spear) {
        // Check for ultimate ability (sneaking + right-click)
        if (player.isSneaking() && ultimateCharge.getOrDefault(player.getUniqueId(), 0) >= 100) {
            activateUltimateAbility(player, spear);
            ultimateCharge.put(player.getUniqueId(), 0);
            return;
        }
        
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        
        switch (spear) {
            case LIGHTNING_SPEAR:
                shootLightningProjectile(player, eyeLocation, direction);
                break;
            case ICE_SPEAR:
                shootIceProjectile(player, eyeLocation, direction);
                break;
            case FIRE_SPEAR:
                shootFireProjectile(player, eyeLocation, direction);
                break;
            case VOID_SPEAR:
                shootVoidProjectile(player, eyeLocation, direction);
                break;
            case LIFE_SPEAR:
                shootLifeProjectile(player, eyeLocation, direction);
                break;
            case POISON_SPEAR:
                shootPoisonProjectile(player, eyeLocation, direction);
                break;
        }
        
        // Play sound and particles
        player.getWorld().playSound(player.getLocation(), spear.getSound(), 1.0f, 1.0f);
        showActionBar(player, "§6" + spear.getDisplayName() + " §eprojectile launched!");
    }
    
    private void activateUltimateAbility(Player player, MagicalSpear spear) {
        player.sendMessage("§6⚡ ULTIMATE ABILITY! §e" + spear.getDisplayName() + " §6activated!");
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
        
        // Spawn giant particles
        for (int i = 0; i < 50; i++) {
            player.getWorld().spawnParticle(Particle.FIREWORK, player.getLocation(), 20, 3, 3, 3, 0.1);
        }
        
        switch (spear) {
            case LIGHTNING_SPEAR:
                // Lightning storm
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        player.getWorld().strikeLightningEffect(entity.getLocation());
                        ((LivingEntity) entity).damage(10.0, player);
                    }
                }
                break;
                
            case ICE_SPEAR:
                // Ice age
                for (int x = -8; x <= 8; x++) {
                    for (int z = -8; z <= 8; z++) {
                        Location loc = player.getLocation().add(x, 0, z);
                        if (loc.getBlock().getType() == Material.AIR) {
                            loc.getBlock().setType(Material.ICE);
                        }
                    }
                }
                for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 3));
                    }
                }
                break;
                
            case FIRE_SPEAR:
                // Fire explosion
                player.getWorld().createExplosion(player.getLocation(), 6.0f, true, false, player);
                for (Entity entity : player.getNearbyEntities(8, 8, 8)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        ((LivingEntity) entity).setFireTicks(200);
                    }
                }
                break;
                
            case VOID_SPEAR:
                // Void vortex
                for (Entity entity : player.getNearbyEntities(12, 12, 12)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        Location randomLoc = findSafeLocation(entity.getLocation(), 15);
                        entity.teleport(randomLoc);
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0));
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 120, 1));
                    }
                }
                break;
                
            case LIFE_SPEAR:
                // Healing wave
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof Player) {
                        Player targetPlayer = (Player) entity;
                        targetPlayer.setHealth(Math.min(targetPlayer.getHealth() + 8.0, targetPlayer.getMaxHealth()));
                        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
                        targetPlayer.sendMessage("§aYou were healed by " + player.getName() + "'s ultimate!");
                    }
                }
                break;
                
            case POISON_SPEAR:
                // Poison nova
                for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
                    if (entity instanceof LivingEntity && entity != player) {
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 2));
                        ((LivingEntity) entity).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
                    }
                }
                break;
        }
    }
    
    private void shootLightningProjectile(Player player, Location location, Vector direction) {
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(direction.multiply(2.0));
        
        new BukkitRunnable() {
            public void run() {
                if (projectile.isDead() || !projectile.isValid()) {
                    this.cancel();
                    return;
                }
                Location projLoc = projectile.getLocation();
                player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, projLoc, 5, 0.2, 0.2, 0.2, 0.1);
                player.getWorld().spawnParticle(Particle.CRIT, projLoc, 3, 0.1, 0.1, 0.1, 0.1);
            }
        }.runTaskTimer(MagicalSpearsPlugin.getInstance(), 0L, 1L);
        
        handleProjectileHit(projectile, (hitEntity, hitLocation) -> {
            // Strike lightning
            player.getWorld().strikeLightningEffect(hitLocation);
            
            // Damage and knockback
            if (hitEntity instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) hitEntity;
                entity.damage(8.0, player);
                
                // Strong knockback
                Vector knockback = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                entity.setVelocity(knockback.multiply(2.0).setY(0.8));
                
                // Chain lightning to nearby entities
                for (Entity nearby : entity.getNearbyEntities(5, 3, 5)) {
                    if (nearby instanceof LivingEntity && nearby != entity && nearby != player) {
                        player.getWorld().strikeLightningEffect(nearby.getLocation());
                        ((LivingEntity) nearby).damage(4.0, player);
                    }
                }
            }
        });
    }
    
    private void shootIceProjectile(Player player, Location location, Vector direction) {
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(direction.multiply(1.8));
        
        // Ice trail effect
        new BukkitRunnable() {
            public void run() {
                if (projectile.isDead() || !projectile.isValid()) {
                    this.cancel();
                    return;
                }
                Location projLoc = projectile.getLocation();
                player.getWorld().spawnParticle(Particle.SNOWFLAKE, projLoc, 8, 0.2, 0.2, 0.2, 0.1);
                
                // Create ice blocks on ground
                if (projLoc.getBlock().getType() == Material.AIR && 
                    projLoc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
                    projLoc.getBlock().setType(Material.FROSTED_ICE);
                }
            }
        }.runTaskTimer(MagicalSpearsPlugin.getInstance(), 0L, 2L);
        
        handleProjectileHit(projectile, (hitEntity, hitLocation) -> {
            // Freeze effect
            if (hitEntity instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) hitEntity;
                entity.damage(6.0, player);
                
                // Freeze effects
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 3));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
                
                // Knockback
                Vector knockback = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                entity.setVelocity(knockback.multiply(1.5).setY(0.6));
                
                // Create ice area
                createIceArea(hitLocation);
            }
        });
    }
    
    private void shootFireProjectile(Player player, Location location, Vector direction) {
        Fireball fireball = player.launchProjectile(Fireball.class);
        fireball.setVelocity(direction.multiply(1.5));
        fireball.setIsIncendiary(false); // We'll handle explosion ourselves
        
        // Fire trail
        new BukkitRunnable() {
            public void run() {
                if (fireball.isDead() || !fireball.isValid()) {
                    this.cancel();
                    return;
                }
                player.getWorld().spawnParticle(Particle.FLAME, fireball.getLocation(), 5, 0.2, 0.2, 0.2, 0.1);
            }
        }.runTaskTimer(MagicalSpearsPlugin.getInstance(), 0L, 1L);
        
        handleProjectileHit(fireball, (hitEntity, hitLocation) -> {
            // Fire explosion
            player.getWorld().createExplosion(hitLocation, 3.0f, true, false, player);
            
            // Set fire to nearby entities
            for (Entity nearby : hitLocation.getWorld().getNearbyEntities(hitLocation, 4, 3, 4)) {
                if (nearby instanceof LivingEntity && nearby != player) {
                    ((LivingEntity) nearby).setFireTicks(100); // 5 seconds
                }
            }
        });
    }
    
    private void shootVoidProjectile(Player player, Location location, Vector direction) {
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(direction.multiply(2.2));
        
        // Void trail
        new BukkitRunnable() {
            public void run() {
                if (projectile.isDead() || !projectile.isValid()) {
                    this.cancel();
                    return;
                }
                Location projLoc = projectile.getLocation();
                player.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, projLoc, 6, 0.2, 0.2, 0.2, 0.1);
                player.getWorld().spawnParticle(Particle.PORTAL, projLoc, 3, 0.3, 0.3, 0.3, 0.1);
            }
        }.runTaskTimer(MagicalSpearsPlugin.getInstance(), 0L, 1L);
        
        handleProjectileHit(projectile, (hitEntity, hitLocation) -> {
            if (hitEntity instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) hitEntity;
                
                // Drain health
                double drainAmount = 5.0;
                entity.damage(drainAmount, player);
                player.setHealth(Math.min(player.getHealth() + 3.0, player.getMaxHealth()));
                
                // Teleport and negative effects
                Location randomLoc = findSafeLocation(entity.getLocation(), 8);
                entity.teleport(randomLoc);
                
                entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 0));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 1));
                
                player.sendMessage("§5Drained health from " + (entity instanceof Player ? ((Player) entity).getName() : "enemy"));
            }
        });
    }
    
    private void shootLifeProjectile(Player player, Location location, Vector direction) {
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(direction.multiply(1.8));
        
        // Life energy trail
        new BukkitRunnable() {
            public void run() {
                if (projectile.isDead() || !projectile.isValid()) {
                    this.cancel();
                    return;
                }
                Location projLoc = projectile.getLocation();
                player.getWorld().spawnParticle(Particle.HEART, projLoc, 3, 0.2, 0.2, 0.2, 0.1);
                player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, projLoc, 4, 0.3, 0.3, 0.3, 0.1);
            }
        }.runTaskTimer(MagicalSpearsPlugin.getInstance(), 0L, 2L);
        
        handleProjectileHit(projectile, (hitEntity, hitLocation) -> {
            if (hitEntity instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) hitEntity;
                
                // Drain health and heal player
                double drainAmount = 7.0;
                double currentHealth = entity.getHealth();
                double damage = Math.min(drainAmount, currentHealth - 1); // Don't kill, leave at least 0.5 heart
                
                if (damage > 0) {
                    entity.damage(damage, player);
                    player.setHealth(Math.min(player.getHealth() + damage, player.getMaxHealth()));
                    
                    // Healing effects
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 0));
                    
                    player.sendMessage("§aDrained §c" + String.format("%.1f", damage) + "❤ §afrom enemy!");
                }
            }
        });
    }
    
    private void shootPoisonProjectile(Player player, Location location, Vector direction) {
        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(direction.multiply(1.7));
        
        // Poison trail
        new BukkitRunnable() {
            public void run() {
                if (projectile.isDead() || !projectile.isValid()) {
                    this.cancel();
                    return;
                }
                Location projLoc = projectile.getLocation();
                player.getWorld().spawnParticle(Particle.SQUID_INK, projLoc, 6, 0.2, 0.2, 0.2, 0.1); // CHANGED: SPELL -> SQUID_INK
                player.getWorld().spawnParticle(Particle.SMOKE, projLoc, 3, 0.3, 0.3, 0.3, 0.1);
            }
        }.runTaskTimer(MagicalSpearsPlugin.getInstance(), 0L, 1L);
        
        handleProjectileHit(projectile, (hitEntity, hitLocation) -> {
            // Create poison cloud
            createPoisonCloud(hitLocation, player);
            
            if (hitEntity instanceof LivingEntity) {
                LivingEntity entity = (LivingEntity) hitEntity;
                entity.damage(4.0, player);
                
                // Poison effects
                entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 2));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1));
                
                // Weak knockback
                Vector knockback = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                entity.setVelocity(knockback.multiply(1.2).setY(0.4));
            }
        });
    }
    
    // Helper method to handle projectile hits
    private void handleProjectileHit(Projectile projectile, ProjectileHitCallback callback) {
        new BukkitRunnable() {
            public void run() {
                if (projectile.isDead() || !projectile.isValid()) {
                    this.cancel();
                    // If projectile didn't hit entity but still expired, trigger at location
                    if (projectile.getLocation() != null) {
                        callback.onHit(null, projectile.getLocation());
                    }
                    return;
                }
                
                // Check for entity hits
                for (Entity entity : projectile.getNearbyEntities(1.5, 1.5, 1.5)) {
                    if (entity instanceof LivingEntity && entity != ((LivingEntity) projectile.getShooter())) {
                        callback.onHit((LivingEntity) entity, entity.getLocation());
                        projectile.remove();
                        this.cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(MagicalSpearsPlugin.getInstance(), 1L, 1L);
    }
    
    // Interface for projectile hit callbacks
    private interface ProjectileHitCallback {
        void onHit(LivingEntity hitEntity, Location hitLocation);
    }
    
    // Helper methods
    private Location findSafeLocation(Location center, double radius) {
        for (int i = 0; i < 10; i++) {
            Location randomLoc = center.clone().add(
                (Math.random() - 0.5) * radius * 2,
                0,
                (Math.random() - 0.5) * radius * 2
            );
            randomLoc.setY(center.getWorld().getHighestBlockYAt(randomLoc) + 1);
            
            // Check if location is safe
            if (randomLoc.getBlock().getType() == Material.AIR && 
                randomLoc.clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
                return randomLoc;
            }
        }
        return center; // Fallback to original location
    }
    
    private void createIceArea(Location center) {
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                Location loc = center.clone().add(x, 0, z);
                if (loc.getBlock().getType() == Material.AIR && 
                    loc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
                    loc.getBlock().setType(Material.FROSTED_ICE);
                }
            }
        }
    }
    
    private void createPoisonCloud(Location center, Player owner) {
        AreaEffectCloud cloud = center.getWorld().spawn(center, AreaEffectCloud.class);
        cloud.setRadius(4.0f);
        cloud.setDuration(200); // 10 seconds
        cloud.setParticle(Particle.SQUID_INK); // CHANGED: SPELL -> SQUID_INK
        cloud.setColor(Color.GREEN);
        cloud.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 1), true);
        cloud.setSource(owner);
    }
    
    private void applyMeleeEffects(Player attacker, Player target, MagicalSpear spear) {
        // Basic melee effects (weaker than projectile)
        switch (spear) {
            case LIGHTNING_SPEAR:
                target.getWorld().strikeLightningEffect(target.getLocation());
                target.damage(3.0);
                break;
            case ICE_SPEAR:
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
                break;
            case FIRE_SPEAR:
                target.setFireTicks(60);
                break;
            case VOID_SPEAR:
                // Small health drain
                target.damage(2.0, attacker);
                attacker.setHealth(Math.min(attacker.getHealth() + 1.0, attacker.getMaxHealth()));
                break;
            case LIFE_SPEAR:
                // Small health drain
                target.damage(2.0, attacker);
                attacker.setHealth(Math.min(attacker.getHealth() + 2.0, attacker.getMaxHealth()));
                break;
            case POISON_SPEAR:
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0));
                break;
        }
        
        attacker.sendMessage("§6" + spear.getDisplayName() + " §emelee effect!");
    }
    
    private void showCoordinates(Player target) {
        String coordMessage = String.format("§c%s's Location: §fX:%.1f Y:%.1f Z:%.1f",
            target.getName(),
            target.getLocation().getX(),
            target.getLocation().getY(),
            target.getLocation().getZ());
        
        // Show to all players
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendActionBar(net.kyori.adventure.text.Component.text(coordMessage));
        }
    }
    
    private void showActionBar(Player player, String message) {
        player.sendActionBar(net.kyori.adventure.text.Component.text(message));
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacySection().serialize(event.getView().title());
        
        if (title.contains("Magical Spears")) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) return;
            
            // Handle close button
            if (event.getCurrentItem().getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }
            
            // Give spear when clicked
            MagicalSpear spear = MagicalSpear.fromItem(event.getCurrentItem());
            if (spear != null) {
                spearManager.giveSpear(player, spear);
                player.closeInventory();
            }
        }
    }
}
