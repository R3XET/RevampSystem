package eu.revamp.system.menus.settings;

import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.menu.menu.AquaMenu;
import eu.revamp.system.menu.slots.Slot;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu extends AquaMenu {

    @Override
    public String getName(Player player) {
        return "&7Settings";
    }

    @Override
    public List<Slot> getSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new ToggleMessagesSlot());
        slots.add(new TimeSlot());
        slots.add(new ToggleMessagesSoundsSlot());
        slots.add(new GlobalChatSlot());
        slots.add(new ChatMentionSlot());
        slots.add(new TipsAlertsSlot());

        for (int i = 0; i < 45; i++) {
            if (!Slot.hasSlot(slots, i)) {
                slots.add(Slot.getGlass(i));
            }
        }

        return slots;
    }

    private class ToggleMessagesSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.EMERALD);
            item.setName("&aPrivate Messages");
            item.addLoreLine("&7Dou you want to see your");
            item.addLoreLine("&7private messages?");
            item.addLoreLine(" ");
            if (playerData.getMessageSystem().isMessagesToggled()) {
                item.addLoreLine(CC.X + " &aYes");
                item.addLoreLine("&eNo");
            } else {
                item.addLoreLine("&eYes");
                item.addLoreLine(CC.X + " &aNo");
            }
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 10;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            playerData.getMessageSystem().setMessagesToggled(!playerData.getMessageSystem().isMessagesToggled());
            PlayerUtils.playSound(player, Sound.NOTE_PIANO);

            update(player);
        }
    }

    private class ToggleMessagesSoundsSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.NOTE_BLOCK);
            item.setName("&aPrivate Messages Sounds");
            item.addLoreLine("&7Dou you want to hear your");
            item.addLoreLine("&7private messages sounds?");
            item.addLoreLine(" ");
            if (playerData.getMessageSystem().isSoundsEnabled()) {
                item.addLoreLine(CC.X + " &aYes");
                item.addLoreLine("&eNo");
            } else {
                item.addLoreLine("&eYes");
                item.addLoreLine(CC.X + " &aNo");
            }
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            playerData.getMessageSystem().setSoundsEnabled(!playerData.getMessageSystem().isSoundsEnabled());
            PlayerUtils.playSound(player, Sound.NOTE_PIANO);

            update(player);
        }
    }

    private class TimeSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.BLAZE_POWDER);
            item.setName("&aWorld");
            item.addLoreLine("&7Which world time you would like");
            item.addLoreLine("&7to be set for you?");
            item.addLoreLine(" ");
            switch (playerData.getWorldTime()) {
                case "DAY":
                    item.addLoreLine(CC.X + " &aDay      &eSunset");
                    item.addLoreLine("&eNight      &eReset");
                    break;
                case "SUNSET":
                    item.addLoreLine("&eDay      " + CC.X + " &aSunset");
                    item.addLoreLine("&eNight      &eReset");
                    break;
                case "NIGHT":
                    item.addLoreLine("&eDay      &eSunset");
                    item.addLoreLine(CC.X + " &aNight      &eReset");
                    break;
                default:
                    item.addLoreLine("&eDay      &eSunset");
                    item.addLoreLine("&eNight      " + CC.X + " &aReset");
                    break;
            }
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 16;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            switch (playerData.getWorldTime()) {
                case "DAY":
                    playerData.setWorldTime("SUNSET");
                    player.setPlayerTime(12500L, false);
                    break;
                case "SUNSET":
                    playerData.setWorldTime("NIGHT");
                    player.setPlayerTime(20000L, false);
                    break;
                case "NIGHT":
                    playerData.setWorldTime("RESET");
                    player.resetPlayerTime();
                    break;
                default:
                    playerData.setWorldTime("DAY");
                    player.setPlayerTime(0L, false);
                    break;
            }
            PlayerUtils.playSound(player, Sound.NOTE_PIANO);
            update(player);
        }
    }

    private class GlobalChatSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item  = new ItemBuilder(Material.PAPER);
            item.setName("&aGlobal Chat");
            item.addLoreLine("&7Dou you want to see global");
            item.addLoreLine("&7chat messages?");
            item.addLoreLine(" ");
            if (playerData.getMessageSystem().isGlobalChat()) {
                item.addLoreLine(CC.X + " &aYes");
                item.addLoreLine("&eNo");
            } else {
                item.addLoreLine("&eYes");
                item.addLoreLine(CC.X + " &aNo");
            }
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 28;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            playerData.getMessageSystem().setGlobalChat(!playerData.getMessageSystem().isGlobalChat());
            PlayerUtils.playSound(player, Sound.NOTE_PIANO);

            update(player);
        }
    }

    private class ChatMentionSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item  = new ItemBuilder(Material.CACTUS);
            item.setName("&aChat Mention");
            item.addLoreLine("&7Dou you want to be alerted when");
            item.addLoreLine("&7someone mention you in a chat?");
            item.addLoreLine(" ");
            if (playerData.getMessageSystem().isChatMention()) {
                item.addLoreLine(CC.X + " &aYes");
                item.addLoreLine("&eNo");
            } else {
                item.addLoreLine("&eYes");
                item.addLoreLine(CC.X + " &aNo");
            }
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 31;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            playerData.getMessageSystem().setChatMention(!playerData.getMessageSystem().isChatMention());
            PlayerUtils.playSound(player, Sound.NOTE_PIANO);

            update(player);
        }
    }

    private class TipsAlertsSlot extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item  = new ItemBuilder(Material.EXP_BOTTLE);
            item.setName("&aTips Alerts");
            item.addLoreLine("&7Dou you want to see");
            item.addLoreLine("&7tips alerts?");
            item.addLoreLine(" ");
            if (playerData.isTipsAlerts()) {
                item.addLoreLine(CC.X + " &aYes");
                item.addLoreLine("&eNo");
            } else {
                item.addLoreLine("&eYes");
                item.addLoreLine(CC.X + " &aNo");
            }
            item.addLoreLine(" ");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 34;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            playerData.setTipsAlerts(!playerData.isTipsAlerts());
            PlayerUtils.playSound(player, Sound.NOTE_PIANO);

            update(player);
        }
    }
}
