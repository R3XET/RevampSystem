package eu.revamp.system.menus.inventory;

import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.spigot.utils.time.TimeUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.menu.menu.AquaMenu;
import eu.revamp.system.menu.slots.Slot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class PlayerInventoryMenu extends AquaMenu {
    private PlayerData playerData;

    @Override
    public void onOpen(Player player) {
        this.setUpdateInTask(true);
    }

    @Override
    public void onClose(Player player) {
        plugin.getPlayerManagement().deleteData(this.playerData.getUniqueId());
    }

    @Override
    public String getName(Player player) {
        return "&7" + this.playerData.getPlayerName() + "'s inventory";
    }

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        for (int i = 0; i < 36; i++) {
            int slot = i;
            if (playerData.getOfflineInventory().getInventory()[i] == null) continue;

            int finalI = i;
            slots.add(new Slot() {
                @Override
                public ItemStack getItem(Player player) {
                    return playerData.getOfflineInventory().getInventory()[slot];
                }

                @Override
                public int getSlot() {
                    return finalI;
                }
            });
        }

        slots.add(this.playerData.getOfflineInventory().getArmor()[3].getType() == Material.AIR ? Slot.getGlass(36) : new ArmorPieceSlot(this.playerData.getOfflineInventory().getArmor()[3], 36));
        slots.add(this.playerData.getOfflineInventory().getArmor()[2].getType() == Material.AIR ? Slot.getGlass(37) : new ArmorPieceSlot(this.playerData.getOfflineInventory().getArmor()[2], 37));
        slots.add(this.playerData.getOfflineInventory().getArmor()[1].getType() == Material.AIR ? Slot.getGlass(38) : new ArmorPieceSlot(this.playerData.getOfflineInventory().getArmor()[1], 38));
        slots.add(this.playerData.getOfflineInventory().getArmor()[0].getType() == Material.AIR ? Slot.getGlass(39) : new ArmorPieceSlot(this.playerData.getOfflineInventory().getArmor()[0], 39));

        slots.add(new PotionsSlot());
        slots.add(new LocationSlot());
        slots.add(new CloseSlot());

        for (int i = 36; i < 45; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }

        return slots;
    }

    @AllArgsConstructor
    private static class ArmorPieceSlot extends Slot {
        private ItemStack stack;
        private int slot;

        @Override
        public ItemStack getItem(Player player) {
            return stack;
        }

        @Override
        public int getSlot() {
            return slot;
        }
    }

    private class LocationSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            Location location = playerData.getOfflineInventory().getLocation();

            ItemBuilder item = new ItemBuilder(Material.PAPER);
            item.setName("&bLocation");
            item.addLoreLine("&f" + CC.ARROW_RIGHT + "  &7[&3" + location.getBlockX() + "&7, &3" +
                    location.getBlockY() + "&7, &3" + location.getBlockZ() + " &7(&3" +
                    location.getWorld().getName() + "&7)]");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 43;
        }
    }

    private static class CloseSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.ARROW);
            item.setName("&4&lClose");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 44;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            player.closeInventory();
        }
    }

    private class PotionsSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.BLAZE_POWDER);
            item.setName("&bActive Potions");
            item.addLoreLine("&7&m-----------------------");
            if (playerData.getOfflineInventory().getPotionEffects().size() != 0) {
                playerData.getOfflineInventory().getPotionEffects().forEach(potionEffect -> {
                    item.addLoreLine("&b" + eu.revamp.spigot.utils.string.StringUtils.convertFirstUpperCase(potionEffect.getType().getName().toLowerCase().replace("_", " ")) + " " + (potionEffect.getAmplifier() + 1) + " &7(&3" + TimeUtils.formatTime(potionEffect.getDuration() / 20) + "&7)");
                });
            } else {
                item.addLoreLine("&3Player don't have any");
                item.addLoreLine("&3potion effects.");
            }
            item.addLoreLine("&7&m-----------------------");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 42;
        }
    }
}
