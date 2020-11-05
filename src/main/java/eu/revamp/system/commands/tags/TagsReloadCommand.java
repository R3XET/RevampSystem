package eu.revamp.system.commands.tags;

import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.utilities.file.ConfigFile;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.entity.Player;

public class TagsReloadCommand extends BaseCommand {

    @Command(name = "tagsreload", aliases = "reloadtags", permission = "revampsystem.command.tagsreload")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        plugin.setTags(new ConfigFile(plugin, "tags.yml"));

        Tasks.runAsync(plugin, () -> {
            player.sendMessage(Color.translate("&aRe-importing tags. Please wait."));
            plugin.getTagManagement().deleteTags();
            plugin.getTagManagement().importTags();
            plugin.getTagManagement().saveTags();
            plugin.getTagManagement().requestTagsUpdate();
            player.sendMessage(Color.translate("&aTags have been imported and updated on all servers."));
        });
    }
}
