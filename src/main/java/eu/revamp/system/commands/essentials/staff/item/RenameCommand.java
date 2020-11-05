package eu.revamp.system.commands.essentials.staff.item;

import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand extends BaseCommand {

    @Command(name = "rename", permission = "revampsystem.command.rename", aliases = "renameitem")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.RENAME_USAGE.toString());
                return;
            }
            ItemStack item = player.getItemInHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(Language.RENAME_ITEM_NULL.toString());
                return;
            }
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(Color.translate(CCUtils.buildMessage(args, 0)));
            item.setItemMeta(itemMeta);

            player.updateInventory();
            player.sendMessage(Language.RENAME_SUCCESS.toString()
                    .replace("%name%", Color.translate(CCUtils.buildMessage(args, 0))));
        });
    }
}
