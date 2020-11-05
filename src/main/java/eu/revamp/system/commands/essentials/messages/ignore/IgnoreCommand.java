package eu.revamp.system.commands.essentials.messages.ignore;

import eu.revamp.spigot.utils.chat.ChatComponentBuilder;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.entity.Player;

public class IgnoreCommand extends BaseCommand {

    @Command(name = "ignore")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (args.length == 0) {
                player.sendMessage(Language.MESSAGES_IGNORE_USAGE.toString());
                return;
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (playerData.getMessageSystem().getIgnoreList().size() > 0) {
                    plugin.getCoreConfig().getStringList("IGNORE_LIST_FORMAT").forEach(message -> {
                        if (!message.contains("%names%")) {
                            player.sendMessage(message);
                        } else {
                            ChatComponentBuilder chatComponentBuilder = new ChatComponentBuilder("");
                            playerData.getMessageSystem().getIgnoreList().forEach(name -> {
                                chatComponentBuilder.append(Color.translate("&a" + name));

                                HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ChatComponentBuilder(ChatColor.RED + "Click to un-ignore.").create());
                                ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/ignore remove " + name);

                                chatComponentBuilder.setCurrentHoverEvent(hoverEvent).setCurrentClickEvent(clickEvent);

                                chatComponentBuilder.append(Color.translate("&7, "));
                            });
                            chatComponentBuilder.getCurrent().setText(chatComponentBuilder.getCurrent().getText().substring(0, chatComponentBuilder.getCurrent().getText().length() - 4));
                            chatComponentBuilder.append(Color.translate("&7."));
                            player.spigot().sendMessage(chatComponentBuilder.create());
                        }
                    });
                } else {
                    player.sendMessage(Language.MESSAGES_IGNORE_LIST_EMPTY.toString());
                }
                return;
            }
            if (args.length < 2) {
                Tasks.run(plugin, () -> player.performCommand(command.getLabel()));;
                return;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (args[1].equalsIgnoreCase(player.getName())) {
                    player.sendMessage(Language.MESSAGES_IGNORE_CANNOT_ADD_YOURSELF.toString());
                    return;
                }
                if (playerData.getMessageSystem().isIgnoring(args[1])) {
                    player.sendMessage(Language.MESSAGES_IGNORE_ALREADY_IGNORING.toString().replace("%player%", args[1]));
                    return;
                }
                playerData.getMessageSystem().getIgnoreList().add(args[1]);
                player.sendMessage(Language.MESSAGES_IGNORE_IGNORED.toString().replace("%player%", args[1]));
                return;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (!playerData.getMessageSystem().isIgnoring(args[1])) {
                    player.sendMessage(Language.MESSAGES_IGNORE_NOT_IGNORING.toString().replace("%player%", args[1]));
                    return;
                }
                playerData.getMessageSystem().getIgnoreList().remove(args[1]);
                player.sendMessage(Language.MESSAGES_IGNORE_UNIGNORED.toString().replace("%player%", args[1]));
                return;
            }
            Tasks.run(plugin, () -> player.performCommand(command.getLabel()));;
        });
    }
}
