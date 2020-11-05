package eu.revamp.system.kit;

import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.kit.event.KitApplyEvent;
import eu.revamp.system.plugin.RevampSystem;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

@AllArgsConstructor
public class KitListener implements Listener {

    private final RevampSystem plugin;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory != null) {
            String title = inventory.getTitle();
            if (title.contains("Kit Preview")) {
                event.setCancelled(true);
            }
            HumanEntity humanEntity = event.getWhoClicked();
            if (title.contains("Kit Selector") && humanEntity instanceof Player) {
                event.setCancelled(true);
                if (!Objects.equals(event.getView().getTopInventory(), event.getClickedInventory())) {
                    return;
                }
                ItemStack stack = event.getCurrentItem();
                if (stack == null || !stack.hasItemMeta()) {
                    return;
                }
                ItemMeta meta = stack.getItemMeta();
                if (!meta.hasDisplayName()) {
                    return;
                }
                Player player = (Player) humanEntity;
                String name = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
                Kit kit = this.plugin.getKitManager().getKit(name);
                if (kit == null) {
                    return;
                }
                kit.applyTo(player, false, true);
            }
        }
    }
    // TO ADD WHEN HCF CORE IS READY
/*
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onKitSign(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            BlockState state = block.getState();
            if (!(state instanceof Sign)) {
                return;
            }
            Sign sign = (Sign) state;
            String[] lines = sign.getLines();
            if (lines.length >= 2 && lines[1].contains(ChatColor.GREEN + "[Kit]")) {
                Kit kit = this.plugin.getKitManager().getKit((lines.length >= 3) ? lines[2] : null);
                if (kit == null) {
                    return;
                }
                event.setCancelled(true);
                Player player = event.getPlayer();

                FactionUser baseUser = this.plugin.getUserManager().getUser(player.getUniqueId());
                long remaining = baseUser.getRemainingKitCooldown(kit);

                String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);
                boolean applied = kit.applyTo(player, false, true) && player.hasPermission(kit.getPermissionNode());
                if (applied && remaining <= 0) {
                    fakeLines[0] = ChatColor.GREEN + "Successfully";
                    fakeLines[1] = ChatColor.GREEN + "equipped kit";
                    fakeLines[2] = kit.getDisplayName();
                    fakeLines[3] = "";
                } else {
                    fakeLines[0] = ChatColor.RED + "Failed to";
                    fakeLines[1] = ChatColor.RED + "equip kit";
                    fakeLines[2] = kit.getDisplayName();
                    fakeLines[3] = ChatColor.RED + "Check chat";
                }

                this.plugin.getSignHandler().showLines(player, sign, fakeLines, 15L, false);

            }
        }
    }*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onKitApply(KitApplyEvent event) {
        if (!event.isForce()) {
            Player player = event.getPlayer();
            Kit kit = event.getKit();
            if (!player.isOp() && !kit.isEnabled()) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "The " + kit.getDisplayName() + " kit is currently disabled.");
            } else {
                String kitPermission = kit.getPermissionNode();
                if (kitPermission != null && !player.hasPermission(kitPermission)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You do not have permission to use this kit.");
                } else {
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
                    /*
                    UUID uuid = player.getUniqueId();
                    FactionUser baseUser = this.plugin.getUserManager().getUser(uuid);
                    long remaining = baseUser.getRemainingKitCooldown(kit);*/
                    if (playerData.getGlobalCooldowns().hasCooldown(kit.getName())) {
                        player.sendMessage(ChatColor.RED + "You cannot use the " + kit.getDisplayName() + " kit for " + playerData.getGlobalCooldowns().getMiliSecondsLeft(kit.getName()) + "seconds");
                        event.setCancelled(true);
                    }
                    /*
                    else {
                        int curUses = baseUser.getKitUses(kit);
                        int maxUses = kit.getMaximumUses();
                        if (curUses >= maxUses && maxUses != Integer.MAX_VALUE) {
                            player.sendMessage(ChatColor.RED + "You have already used this kit " + curUses + '/' + maxUses + " times.");
                            event.setCancelled(true);
                        }
                    }*/
                }
            }
        }
    }
/*
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitApplyMonitor(KitApplyEvent event) {
        if (!event.isForce()) {
            Kit kit = event.getKit();
            FactionUser baseUser = this.plugin.getUserManager().getUser(event.getPlayer().getUniqueId());
            baseUser.incrementKitUses(kit);
            baseUser.updateKitCooldown(kit);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onKitApplyHigh(KitApplyEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        Faction playerFaction;
        if (this.plugin.getConfigManager().getFactionConfig().KIT_MAP) {
            if (!factionAt.isSafezone() && ((playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) == null || !playerFaction.equals(factionAt)) && !player.isOp()) {
                player.sendMessage(ChatColor.RED + "Kits can only be applied in safe-zones or your own claims.");
                event.setCancelled(true);
            }
        }
    }*/
}
