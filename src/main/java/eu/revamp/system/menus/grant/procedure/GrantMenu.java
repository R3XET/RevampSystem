package eu.revamp.system.menus.grant.procedure;

import eu.revamp.spigot.utils.block.wool.WoolUtils;
import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.api.rank.RankData;
import eu.revamp.system.data.grant.GrantProcedure;
import eu.revamp.system.data.grant.GrantProcedureState;
import eu.revamp.system.enums.Language;
import eu.revamp.system.menu.menu.SwitchableMenu;
import eu.revamp.system.menu.slots.Slot;
import eu.revamp.system.menus.slots.PlayerInfoSlot;
import eu.revamp.spigot.utils.generic.Tasks;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@RequiredArgsConstructor
public class GrantMenu extends SwitchableMenu {
    private final PlayerData target;

    @Override
    public void onClose(Player player) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        if (playerData.getGrantProcedure() != null && playerData.getGrantProcedure().getGrantProcedureState() == GrantProcedureState.START) {
            playerData.setGrantProcedure(null);
        }
        plugin.getPlayerManagement().deleteData(target.getUniqueId());
    }

    @Override
    public void onOpen(Player player) {
        this.setUpdateInTask(true);
    }

    @Override
    public String getPagesTitle(Player player) {
        return "&7Ranks";
    }

    @Override
    public List<Slot> getSwitchableSlots(Player player) {
        List<Slot> slots = new ArrayList<>();

        plugin.getRankManagement().getRanks().stream().sorted(Comparator.comparingInt(RankData::getWeight).reversed()).forEach(rankData -> {
            slots.add(new RankButton(rankData, target));
        });

        return slots;
    }

    @Override
    public List<Slot> getEveryMenuSlots(Player player) {
        List<Slot> slots = new ArrayList<>();
        slots.add(new PlayerInfoSlot(target, 4));
        slots.add(new Slot() {

            @Override
            public ItemStack getItem(Player player) {
                ItemBuilder item = new ItemBuilder(Material.PAPER);
                item.setName("&aGrants");
                item.addLoreLine(" ");
                item.addLoreLine("&7Click to show");
                item.addLoreLine("&b" + target.getPlayerName() + "'s &7grants!");
                item.addLoreLine(" ");
                return item.toItemStack();
            }

            @Override
            public int getSlot() {
                return 40;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType) {
                Tasks.run(plugin, () -> player.performCommand("grants " + target.getPlayerName()));
            }
        });
        return slots;
    }

    @AllArgsConstructor
    private class RankButton extends Slot {
        private RankData rankData;
        private PlayerData playerData;

        @Override
        public ItemStack getItem(Player player) {
            ItemBuilder item = new ItemBuilder(Material.WOOL);
            item.setName(rankData.getDisplayName());
            item.setDurability((short) (rankData.isDefaultRank() ? 4 : WoolUtils.convertChatColorToWoolData(rankData.getColor())));
            plugin.getCoreConfig().getStringList("grant-rank-lore").forEach(message -> {
                Replacement replacement = new Replacement(message).add("%rank%", rankData.getDisplayName())
                        .add("%player%", playerData.getHighestRank().getColor() + playerData.getPlayerName());
                item.addLoreLine(replacement.toString());
            });
            return item.toItemStack();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType) {
            if (rankData.isDefaultRank()) {
                player.sendMessage(Language.GRANT_PROCEDURE_CANT_GRANT_DEFAULT.toString());
                return;
            }
            if (playerData.hasRank(rankData)) {
                player.sendMessage(Language.GRANT_PROCEDURE_ALREADY_HAVE_RANK.toString().replace("%player%", playerData.getPlayerName()));
                return;
            }
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (!plugin.getRankManagement().canGrant(playerData, rankData) && !player.hasPermission("revampsystem.grant.all")) {
                player.sendMessage(Language.GRANT_PROCEDURE_CANT_GRANT.toString());
                return;
            }
            if (!player.isOp() && player.hasPermission("revampsystem.grant.disallow." + rankData.getName().toLowerCase())) {
                player.sendMessage(Language.GRANT_PROCEDURE_CANT_GRANT_DISALLOWED.toString());
                return;
            }
            playerData.setGrantProcedure(new GrantProcedure(this.playerData));
            playerData.getGrantProcedure().setRankName(rankData.getName());
            playerData.getGrantProcedure().setGrantProcedureState(GrantProcedureState.SERVER_CHOOSE);

            new ChooseGrantServerMenu().open(player);
        }
    }
}
