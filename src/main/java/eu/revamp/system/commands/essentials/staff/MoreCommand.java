package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.Language;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoreCommand extends BaseCommand {

    @Command(name = "more", permission = "revampsystem.command.more")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            ItemStack item = player.getItemInHand();

            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(Language.MORE_ITEM_NULL.toString());
                return;
            }

            item.setAmount(item.getMaxStackSize());
            player.updateInventory();

            PlayerUtils.playSound(player, Sound.LEVEL_UP);
            player.sendMessage(Language.MORE_SUCCESS.toString());
        });
    }
}
