package eu.revamp.system.commands.tags;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.menus.tags.TagsMainMenu;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class TagsCommand extends BaseCommand {

    @Command(name = "tag", aliases = "tags")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (!player.hasPermission("revampsystem.command.tags")) {
            player.sendMessage(Language.TAGS_NO_ACCESS.toString());
            return;
        }
        new TagsMainMenu().open(command.getPlayer());
    }
}
