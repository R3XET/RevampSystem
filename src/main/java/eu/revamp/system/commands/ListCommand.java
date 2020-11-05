package eu.revamp.system.commands;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import eu.revamp.spigot.utils.chat.ChatComponentBuilder;
import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.GlobalPlayer;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.api.rank.RankData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends BaseCommand {

    private Comparator<RankData> RANK_COMPARATOR = Comparator.comparingInt(RankData::getWeight).reversed();
    private Comparator<PlayerData> PLAYER_DATA_COMPARATOR = Comparator.comparingInt(playerData -> playerData.getHighestRank().getWeight());
    private Comparator<GlobalPlayer> GLOBAL_PLAYER_COMPARATOR = Comparator.comparingInt(GlobalPlayer::getRankWeight);

    @Command(name = "list", permission = "revampsystem.command.list")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                List<RankData> ranks = plugin.getRankManagement().getRanks().stream().sorted(RANK_COMPARATOR).collect(Collectors.toList());

                StringBuilder rankBuilder = new StringBuilder();
                ranks.forEach(rankData -> {
                    rankBuilder.append(rankData.getDisplayName());
                    rankBuilder.append("&7, ");
                });
                if (rankBuilder.length() > 0) {
                    rankBuilder.setLength(rankBuilder.length() - 2);
                    rankBuilder.append("&7.");
                }

                plugin.getCoreConfig().getStringList("LIST_COMMAND.FORMAT").forEach(message -> {
                    if (message.contains("%ranks%")) {
                        player.sendMessage(Color.translate(rankBuilder.toString()));
                    } else if (message.contains("%players%")) {
                        ChatComponentBuilder chatComponentBuilder = new ChatComponentBuilder("");
                        PlayerUtils.getOnlinePlayers().stream().map(online -> plugin.getPlayerManagement().getPlayerData(online.getUniqueId())).filter(playerData -> !playerData.isVanished()).sorted(PLAYER_DATA_COMPARATOR.reversed()).limit(100).forEach(playerData -> {
                            if (plugin.getEssentialsManagement().useNameColorList()) {
                                chatComponentBuilder.append(playerData.getNameWithColor());
                            } else {
                                chatComponentBuilder.append(playerData.getHighestRank().getColor() + playerData.getPlayerName());
                            }

                            if (plugin.getCoreConfig().getBoolean("LIST_COMMAND.PLAYERS_FORMAT.HOVER_ENABLED")) {
                                String text = eu.revamp.spigot.utils.string.StringUtils.getStringFromList(plugin.getCoreConfig().getStringList("LIST_COMMAND.PLAYERS_FORMAT.HOVER_TEXT")).replace(", ", "\n");
                                Replacement replacement = new Replacement(text).add("%rank%", playerData.getHighestRank().getDisplayName());

                                chatComponentBuilder.setCurrentHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ChatComponentBuilder(replacement.toString()).create()));
                            }
                            chatComponentBuilder.append(Color.translate("&7, "));
                        });
                        if (chatComponentBuilder.getCurrent().getText().length() > 0) {
                            chatComponentBuilder.getCurrent().setText(chatComponentBuilder.getCurrent().getText().substring(0, chatComponentBuilder.getCurrent().getText().length() - 4));
                            chatComponentBuilder.append(Color.translate("&7."));
                            player.spigot().sendMessage(chatComponentBuilder.create());
                        }
                    } else {
                        player.sendMessage(this.replacePlaceholders(player, message.replace("%online%", String.valueOf(PlayerUtils.getOnlinePlayers().size()))));
                    }
                });
                return;
            }
            if (args[0].equalsIgnoreCase("all")) {
                StringBuilder nameBuilder = new StringBuilder();
                plugin.getServerManagement().getGlobalPlayers().stream().filter(globalPlayer -> !globalPlayer.isVanished()).sorted(GLOBAL_PLAYER_COMPARATOR.reversed()).limit(100).forEach(playerData -> {
                    RankData rankData = plugin.getRankManagement().getRank(playerData.getRankName());
                    if (rankData != null) {
                        nameBuilder.append(rankData.getColor()).append(playerData.getName());
                    } else {
                        nameBuilder.append(ChatColor.WHITE).append(playerData.getName());
                    }
                    nameBuilder.append("&7, ");
                });
                if (nameBuilder.length() > 0) {
                    nameBuilder.setLength(nameBuilder.length() - 2);
                    nameBuilder.append("&7.");
                }

                Replacement replacement = new Replacement(Language.LIST_ALL_FORMAT.toString());
                replacement.add("%max%", plugin.getServerManagement().getGlobalMaxPlayers());
                replacement.add("%players%", plugin.getServerManagement().getGlobalPlayers().size());
                replacement.add("%list%", nameBuilder.toString());
                replacement.add("%online", PlayerUtils.getOnlinePlayers().size());

                player.sendMessage(this.replacePlaceholders(player, replacement.toString()));
                return;
            }
            Tasks.run(plugin, () -> player.performCommand(command.getLabel()));
        });
    }

    private String replacePlaceholders(Player player, String source) {
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null
                && plugin.getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player,
                    PlaceholderAPI.replacePlaceholders(player, source));
        }
        if (plugin.getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            return PlaceholderAPI.replacePlaceholders(player, source);
        }
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, source);
        }
        return source;
    }
}
