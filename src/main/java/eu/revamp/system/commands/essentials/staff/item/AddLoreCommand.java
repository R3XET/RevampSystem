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

import java.util.ArrayList;
import java.util.List;

public class AddLoreCommand extends BaseCommand {

    @Command(name = "addlore", permission = "revampsystem.command.addlore", aliases = "additemlore")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.LORE_ADD_USAGE.toString());
                return;
            }
            ItemStack item = player.getItemInHand();
            if (item == null || item.getType() == Material.AIR) {
                player.sendMessage(Language.LORE_ITEM_NULL.toString());
                return;
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (!itemMeta.hasLore()) {
                itemMeta.setLore(new ArrayList<>());
            }

            String add = Color.translate(CCUtils.buildMessage(args, 0));

            List<String> lore = itemMeta.getLore();
            lore.add(add);
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);

            player.updateInventory();
            player.sendMessage(Language.LORE_SUCCESS.toString()
                    .replace("%lore%", Color.translate(CCUtils.buildMessage(args, 0))));
        });
    }
}
