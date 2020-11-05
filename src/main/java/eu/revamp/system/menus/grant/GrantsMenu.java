package eu.revamp.system.menus.grant;

import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.date.DateUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.api.rank.RankData;
import eu.revamp.system.api.rank.grant.Grant;
import eu.revamp.system.data.grant.GrantSerilization;
import eu.revamp.system.database.redis.other.bson.JsonChain;
import eu.revamp.system.database.redis.payload.action.JedisAction;
import eu.revamp.system.menu.menu.SwitchableMenu;
import eu.revamp.system.menu.slots.Slot;
import eu.revamp.system.menus.slots.PlayerInfoSlot;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class GrantsMenu extends SwitchableMenu {
    private final PlayerData targetData;
    private boolean activeOnly = true;

    private Comparator<Grant> GRANT_COMPARATOR = Comparator.comparingLong(Grant::getAddedAt).reversed();

    {
        setUpdateInTask(true);
    }

    @Override
    public String getPagesTitle(Player player) {
        return "&7" + targetData.getPlayerName() + "'s grants";
    }

    @Override
    public void onOpen(Player player) {
        setUpdateInTask(true);
    }

    @Override
    public void onClose(Player player) {
        plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        if (activeOnly) {
            this.targetData.getGrants().stream().sorted(GRANT_COMPARATOR).filter(grant -> !grant.hasExpired()).forEach(grant -> {
                slots.add(new GrantSlot(grant));
            });
        } else {
            this.targetData.getGrants().stream().sorted(GRANT_COMPARATOR).forEach(grant -> {
                slots.add(new GrantSlot(grant));
            });
        }

        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new ActiveOnlySlot(40));
        slots.add(new PlayerInfoSlot(targetData, 4));
        return slots;
    }

    @AllArgsConstructor
    private class ActiveOnlySlot extends Slot {
        private int slot;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.ARROW);
            item.setName("&aGrants to show");
            item.addLoreLine(" ");
            if (activeOnly) {
                item.addLoreLine("&7Currently showing");
                item.addLoreLine("&7active grants only!");
            } else {
                item.addLoreLine("&7Currently showing all");
                item.addLoreLine("&7active/expired grants!");
            }
            item.addLoreLine(" ");
            item.addLoreLine("&aClick to change!");
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            activeOnly = !activeOnly;
            update(player);
            PlayerUtils.playSound(player, Sound.ORB_PICKUP);
        }
    }

    @AllArgsConstructor
    private class GrantSlot extends Slot {
        private Grant grant;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            RankData rankData = plugin.getRankManagement().getRank(grant.getRankName());
            if (rankData != null) {
                item.setName(rankData.getDisplayName());
            } else {
                item.setName("&a" + grant.getRankName());
            }
            item.setDurability((short) (grant.isActiveSomewhere() && rankData != null && !rankData.isDefaultRank() ? 1 : grant.hasExpired() ? 14 : rankData != null && rankData.isDefaultRank() ? 4 : 5));

            plugin.getCoreConfig().getStringList("grant-item-lore").forEach(message -> {
                Replacement replacement = new Replacement(message);
                replacement.add("%removedBy%", grant.getRemovedBy());
                replacement.add("%removedAt%", DateUtils.getDate(grant.getRemovedAt()));
                replacement.add("%addedBy%", grant.getAddedBy());
                replacement.add("%addedAt%", DateUtils.getDate(grant.getAddedAt()));
                replacement.add("%duration%", grant.getNiceDuration());
                replacement.add("%isActive%", (grant.hasExpired()  ? "&cNo" : "&2Yes"));
                replacement.add("%expire%", grant.getNiceExpire());
                replacement.add("%server%", grant.getServer());
                replacement.add("%reason%", grant.getReason());

                item.addLoreLine(replacement.toString());
            });
            if (!grant.hasExpired() && rankData != null && !rankData.isDefaultRank()) {
                plugin.getCoreConfig().getStringList("grant-item-lore-if-not-expired").forEach(message -> {
                    Replacement replacement = new Replacement(message);
                    replacement.add("%removedBy%", grant.getRemovedBy());
                    replacement.add("%removedAt%", DateUtils.getDate(grant.getRemovedAt()));
                    replacement.add("%addedBy%", grant.getAddedBy());
                    replacement.add("%addedAt%", DateUtils.getDate(grant.getAddedAt()));
                    replacement.add("%duration%", grant.getNiceDuration());
                    replacement.add("%isActive%", (grant.hasExpired() ? "&cNo" : "&2Yes"));
                    replacement.add("%expire%", grant.getNiceExpire());
                    replacement.add("%reason%", grant.getReason());
                    replacement.add("%server%", grant.getServer());

                    item.addLoreLine(replacement.toString());
                });
            }
            if (grant.getRemovedBy() != null) {
                plugin.getCoreConfig().getStringList("grant-item-lore-if-removed").forEach(message -> {
                    Replacement replacement = new Replacement(message);
                    replacement.add("%removedBy%", grant.getRemovedBy());
                    replacement.add("%removedAt%", DateUtils.getDate(grant.getRemovedAt()));
                    replacement.add("%addedBy%", grant.getAddedBy());
                    replacement.add("%addedAt%", DateUtils.getDate(grant.getAddedAt()));
                    replacement.add("%duration%", grant.getNiceDuration());
                    replacement.add("%isActive%", (grant.hasExpired() ? "&cNo" : "&2Yes"));
                    replacement.add("%expire%", grant.getNiceExpire());
                    replacement.add("%reason%", grant.getReason());
                    replacement.add("%server%", grant.getServer());

                    item.addLoreLine(replacement.toString());
                });
            }
            return item.toItemStack();
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            RankData rankData = plugin.getRankManagement().getRank(grant.getRankName());
            if (rankData != null && rankData.isDefaultRank()) return;
            if (grant.hasExpired()) return;

            grant.setActive(false);
            grant.setRemovedAt(System.currentTimeMillis());
            grant.setRemovedBy(player.getName());

            Tasks.runAsync(plugin, targetData::saveData);

            Player target = Bukkit.getPlayer(targetData.getPlayerName());
            if (target == null) {
                plugin.getRedisData().write(JedisAction.GRANTS_UPDATE,
                        new JsonChain().addProperty("name", targetData.getPlayerName())
                                .addProperty("grants", GrantSerilization.serilizeGrants(targetData.getGrants())).get());
            }

            update(player);
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
}
