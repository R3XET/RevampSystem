package eu.revamp.system.commands.essentials.gamemode;

import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.enums.Language;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GamemodeCommand extends BaseCommand {

    @Command(name = "gamemode", permission = "revampsystem.command.gamemode", aliases = {"gm"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Language.GAMEMODE_USAGE.toString());
            return;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("survival") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("0")) {
                if (player.getGameMode() == GameMode.SURVIVAL) {
                    player.sendMessage(Language.ALREADY_HAVE_GAMEMODE.toString()
                            .replace("%gamemode%", "Survival"));
                    return;
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(Language.GAMEMODE_UPDATED.toString()
                        .replace("%gamemode%", "Survival"));
                return;
            }
            if (args[0].equalsIgnoreCase("creative") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("1")) {
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.sendMessage(Language.ALREADY_HAVE_GAMEMODE.toString()
                            .replace("%gamemode%", "Creative"));
                    return;
                }
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(Language.GAMEMODE_UPDATED.toString()
                        .replace("%gamemode%", "Creative"));
                return;
            }
            if (args[0].equalsIgnoreCase("adventure") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("2")) {
                if (player.getGameMode() == GameMode.ADVENTURE) {
                    player.sendMessage(Language.ALREADY_HAVE_GAMEMODE.toString()
                            .replace("%gamemode%", "Adventure"));
                    return;
                }
                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage(Language.GAMEMODE_UPDATED.toString()
                        .replace("%gamemode%", "Adventure"));
                return;
            }
        }
        if (!player.hasPermission("revampsystem.command.gamemode.other")) {
            player.sendMessage(Language.WRONG_GAMEMODE.toString());
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }
        if (args.length == 1) {
            player.sendMessage(Language.GAMEMODE_USAGE.toString());
            return;
        }
        if (args[1].equalsIgnoreCase("survival") || args[1].equalsIgnoreCase("s") || args[1].equalsIgnoreCase("0")) {
            if (target.getGameMode() == GameMode.SURVIVAL) {
                player.sendMessage(Language.ALREADY_HAVE_GAMEMODE_OTHER.toString()
                        .replace("%player%", target.getName())
                        .replace("%gamemode%", "Survival"));
                return;
            }
            target.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Language.GAMEMODE_UPDATED_OTHER.toString()
                    .replace("%player%", target.getName())
                    .replace("%gamemode%", "Survival"));
            target.sendMessage(Language.GAMEMODE_UPDATED_OTHER_TARGET.toString()
                    .replace("%sender%", player.getName())
                    .replace("%gamemode%", "Survival"));
            return;
        }
        if (args[1].equalsIgnoreCase("creative") || args[1].equalsIgnoreCase("c") || args[1].equalsIgnoreCase("1")) {
            if (target.getGameMode() == GameMode.CREATIVE) {
                player.sendMessage(Language.ALREADY_HAVE_GAMEMODE_OTHER.toString()
                        .replace("%player%", target.getName())
                        .replace("%gamemode%", "Creative"));
                return;
            }
            target.setGameMode(GameMode.CREATIVE);
            player.sendMessage(Language.GAMEMODE_UPDATED_OTHER.toString()
                    .replace("%player%", target.getName())
                    .replace("%gamemode%", "Creative"));
            target.sendMessage(Language.GAMEMODE_UPDATED_OTHER_TARGET.toString()
                    .replace("%sender%", player.getName())
                    .replace("%gamemode%", "Creative"));
            return;
        }
        if (args[1].equalsIgnoreCase("adventure") || args[1].equalsIgnoreCase("a") || args[1].equalsIgnoreCase("2")) {
            if (target.getGameMode() == GameMode.ADVENTURE) {
                player.sendMessage(Language.ALREADY_HAVE_GAMEMODE_OTHER.toString()
                        .replace("%player%", target.getName())
                        .replace("%gamemode%", "Adventure"));
                return;
            }
            target.setGameMode(GameMode.ADVENTURE);
            player.sendMessage(Language.GAMEMODE_UPDATED_OTHER.toString()
                    .replace("%player%", target.getName())
                    .replace("%gamemode%", "Adventure"));
            target.sendMessage(Language.GAMEMODE_UPDATED_OTHER_TARGET.toString()
                    .replace("%sender%", player.getName())
                    .replace("%gamemode%", "Adventure"));
            return;
        }
        player.sendMessage(Language.WRONG_GAMEMODE.toString());
    }
}
