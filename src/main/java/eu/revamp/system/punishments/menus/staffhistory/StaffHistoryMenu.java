package eu.revamp.system.punishments.menus.staffhistory;

import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.data.other.punishments.PunishHistory;
import eu.revamp.system.menu.menu.AquaMenu;
import eu.revamp.system.menu.slots.Slot;
import eu.revamp.system.menus.slots.PlayerInfoSlot;
import eu.revamp.system.punishments.PunishmentPlugin;
import eu.revamp.system.punishments.utilities.punishments.PunishmentType;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class StaffHistoryMenu extends AquaMenu {
    private PlayerData playerData;

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new PlayerInfoSlot(playerData, 4));

        slots.add(new BansSlot());
        slots.add(new BlacklistsSlot());
        slots.add(new MutesSlot());
        slots.add(new WarnsSlot());
        slots.add(new KicksSlot());

        for (int i = 0; i < 36; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }
        return slots;
    }

    @Override
    public String getName(Player player) {
        return "&7Punishments";
    }

    @AllArgsConstructor
    private class BansSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            int active = PunishHistory.getPunishments(playerData, PunishmentType.BAN, true).size();
            int all = PunishHistory.getPunishments(playerData, PunishmentType.BAN, false).size();

            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability((short) 6);
            item.setName("&bBans");
            item.addLoreLine("");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Bans performed&7: " + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + PunishmentPlugin.SECONDARY_COLOR + active + PunishmentPlugin.MIDDLE_COLOR + "/" + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 18;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            int all = PunishHistory.getPunishments(playerData, PunishmentType.BAN, false).size();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.BAN).open(player);
        }
    }

    @AllArgsConstructor
    private class BlacklistsSlot extends Slot {
        @Override
        public ItemStack getItem(Player player) {
            int active = PunishHistory.getPunishments(playerData, PunishmentType.BLACKLIST, true).size();
            int all = PunishHistory.getPunishments(playerData, PunishmentType.BLACKLIST, false).size();

            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability((short) 14);
            item.setName("&4Blacklists");
            item.addLoreLine("");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Blacklists performed&7: " + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + PunishmentPlugin.SECONDARY_COLOR + active + PunishmentPlugin.MIDDLE_COLOR + "/" + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 20;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            int all = PunishHistory.getPunishments(playerData, PunishmentType.BLACKLIST, false).size();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.BLACKLIST).open(player);
        }
    }

    @AllArgsConstructor
    private class MutesSlot extends Slot {
        @Override
        public ItemStack getItem(Player player) {
            int active = PunishHistory.getPunishments(playerData, PunishmentType.MUTE, true).size();
            int all = PunishHistory.getPunishments(playerData, PunishmentType.MUTE, false).size();

            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability((short) 8);
            item.setName("&eMutes");
            item.addLoreLine("");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Mutes performed&7: " + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + PunishmentPlugin.SECONDARY_COLOR + active + PunishmentPlugin.MIDDLE_COLOR + "/" + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 22;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            int all = PunishHistory.getPunishments(playerData, PunishmentType.MUTE, false).size();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.MUTE).open(player);
        }
    }

    @AllArgsConstructor
    private class KicksSlot extends Slot {
        @Override
        public ItemStack getItem(Player player) {
            int active = PunishHistory.getPunishments(playerData, PunishmentType.KICK, true).size();
            int all = PunishHistory.getPunishments(playerData, PunishmentType.KICK, false).size();

            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability((short) 12);
            item.setName("&3Kicks");
            item.addLoreLine("");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Kicks performed&7: " + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + PunishmentPlugin.SECONDARY_COLOR + active + PunishmentPlugin.MIDDLE_COLOR + "/" + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            int all = PunishHistory.getPunishments(playerData, PunishmentType.KICK, false).size();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.KICK).open(player);
        }
    }

    @AllArgsConstructor
    private class WarnsSlot extends Slot {
        @Override
        public ItemStack getItem(Player player) {
            int active = PunishHistory.getPunishments(playerData, PunishmentType.WARN, true).size();
            int all = PunishHistory.getPunishments(playerData, PunishmentType.WARN, false).size();

            ItemBuilder item = new ItemBuilder(Material.INK_SACK);
            item.setDurability((short) 7);
            item.setName("&dWarns");
            item.addLoreLine("");
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Warns performed&7: " + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine(PunishmentPlugin.MAIN_COLOR + "Active&7: " + PunishmentPlugin.SECONDARY_COLOR + active + PunishmentPlugin.MIDDLE_COLOR + "/" + PunishmentPlugin.SECONDARY_COLOR + all);
            item.addLoreLine("");

            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 24;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            int all = PunishHistory.getPunishments(playerData, PunishmentType.WARN, false).size();
            if (all == 0) return;
            new StaffHistoryPunishmentMenu(playerData, PunishmentType.WARN).open(player);
        }
    }
}
