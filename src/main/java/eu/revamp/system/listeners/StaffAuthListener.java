package eu.revamp.system.listeners;

import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.plugin.RevampSystem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class StaffAuthListener implements Listener {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    private boolean isAuth(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        return playerData != null && playerData.isStaffAuth() && plugin.getCoreConfig().getBoolean("staff-auth.enabled")
                && player.hasPermission(plugin.getCoreConfig().getString("staff-auth.permission", "revampsystem.staff.auth"));
    }

    private boolean isCommandAllowed(String command) {
        for (String allowed : plugin.getCoreConfig().getStringList("auth-allowed-commands")) {
            if (command.toLowerCase().startsWith(allowed.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (this.isAuth(player)) {
            Location from = event.getFrom();
            Location to = event.getTo();
            if ((from.getX() != to.getX()) || (from.getZ() != to.getZ())) {
                player.teleport(from);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleInteract(PlayerInteractEvent event){
        if (!this.isAuth(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleBlockBreak(BlockBreakEvent event) {
        if (!this.isAuth(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleBlockPlace(BlockPlaceEvent event) {
        if (!this.isAuth(event.getPlayer())){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!this.isAuth(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleInventoryClick(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!this.isAuth(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) return;
        Player player = (Player) event.getEntered();
        if (!this.isAuth(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleEnter(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!this.isAuth(player) && !this.isCommandAllowed(event.getMessage())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Language.PREFIX.toString() + ChatColor.RED + "Please auth yourself before executing other commands!");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!this.isAuth(player)){
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void handleEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player player = (Player) event.getDamager();
        if (!this.isAuth(player)){
         event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!this.isAuth(player)){
            event.setCancelled(true);
            player.sendMessage(Language.PREFIX.toString() + ChatColor.RED + "Please auth yourself before using chat!");
        }
    }
}
