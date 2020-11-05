package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.item.ItemBuilder;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SkullCommand extends BaseCommand {

    @Command(name = "skull", permission = "revampsystem.command.skull", aliases = {"head", "getskull", "gethead"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.SKULL_USAGE.toString());
                return;
            }
            if (player.getInventory().firstEmpty() == -1) {
                player.sendMessage(Language.SKULL_INV_FULL.toString());
                return;
            }
            player.getInventory().addItem(new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setSkullOwner(args[0]).toItemStack());

            player.sendMessage(Language.SKULL_GIVEN.toString()
                    .replace("%name%", args[0]));
        });
    }
}
