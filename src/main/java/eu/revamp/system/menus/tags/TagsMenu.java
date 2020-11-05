package eu.revamp.system.menus.tags;

import eu.revamp.spigot.utils.block.wool.WoolUtils;
import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.api.tags.Tag;
import eu.revamp.system.enums.Language;
import eu.revamp.system.menu.menu.SwitchableMenu;
import eu.revamp.system.menu.slots.Slot;
import eu.revamp.spigot.utils.generic.Tasks;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class TagsMenu extends SwitchableMenu {

    @Override
    public String getPagesTitle(Player player) {
        return "&7Tags";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        plugin.getTagManagement().getTags().stream().sorted(Comparator.comparingInt(Tag::getWeight).reversed()).forEach(tag -> {
            slots.add(new TagSlot(tag));
        });

        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        slots.add(new Slot() {

            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.ARROW).setName("&c&lGo Back!").toItemStack();
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                new TagsMainMenu().open(player);
            }

            @Override
            public int getSlot() {
                return 41;
            }

            @Override
            public int[] getSlots() {
                return new int[]{39};
            }
        });

        return slots;
    }


    @AllArgsConstructor
    private class TagSlot extends Slot {
        private Tag tag;

        @Override
        public ItemStack getItem(Player player) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setName(tag.getColor() + tag.getName());
            item.setDurability((short) WoolUtils.convertChatColorToWoolData(tag.getColor()));
            if (player.hasPermission("revampsystem.tags." + tag.getName().toLowerCase()) || player.hasPermission("revampsystem.tags.all")) {
                plugin.getCoreConfig().getStringList("tag-item-lore-have-access").forEach(message -> {
                    Replacement replacement = new Replacement(message);
                    replacement.add("%tag%", tag.getName());
                    replacement.add("%store%", plugin.getEssentialsManagement().getStore());
                    replacement.add("%tagFormat%", tag.getFormat());
                    replacement.add("%chatFormat%", this.getChatFormat(player, tag));
                    item.addLoreLine(replacement.toString());
                });
            } else {
                plugin.getCoreConfig().getStringList("tag-item-lore-have-not-access").forEach(message -> {
                    Replacement replacement = new Replacement(message);
                    replacement.add("%tag%", tag.getName());
                    replacement.add("%store%", plugin.getEssentialsManagement().getStore());
                    replacement.add("%tagFormat%", tag.getFormat());
                    replacement.add("%chatFormat%", this.getChatFormat(player, tag));
                    item.addLoreLine(replacement.toString());
                });
            }
            return item.toItemStack();
        }

        private String getChatFormat(Player player, Tag tag) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            Replacement replacement;
            if (!plugin.isRevampHCFEnabled()){
                replacement = new Replacement(plugin.getCoreConfig().getString("chat-format.format"));
            }
            else {
                replacement = new Replacement(plugin.getCoreConfig().getString("chat-format.format").substring(12));
            }
            replacement.add("%prefix%", playerData.getHighestRank().getPrefix());
            replacement.add("%suffix%", playerData.getHighestRank().getSuffix());
            replacement.add("%player%", playerData.getPlayerName());
            replacement.add("%nameColor%", playerData.getNameColor() != null ? playerData.getNameColor() : playerData.getHighestRank().getColor().toString());
            replacement.add("%tag%", tag.getFormat());
            replacement.add("%message%", "Your Chat Message");
            return replacement.toString();
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (!player.hasPermission("revampsystem.tags." + tag.getName().toLowerCase())) return;
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (playerData.getTag() != null && playerData.getTag().getName().equalsIgnoreCase(tag.getName())) {
                player.sendMessage(Language.TAGS_ALREADY_HAVE_APPLIED.toString().replace("%tag%", tag.getName()));
                return;
            }
            playerData.setTag(tag.getName());
            if (playerData.getTagColor() == null || playerData.getTagColor().equals("")) {
                playerData.setTagColor(CCUtils.convertChatColor(tag.getColor()));
            }
            player.closeInventory();
            PlayerUtils.playSound(player, Sound.LEVEL_UP);
            player.sendMessage(Language.TAGS_TAG_APPLIED.toString().replace("%tag%", tag.getName()));

            Tasks.runAsync(plugin, playerData::saveData);
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
