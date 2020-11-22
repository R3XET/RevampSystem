package eu.revamp.system.commands.essentials.staff.item;

import eu.revamp.spigot.utils.enchant.EnchantUtils;
import eu.revamp.spigot.utils.generic.ConversionUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCommand extends BaseCommand {

    @Command(name = "enchant", permission = "revampsystem.command.enchant", aliases = "enchantitem")
    public void onCommand(CommandArgs args) {
        Tasks.runAsync(plugin, () -> {
            Player player = (Player)args.getSender();
            if (args.getArgs().length < 2) {
                player.sendMessage(Language.ENCHANT_COMMAND_USAGE.toString());
            }
            else {
                ItemStack item = player.getItemInHand();
                if (item == null || item.getType() == Material.AIR) {
                    player.sendMessage(Language.ENCHANT_CAN_NOT_ENCHANT_ITEM.toString());
                    return;
                }
                if (!ConversionUtils.isInteger(args.getArgs(1))) {
                    player.sendMessage(Language.MUST_BE_INTEGER.toString());
                    return;
                }
                int level = Integer.parseInt(args.getArgs(1));
                if (level < 0) {
                    player.sendMessage(Language.ENCHANT_LEVEL_MUST_BE_POSITIVE.toString());
                    return;
                }
                if (level > 7 && !player.isOp()) {
                    player.sendMessage(Language.ENCHANT_LIMIT.toString().replace("%level%", String.valueOf(7)));
                    return;
                }
                String enchantment = EnchantUtils.getEnchantment(args.getArgs(0));
                if (Enchantment.getByName(enchantment) == null){
                    player.sendMessage(Language.ENCHANT_UNKNOWN.toString().replace("%enchant%", args.getArgs(0)));
                }
                if (level == 0) {
                    if (item.containsEnchantment(Enchantment.getByName(enchantment))) {
                        item.removeEnchantment(Enchantment.getByName(enchantment));
                        player.sendMessage(Language.ENCHANT_REMOVED.toString().replace("%enchant%", args.getArgs(0)));
                    }
                    else {
                        player.sendMessage(Language.ENCHANT_NO_ENCHANT.toString().replace("%enchant%", args.getArgs(0)));
                    }
                }
                else {
                    item.addUnsafeEnchantment(Enchantment.getByName(enchantment), level);
                    player.sendMessage(Language.ENCHANT_ENCHANTED.toString().replace("%item%", String.valueOf(item.getType())));
                }
            }
        });
    }
}

