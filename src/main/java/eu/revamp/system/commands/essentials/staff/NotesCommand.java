package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.generic.ConversionUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.concurrent.atomic.AtomicInteger;

public class NotesCommand extends BaseCommand {

    @Command(name = "notes", aliases = {"note"}, permission = "revampsystem.command.notes", inGameOnly = false) @SuppressWarnings("deprecation")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                sender.sendMessage(Language.NOTE_USAGE.toString());
                return;
            }
            if (args.length == 1) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                if (playerData == null) {
                    sender.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                    playerData = plugin.getPlayerManagement().loadData(player.getUniqueId());

                    if (playerData == null) {
                        sender.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                        return;
                    }
                    playerData.loadData();
                }
                if (playerData.getNotes().size() == 0) {
                    sender.sendMessage(Language.NOTE_DONT_HAVE.toString());
                    return;
                }

                PlayerData finalPlayerData = playerData;
                plugin.getCoreConfig().getStringList("notes-format").forEach(message -> {
                    if (!message.toLowerCase().contains("%notes%")) {
                        sender.sendMessage(message
                                .replace("%player%", player.getName()));
                    } else {
                        AtomicInteger id = new AtomicInteger(1);
                        finalPlayerData.getNotes().forEach(note -> sender.sendMessage(plugin.getCoreConfig().getString("note-format")
                                .replace("%note%", ChatColor.stripColor(note))
                                .replace("%id%", String.valueOf(id.getAndIncrement()))));
                    }
                });
                return;
            }
            if (args.length >= 3) {
                if (args[0].equalsIgnoreCase("add")) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[1]));
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                    if (playerData == null) {
                        sender.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                        playerData = plugin.getPlayerManagement().loadData(player.getUniqueId());

                        if (playerData == null) {
                            sender.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                            return;
                        }
                        playerData.loadData();
                    }
                    playerData.getNotes().add(CCUtils.buildMessage(args, 2));

                    sender.sendMessage(Language.NOTE_ADDED.toString()
                            .replace("%id%", String.valueOf(playerData.getNotes().size()))
                            .replace("%player%", player.getName()));

                    playerData.saveData();
                    return;
                }
                if (args.length == 3) {
                    if (args[0].equalsIgnoreCase("remove")) {
                        if (!ConversionUtils.isInteger(args[2])) {
                            sender.sendMessage(Language.USE_NUMBERS.toString());
                            return;
                        }
                        OfflinePlayer player = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[1]));
                        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
                        int id = Integer.parseInt(args[2]);

                        if (id <= 0) {
                            sender.sendMessage(Color.translate("&cId must be a positive number."));
                            return;
                        }
                        if (playerData == null) {
                            sender.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                            playerData = plugin.getPlayerManagement().loadData(player.getUniqueId());

                            if (playerData == null) {
                                sender.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                                return;
                            }
                            playerData.loadData();
                        }
                        if (id > playerData.getNotes().size()) {
                            sender.sendMessage(Language.NOTE_INVALID_ID.toString()
                                    .replace("%player%", player.getName()));
                            return;
                        }
                        playerData.getNotes().remove(id - 1);

                        sender.sendMessage(Language.NOTE_REMOVED.toString()
                                .replace("%id%", String.valueOf(id))
                                .replace("%player%", player.getName()));

                        playerData.saveData();
                        return;
                    }
                }
                sender.sendMessage(Language.NOTE_USAGE.toString());
            }
        });
    }


}
