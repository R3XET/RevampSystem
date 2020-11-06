package eu.revamp.system.listeners;

import eu.revamp.spigot.utils.generic.NameMC;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.spigot.utils.reflection.SitUtils;
import eu.revamp.system.api.player.GlobalPlayer;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.api.rank.RankData;
import eu.revamp.system.api.rank.grant.Grant;
import eu.revamp.system.data.other.GlobalCooldowns;
import eu.revamp.system.database.redis.other.bson.JsonChain;
import eu.revamp.system.database.redis.payload.action.JedisAction;
import eu.revamp.system.enums.Language;
import eu.revamp.system.events.PlayerOpChangeEvent;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.punishments.player.PunishPlayerData;
import eu.revamp.system.punishments.utilities.punishments.Alt;
import eu.revamp.system.utilities.chat.Color;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class PlayerListener implements Listener {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleAsyncLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.getEssentialsManagement().isServerJoinable()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Language.SERVER_LOADING_KICK.toString());
            return;
        }
        if (plugin.getImportManagement().isLoadingUsers()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, Color.translate("&cCurrently importing PEX, please wait."));
            return;
        }

        UUID uuid = event.getUniqueId();
        String name = event.getName();

        plugin.getPlayerManagement().createPlayerData(uuid, name);
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(uuid);
        playerData.setGlobalCooldowns(new GlobalCooldowns(uuid, name));

        playerData.loadData();
        playerData.getGlobalCooldowns().loadCooldowns();
        playerData.loadPunishmentsPerformed();

        if (!playerData.getAddress().equalsIgnoreCase(event.getAddress().getHostAddress()) || playerData.isStaffAuth()) {
            playerData.setStaffAuth(true);
        } else {
            playerData.setStaffAuth(false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData == null) return;

        if (plugin.getCoreConfig().getBoolean("using-login-events")) {
            playerData.loadAttachments(player);
        } else {
            Tasks.runLater(plugin, () -> playerData.loadAttachments(player), 2L);
        }
        playerData.setAddress(event.getAddress().getHostAddress());

        playerData.saveData("lastServer", plugin.getEssentialsManagement().getServerName());
    }

    /**
     * @param event Done because of Bungee permissions
     */


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        Tasks.runAsyncLater(plugin, () -> {
            if (playerData != null && Bukkit.getPlayer(player.getUniqueId()) != null) {
                playerData.loadAttachments(Bukkit.getPlayer(player.getUniqueId())); //This is because of bungee permissions!
            }
        }, 30L * 2);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData == null) {
            player.kickPlayer(Color.translate("&cYour data failed to load, please try joining again."));
            return;
        }

        // Added playtime
        playerData.setPlayerJoinTime(System.currentTimeMillis());

        if (!plugin.getCoreConfig().getBoolean("on-join.show-default-message")) {
            event.setJoinMessage(null);
        }

        boolean joinedVanished = false;
        if (plugin.getCoreConfig().getBoolean("vanish-on-join.enabled")) {
            if (player.hasPermission(plugin.getCoreConfig().getString("vanish-on-join.permission"))) {
                if (player.hasPermission("revampsystem.command.vanish")) {
                    plugin.getVanishManagement().vanishPlayer(player);
                    joinedVanished = true;
                }
            }
        }

        boolean finalJoinedVanished = joinedVanished;
        Tasks.runAsync(plugin, () -> {
            if (plugin.getCoreConfig().getBoolean("on-join.clear-chat")) {
                player.sendMessage(Color.BLANK_MESSAGE);
            }
            if (plugin.getCoreConfig().getBoolean("join-message.enabled")) {
                plugin.getCoreConfig().getStringList("join-message.message").forEach(message -> {
                    player.sendMessage(message
                            .replace("%server%", plugin.getEssentialsManagement().getServerName())
                            .replace("%rank%", playerData.getHighestRank().getDisplayName())
                            .replace("%name%", playerData.getNameWithColor()));
                });
            }
            if (plugin.getCoreConfig().getBoolean("on-join.play-sound.enabled")) {
                PlayerUtils.playSound(player, plugin.getCoreConfig().getString("on-join.play-sound.sound"));
            }

            switch (playerData.getWorldTime()) {
                case "DAY":
                    player.setPlayerTime(0L, false);
                    break;
                case "NIGHT":
                    player.setPlayerTime(20000L, false);
                    break;
                case "SUNSET":
                    player.setPlayerTime(12500, false);
                    break;
                default:
                    player.resetPlayerTime();
                    break;
            }

            if (plugin.getImportManagement().getImportingUsersPlayer().equalsIgnoreCase(player.getName())) {
                plugin.getImportManagement().setImportingUsersPlayer("");
                player.sendMessage(" ");
                player.sendMessage(Color.translate(Language.PREFIX.toString() + "&aYou have successfully imported users and ranks from &bPEX&a!"));
                player.sendMessage(" ");
            }
            //System.out.println(plugin.getEssentialsManagement().getServerName());

            playerData.setLastServer(plugin.getEssentialsManagement().getServerName());

            //System.out.println("AAAA");

            if (!NameMC.isNameMCVerified(player.getUniqueId())) {
                if (plugin.getCoreConfig().getBoolean("name-mc.on-join.send-message")) {
                    plugin.getCoreConfig().getStringList("name-mc.on-join.message").forEach(player::sendMessage);
                }
            } else {
                if (plugin.getCoreConfig().getBoolean("name-mc.on-join.give-rank.enabled")) {
                    RankData rankData = plugin.getRankManagement().getRank(plugin.getCoreConfig().getString("name-mc.on-join.give-rank.rank-name"));
                    if (rankData != null) {
                        if (!playerData.hasRank(rankData)) {
                            Grant grant = new Grant();
                            grant.setPermanent(true);
                            grant.setRankName(rankData.getName());
                            grant.setActive(true);
                            grant.setAddedBy("Console");
                            grant.setReason("Liked server on NameMC");
                            playerData.getGrants().add(grant);

                            playerData.saveData();
                            playerData.loadAttachments(player);
                        }
                    }
                }
            }

            if (finalJoinedVanished) {
                player.sendMessage(Language.JOINED_VANISHED.toString()
                        .replace("%priority%", String.valueOf(plugin.getVanishManagement().getVanishPriority(player) * 2)));
            }

            if (player.hasPermission("revampsystem.staff.join.messages")) {
                GlobalPlayer globalPlayer = plugin.getServerManagement().getGlobalPlayer(player.getName());
                RankData rankData = playerData.getHighestRank();

                if (globalPlayer != null && globalPlayer.getLastServer() != null && !globalPlayer.getLastServer()
                        .equalsIgnoreCase(plugin.getEssentialsManagement().getServerName())) {
                    plugin.getRedisData().write(JedisAction.STAFF_SWITCH,
                            new JsonChain().addProperty("name", rankData.formatName(player))
                                    .addProperty("server", plugin.getEssentialsManagement().getServerName())
                                    .addProperty("from", globalPlayer.getLastServer()).get());
                } else {
                    plugin.getRedisData().write(JedisAction.STAFF_CONNECT,
                            new JsonChain().addProperty("name", rankData.getColor() + player.getName())
                                    .addProperty("server", plugin.getEssentialsManagement().getServerName()).get());
                }
            }
            if (playerData.getNotes().size() > 0) {
                PlayerUtils.getOnlinePlayers().stream().filter(online -> online.hasPermission("notes.see.on.join")).forEach(online -> {
                    plugin.getCoreConfig().getStringList("notes-format").forEach(message -> {
                        if (!message.toLowerCase().contains("%notes%")) {
                            online.sendMessage(message
                                    .replace("%player%", player.getName()));
                        } else {
                            AtomicInteger id = new AtomicInteger(1);
                            playerData.getNotes().forEach(note -> online.sendMessage(plugin.getCoreConfig().getString("note-format")
                                    .replace("%note%", ChatColor.stripColor(note))
                                    .replace("%id%", String.valueOf(id.getAndIncrement()))));
                        }
                    });
                });
            }
            if (player.isOp() && !plugin.getOps().contains(player.getUniqueId())) {
                plugin.getOps().add(player.getUniqueId());
            }
            playerData.setFullJoined(true);
        });
        if (plugin.getCoreConfig().getBoolean("on-join.teleport.enabled")) {
            double x = plugin.getCoreConfig().getDouble("on-join.teleport.location.x");
            double y = plugin.getCoreConfig().getDouble("on-join.teleport.location.y");
            double z = plugin.getCoreConfig().getDouble("on-join.teleport.location.z");
            float yaw = plugin.getCoreConfig().getInt("on-join.teleport.location.yaw");
            float pitch = plugin.getCoreConfig().getInt("on-join.teleport.location.pitch");
            World world = Bukkit.getWorld(plugin.getCoreConfig().getString("on-join.teleport.location.world"));

            player.teleport(new Location(world, x, y, z, yaw, pitch));
        }

        PlayerUtils.getOnlinePlayers().forEach(online -> {
            PlayerData onlineData = plugin.getPlayerManagement().getPlayerData(online.getUniqueId());

            if (onlineData.isVanished()) {
                plugin.getVanishManagement().vanishPlayerFor(online, player);
            }
        });

        // OLD BUGS INVENTORY LOST
        /*
        Tasks.runLater(this.plugin, () -> {
            if (plugin.getCoreConfig().getBoolean("staff-mode-on-join.enabled")) {
                if (player.hasPermission(plugin.getCoreConfig().getString("staff-mode-on-join.permission"))) {
                    if (player.hasPermission("revampsystem.command.staffmode")) {
                        plugin.getStaffModeManagement().enableStaffMode(player);
                    }
                }
            }
        }, 10L);

*/
        Tasks.runAsync(this.plugin, () -> {
            if (plugin.getCoreConfig().getBoolean("staff-mode-on-join.enabled")) {
                if (player.hasPermission(plugin.getCoreConfig().getString("staff-mode-on-join.permission"))) {
                    if (player.hasPermission("revampsystem.command.staffmode")) {
                        plugin.getStaffModeManagement().enableStaffMode(player);
                    }
                }
            }
        });

        Tasks.runAsync(plugin, () -> {
            PunishPlayerData punishPlayerData = this.plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(player.getUniqueId());

            StringBuilder altBuilder = new StringBuilder();
            int i = 0;
            for (Alt alt : punishPlayerData.getAlts()) {
                if (alt.isBanned()) i++;

                altBuilder.append(alt.getNameColor()).append(alt.getName());
                altBuilder.append(ChatColor.GRAY).append(", ");
            }
            if (i > 0) {
                altBuilder.setLength(altBuilder.length() - 2);
                altBuilder.append(".");
                plugin.getServerManagement().getGlobalPlayers().forEach(globalPlayer -> {
                    if (globalPlayer.hasPermission("revampsystem.alert.evade")) {
                        globalPlayer.sendMessage(Language.BAN_EVADING.toString()
                                .replace("%server%", plugin.getEssentialsManagement().getServerName())
                                .replace("%alts%", altBuilder.toString())
                                .replace("%player%", player.getName()));
                    }
                });
            }
        });

        playerData.checkForDefaultRankInGrants();

        if (plugin.getCoreConfig().getBoolean("staff-auth.enabled", true)) {
            if (player.hasPermission(plugin.getCoreConfig().getString("staff-auth.permission", "revampsystem.staff.auth"))) {
                if (playerData.isStaffAuth()) {
                    if (playerData.isInStaffMode()) {
                        plugin.getStaffModeManagement().disableStaffMode(player);
                    }
                    if (playerData.isVanished()) {
                        plugin.getVanishManagement().unvanishPlayer(player);
                    }
                    SitUtils.sitPlayer(player);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 4));
                }
            } else {
                player.removePotionEffect(PotionEffectType.BLINDNESS);
            }
        } else {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
    }

    @EventHandler
    public void handlePlayerKick(PlayerKickEvent event) {
        if (!plugin.getCoreConfig().getBoolean("on-quit.show-default-message")) {
            event.setLeaveMessage(null);
        }
        Player player = event.getPlayer();

        SitUtils.datas.remove(player.getName());

        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData == null) return;

        //if (!plugin.isRevampHCFEnabled() && plugin.getServer().getPluginManager().getPlugin("RevampUHC") == null && plugin.getServer().getPluginManager().getPlugin("RevampUHCGames") == null && plugin.getServer().getPluginManager().getPlugin("RevampHub") == null && plugin.getServer().getPluginManager().getPlugin("RevampPractice") == null) {
            Tasks.runAsync(plugin, () -> plugin.getNameTagManagement().unregister(player));
        //}

        if (playerData.isInStaffMode()) {
            plugin.getStaffModeManagement().disableStaffMode(player);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void handlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        SitUtils.datas.remove(player.getName());

        //plugin.getMenuManager().getOpenedMenus().remove(player.getUniqueId());

        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData == null) return;

        //if (!plugin.isRevampHCFEnabled() && plugin.getServer().getPluginManager().getPlugin("RevampUHC") == null && plugin.getServer().getPluginManager().getPlugin("RevampUHCGames") == null && plugin.getServer().getPluginManager().getPlugin("RevampHub") == null && plugin.getServer().getPluginManager().getPlugin("RevampPractice") == null) {
            Tasks.runAsync(plugin, () -> plugin.getNameTagManagement().unregister(player));
        //}

        if (playerData.isInStaffMode()) {
            plugin.getStaffModeManagement().disableStaffMode(player);
        }
        if (!plugin.getCoreConfig().getBoolean("on-quit.show-default-message")) {
            event.setQuitMessage(null);
        }

        playerData.setLastSeen(new Date().getTime());

        // Added playtime
        long sessionTime = System.currentTimeMillis() - playerData.getPlayerJoinTime();
        long playTime = playerData.getPlayerTime();
        playerData.setPlayerTime(sessionTime + playTime);
        // First set variable and then save to MongoDB
        playerData.saveData("playerTime", playerData.getPlayerTime());

        if (playerData.getPanicSystem().isInPanic()) {
            if (plugin.getCoreConfig().getBoolean("panic.remove-panic-on-quit")) {
                playerData.getPanicSystem().unPanicPlayer();
            }
        }
        if (player.hasPermission("revampsystem.staff.join.messages")) {
            Tasks.runLater(plugin, () -> {
                if (!playerData.isCurrentOnline(player.getName())) {
                    plugin.getRedisData().write(JedisAction.STAFF_DISCONNECT,
                            new JsonChain().addProperty("name", playerData.getHighestRank().getColor() + player.getName())
                                    .addProperty("server", plugin.getEssentialsManagement().getServerName()).get());

                    playerData.saveData("lastServer", null);
                }
            }, 60L);
        }
        Tasks.runAsync(plugin, () -> {
            playerData.saveData();
            playerData.getOfflineInventory().save(player);
            plugin.getPlayerManagement().deleteData(player.getUniqueId());
        });
    }

    @EventHandler
    public void handleSkullClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        if (block == null) return;
        if (block.getType() != Material.SKULL) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Skull skull = (Skull) block.getState();

        if (!skull.hasOwner() || skull.getOwner() == null) return;

        player.sendMessage(Language.SKULL_CLICK.toString()
                .replace("%name%", !skull.hasOwner() ? "Unknown" : skull.getOwner()));
    }

    /*
    @EventHandler
    public void handleCommandProcess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/tps")) {
            event.setCancelled(true);
            event.getPlayer().performCommand("lag");
        }
    }*/

    @EventHandler
    public void handleOpChange(PlayerOpChangeEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        if (playerData != null) {
            Tasks.runAsync(plugin, () -> playerData.loadAttachments(player));
        }
    }

    @EventHandler
    public void handleSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("revampsystem.sign.colors")) return;

        String[] lines = event.getLines();

        IntStream.range(0, lines.length).forEach(i -> event.setLine(i, Color.translate(lines[i])));
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(event.getPlayer().getUniqueId());

            if (playerData == null) return;

            playerData.setBackLocation(event.getPlayer().getLocation());
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(event.getEntity().getUniqueId());

        if (playerData == null) return;

        playerData.setBackLocation(event.getEntity().getLocation());
    }

    private Object getRegisterInstance() throws ReflectiveOperationException {
        Field initField = RevampSystem.INSTANCE.getClass().getDeclaredField("registerManager");
        initField.setAccessible(true);
        Constructor<?> constructor = initField.getType().getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
