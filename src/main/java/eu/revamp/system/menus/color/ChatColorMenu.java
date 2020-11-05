package eu.revamp.system.menus.color;

import eu.revamp.spigot.utils.block.wool.WoolUtils;
import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.menu.menu.SwitchableMenu;
import eu.revamp.system.menu.slots.Slot;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class ChatColorMenu extends SwitchableMenu {

    @Override
    public String getPagesTitle(Player player) {
        return "&7Chat Color";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        Stream.of(ChatColor.values()).filter(ChatColor::isColor).forEach(chatColor -> {
            slots.add(new NameColorSlot(chatColor, player.hasPermission("revampsystem.chat.color." + CCUtils.convertChatColor(chatColor).toLowerCase())
                    || player.hasPermission("revampsystem.chat.color.all")));
        });
        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new Slot() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.REDSTONE).setName("&cReset color!").toItemStack();
            }

            @Override
            public int getSlot() {
                return 31;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                playerData.setChatColor(null);

                player.closeInventory();
                PlayerUtils.playSound(player, Sound.LEVEL_UP);
                player.sendMessage(Language.CHAT_COLOR_COLOR_RESET.toString().replace("%color%", playerData.getHighestRank().getChatColor() + CCUtils.convertChatColor(playerData.getHighestRank().getColor(), true)));
            }
        });
        return slots;
    }

    @AllArgsConstructor
    private class NameColorSlot extends Slot {
        private ChatColor chatColor;
        boolean hasPermission;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setName(chatColor + CCUtils.convertChatColor(chatColor, true));
            item.setDurability((short) WoolUtils.convertChatColorToWoolData(chatColor));
            if (hasPermission) {
                item.addLoreLine("&8&m--------------------------------");
                item.addLoreLine("&7Click to set your chat color to " + chatColor + CCUtils.convertChatColor(chatColor, true) + "&7!");
                item.addLoreLine("&8&m--------------------------------");
            } else {
                item.addLoreLine("&8&m--------------------------------");
                item.addLoreLine(" ");
                item.addLoreLine("&cYou don't have permission to use");
                item.addLoreLine("&cthis chat color, donate to use");
                item.addLoreLine("&cthis feature: &c&l" + plugin.getEssentialsManagement().getStore());
                item.addLoreLine(" ");
                item.addLoreLine("&8&m--------------------------------");
            }
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (!hasPermission) return;

            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.setChatColor(chatColor);

            player.closeInventory();
            PlayerUtils.playSound(player, Sound.LEVEL_UP);

            player.sendMessage(Language.CHAT_COLOR_COLOR_CHANGED.toString().replace("%color%", chatColor + CCUtils.convertChatColor(chatColor, true)));
        }
    }

    private class NameColorBold extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.FEATHER);
            item.setName(playerData.getNameColor() + "Name Color Bold &7(" + (playerData.isNameColorBold() ? "&aEnabled" : "&cDisabled") + "&7)");
            if (!player.hasPermission("revampsystem.name.color.bold")) {
                item.addLoreLine("&8&m--------------------------------");
                item.addLoreLine(" ");
                item.addLoreLine("&cYou don't have permission to use");
                item.addLoreLine("&cthis name color, donate to use");
                item.addLoreLine("&cthis feature: &c&l" + plugin.getEssentialsManagement().getStore());
                item.addLoreLine(" ");
                item.addLoreLine("&8&m--------------------------------");
            } else if (playerData.getNameColor() == null || playerData.getNameColor().equals(playerData.getHighestRank().getDisplayColor())) {
                item.addLoreLine("");
                item.addLoreLine("&cYou must choose color");
                item.addLoreLine("&cif you want to use this option!");
                item.addLoreLine(" ");
            } else if (!playerData.isNameColorBold()) {
                item.addLoreLine("");
                item.addLoreLine("&7Click to set your");
                item.addLoreLine("&7name bold!");
                item.addLoreLine(" ");
            } else {
                item.addLoreLine("");
                item.addLoreLine("&7Click to un-set your");
                item.addLoreLine("&7name bold!");
                item.addLoreLine(" ");
            }
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 39;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (!player.hasPermission("revampsystem.name.color.bold")) return;

            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData.getNameColor() == null || playerData.getNameColor().equals(playerData.getHighestRank().getDisplayColor())) {
                return;
            }
            playerData.setNameColorBold(!playerData.isNameColorBold());
            PlayerUtils.playSound(player, Sound.LEVEL_UP);

            update(player);
        }
    }

    private class NameColorItalic extends Slot {

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.FEATHER);
            item.setName(playerData.getNameColor() + "Name Color Italic &7(" + (playerData.isNameColorItalic() ? "&aEnabled" : "&cDisabled") + "&7)");
            if (!player.hasPermission("revampsystem.name.color.italic")) {
                item.addLoreLine("&8&m--------------------------------");
                item.addLoreLine(" ");
                item.addLoreLine("&cYou don't have permission to use");
                item.addLoreLine("&cthis name color, donate to use");
                item.addLoreLine("&cthis feature: &c&l" + plugin.getEssentialsManagement().getStore());
                item.addLoreLine(" ");
                item.addLoreLine("&8&m--------------------------------");
            } else if (playerData.getNameColor() == null || playerData.getNameColor().equals(playerData.getHighestRank().getDisplayColor())) {
                item.addLoreLine("");
                item.addLoreLine("&cYou must choose color");
                item.addLoreLine("&cif you want to use this option!");
                item.addLoreLine(" ");
            } else if (!playerData.isNameColorItalic()) {
                item.addLoreLine("");
                item.addLoreLine("&7Click to set your");
                item.addLoreLine("&7name italic!");
                item.addLoreLine(" ");
            } else {
                item.addLoreLine("");
                item.addLoreLine("&7Click to un-set your");
                item.addLoreLine("&7name italic!");
                item.addLoreLine(" ");
            }
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 41;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (!player.hasPermission("revampsystem.name.color.italic")) return;

            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData.getNameColor() == null || playerData.getNameColor().equals(playerData.getHighestRank().getDisplayColor())) {
                return;
            }
            playerData.setNameColorItalic(!playerData.isNameColorItalic());
            PlayerUtils.playSound(player, Sound.LEVEL_UP);

            update(player);
        }
    }
}
