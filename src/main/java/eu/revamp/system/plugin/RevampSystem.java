package eu.revamp.system.plugin;

import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.database.mongo.MongoManager;
import eu.revamp.system.database.redis.bungee.InComingChannelListener;
import eu.revamp.system.database.redis.other.RedisData;
import eu.revamp.system.database.redis.other.bson.JsonChain;
import eu.revamp.system.database.redis.other.settings.JedisSettings;
import eu.revamp.system.database.redis.payload.action.JedisAction;
import eu.revamp.system.enums.DataType;
import eu.revamp.system.enums.Language;
import eu.revamp.system.events.PlayerOpChangeEvent;
import eu.revamp.system.kit.FlatFileKitManager;
import eu.revamp.system.kit.Kit;
import eu.revamp.system.kit.KitExecutor;
import eu.revamp.system.kit.KitManager;
import eu.revamp.system.managers.*;
import eu.revamp.system.managers.register.RegisterManager;
import eu.revamp.system.menu.MenuManager;
import eu.revamp.system.nametags.NameTagManagement;
import eu.revamp.system.placeholder.MVdWPlaceholderAPIHook;
import eu.revamp.system.placeholder.PlaceHolderAPIExpansion;
import eu.revamp.system.punishments.PunishmentPlugin;
import eu.revamp.system.tasks.*;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.CommandFramework;
import eu.revamp.system.utilities.economy.VaultAccount;
import eu.revamp.system.utilities.file.ConfigFile;
import eu.revamp.system.utilities.server.TPSUtils;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RevampSystem extends JavaPlugin {
    @Getter
    public static RevampSystem INSTANCE;

    @Getter
    @Setter
    private ConfigFile language, dataBase, coreConfig, ranks, tags, staffModeFile, kitsFile;

    @Getter
    private MongoManager mongoManager;
    @Getter
    private RedisData redisData;

    @Getter
    private EssentialsManagement essentialsManagement;
    @Getter
    private ServerManagement serverManagement;
    @Getter
    private RankManagement rankManagement;
    @Getter
    private PlayerManagement playerManagement;
    @Getter
    private PunishmentPlugin punishmentPlugin;
    @Getter
    private TagManagement tagManagement;
    @Getter
    private MenuManager menuManager;
    @Getter
    private FilterManagement filterManager;
    @Getter
    private ChatManagement chatManagement;
    @Getter
    private NameTagManagement nameTagManagement;
    @Getter
    private ImportManagement importManagement;
    @Getter
    private VanishManagement vanishManagement;
    @Getter
    private StaffModeManagement staffModeManagement;
    @Getter

    private RegisterManager registerManager;

    @Getter
    private CommandFramework commandFramework;

    @Getter
    @Setter
    private RevampSystemAPI revampSystemAPI;

    @Getter
    @Setter
    private List<UUID> ops = new ArrayList<>();

    @Getter
    @Setter
    private DataType databaseType = DataType.MONGO;

    @Getter
    private Economy econ = null;

    @Getter
    private KitManager kitManager;


    @Override
    public void onEnable() {
        INSTANCE = this;



        this.coreConfig = new ConfigFile(this, "settings.yml");
        this.language = new ConfigFile(this, "messages.yml");
        this.loadLanguages();
        this.dataBase = new ConfigFile(this, "database.yml");
        this.ranks = new ConfigFile(this, "ranks.yml");
        this.tags = new ConfigFile(this, "tags.yml");
        this.staffModeFile = new ConfigFile(this, "staffmode.yml");
        this.kitsFile = new ConfigFile(this, "kits.yml");


        ConfigurationSerialization.registerClass(Kit.class);
        this.getCommand("kit").setExecutor(new KitExecutor(this));






        this.commandFramework = new CommandFramework(this);

        this.registerManager = new RegisterManager();
        this.registerManager.loadListeners(this);
        this.registerManager.loadCommands();
        this.registerManager.loadManagers();
        this.mongoManager = new MongoManager(this);
        this.essentialsManagement = new EssentialsManagement(this);
        this.serverManagement = new ServerManagement(this);
        this.rankManagement = new RankManagement(this);
        this.playerManagement = new PlayerManagement(this);
        this.punishmentPlugin = new PunishmentPlugin(this);
        this.tagManagement = new TagManagement(this);
        this.menuManager = new MenuManager(this);
        this.filterManager = new FilterManagement(this);
        this.chatManagement = new ChatManagement(this);
        this.nameTagManagement = new NameTagManagement(this);
        this.importManagement = new ImportManagement(this);
        this.vanishManagement = new VanishManagement(this);
        this.staffModeManagement = new StaffModeManagement(this);

        //this.npc = new NPC();



        this.revampSystemAPI = new RevampSystemAPI();


        this.kitManager = new FlatFileKitManager(this);


        if (!this.mongoManager.connect()) return;

        JedisSettings jedisSettings = new JedisSettings(this.dataBase.getString("REDIS.HOST"),
                this.dataBase.getInt("REDIS.PORT"),
                this.dataBase.getString("REDIS.PASSWORD"));
        this.redisData = new RedisData(jedisSettings);

        Tasks.runAsync(this, () -> PlayerUtils.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = this.playerManagement.getPlayerData(player.getUniqueId());
            if (playerData == null) {
                playerData = this.playerManagement.createPlayerData(player.getUniqueId(), player.getName());
            }
            playerData.loadData();
        }));

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DataUpdate(), 20L, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new MenuTask(), 20L, 20L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new FreezeTask(), 70L, 70L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new GUIFreezeTask(), 2L, 2L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new InventoryUpdateTask(), 200L, 200L);

        if (this.getCoreConfig().getBoolean("staff-auth.enabled", true)) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new StaffAuthUpdate(), 60L, 60L);
        }

        this.punishmentPlugin = new PunishmentPlugin(this);
        this.punishmentPlugin.onEnable();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "RevampSync");
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "RevampPermissions");
        Bukkit.getMessenger().registerIncomingPluginChannel(this, "RevampSync", new InComingChannelListener(this));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderAPIExpansion(this).register();
            Bukkit.getConsoleSender().sendMessage(Language.PREFIX + Color.translate("&aPlaceholder API expansion successfully registered."));
        }
        if (Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            new MVdWPlaceholderAPIHook(this, "player_rank").register();
            new MVdWPlaceholderAPIHook(this, "player_color").register();
            new MVdWPlaceholderAPIHook(this, "player_prefix").register();
            new MVdWPlaceholderAPIHook(this, "player_suffix").register();

            Bukkit.getConsoleSender().sendMessage(Language.PREFIX + Color.translate("&aMVdWPlaceholderAPI successfully registered."));
        }

        if (this.coreConfig.getBoolean("tips.enabled")) {
            int seconds = this.coreConfig.getInt("tips.send-every");

            Bukkit.getScheduler().runTaskTimerAsynchronously(this, new TipsTask(), 20L * seconds, 20L * seconds);
        }
        Tasks.runLater(this, () -> this.essentialsManagement.setServerJoinable(true), 20L * 5);
        this.commandFramework.loadCommandsInFile();

        if (!this.commandFramework.isFreezeCommand() && this.commandFramework.getFreezeCommandArgument() != null) {
            Object freezeCommand = this.commandFramework.getFreezeCommandArgument();

            this.commandFramework.unregisterCommands(freezeCommand);
            this.commandFramework.registerCommands(freezeCommand, Arrays.asList("ss", "screenshare", "freeze"));

            this.log("&bSuccessfully registered new allies for GUI Freeze command since Freeze Command is disabled.");
        }

        if (this.isVaultEnabled()){
            if (!RevampSystem.INSTANCE.getCoreConfig().getBoolean("economy.enabled")){
                this.log(" ");
                this.log("&cEconomy is disabled but Vault is loaded, please enable economy in config if you wish to hook Vault!");
                this.log(" ");
            }
            getServer().getServicesManager().register(Economy.class, new VaultAccount(), this, ServicePriority.Normal);
            this.econ = new VaultAccount();
        }


        this.log("&c======&7===========================&c=====");
        this.log("&bRevampSystem has been successfully loaded.");
        this.log("");
        this.log("&bVersion&7: &3v" + this.getDescription().getVersion());
        this.log("&bName&7: &3" + this.getDescription().getName());
        this.log(" ");
        this.log("&bMongo&7: &a" + (this.mongoManager.getMongoClient() != null ? "&aEnabled" : "&cDisabled"));
        this.log("&bRedis&7: &a" + (this.redisData.isConnected() ? "&aEnabled" : "&cDisabled"));
        this.log("&bVault&7: &a" + (this.isVaultEnabled() ? "&aEnabled" : "&cDisabled"));
        this.log("&c======&7===========================&c=====");

        this.redisData.write(JedisAction.SERVER_ONLINE, new JsonChain().addProperty("server", this.essentialsManagement.getServerName()).get());





    }

    @Override
    public void onDisable() {
        if (this.commandFramework == null) return;
        if (this.redisData != null && this.redisData.getPool() != null) {
            this.redisData.write(JedisAction.SERVER_OFFLINE,
                    new JsonChain().addProperty("server", this.essentialsManagement.getServerName()).get());
        }
        //this.npc.save();
        for (Player online : PlayerUtils.getOnlinePlayers()) {
            PlayerData playerData = this.playerManagement.getPlayerData(online.getUniqueId());
            if (playerData.isInStaffMode()) {
                this.staffModeManagement.disableStaffMode(online);
            }
            playerData.saveData();
        }
        this.chatManagement.save();
        PlayerUtils.getOnlinePlayers().forEach(player -> {
            PlayerData playerData = this.playerManagement.getPlayerData(player.getUniqueId());
            if (playerData != null) {
                playerData.getOfflineInventory().save(player);
                playerData.saveData();
            }
        });
        if (this.redisData != null && this.redisData.getPool() != null) {
            this.redisData.getPool().close();
        }
        if (this.mongoManager != null && this.mongoManager.getMongoClient() != null) {
            this.mongoManager.getMongoClient().close();
        }
        Bukkit.getScheduler().cancelTasks(this);
        INSTANCE = null;
    }

    private class DataUpdate implements Runnable {

        @Override
        public void run() {
            JsonChain jsonChain = new JsonChain();
            jsonChain.addProperty("maxPlayers", Bukkit.getMaxPlayers());
            jsonChain.addProperty("whitelisted", Bukkit.hasWhitelist());
            jsonChain.addProperty("name", essentialsManagement.getServerName());
            jsonChain.addProperty("tps1", TPSUtils.getRecentTps()[0]);
            jsonChain.addProperty("tps2", TPSUtils.getRecentTps()[1]);
            jsonChain.addProperty("tps3", TPSUtils.getRecentTps()[2]);
            jsonChain.addProperty("lastTick", System.currentTimeMillis());
            jsonChain.addProperty("players", eu.revamp.spigot.utils.string.StringUtils.getStringFromList(PlayerUtils.getOnlinePlayers().stream()
                    .map(Player::getName).collect(Collectors.toList())));

            redisData.write(JedisAction.SERVER_DATA, jsonChain.get());

            PlayerUtils.getOnlinePlayers().forEach(player -> {
                PlayerData playerData = playerManagement.getPlayerData(player.getUniqueId());
                if (playerData == null) return;
                if (!playerData.isFullJoined()) return;

                if (player.isOp() && !ops.contains(player.getUniqueId())) {
                    ops.add(player.getUniqueId());
                    getServer().getPluginManager().callEvent(new PlayerOpChangeEvent(player, true));
                } else if (!player.isOp() && ops.contains(player.getUniqueId())) {
                    ops.remove(player.getUniqueId());
                    getServer().getPluginManager().callEvent(new PlayerOpChangeEvent(player, false));
                }

                JsonChain playerDataChain = new JsonChain();
                playerDataChain.addProperty("name", player.getName());
                playerDataChain.addProperty("uuid", player.getUniqueId().toString());
                playerDataChain.addProperty("server", essentialsManagement.getServerName());
                try {
                    playerDataChain.addProperty("permissions", eu.revamp.spigot.utils.string.StringUtils.getStringFromList(playerData.getAllPermissions()));
                } catch (Exception ignored) {
                    playerDataChain.addProperty("permissions", "Empty");
                }
                playerDataChain.addProperty("rank", playerData.getHighestRank().getName());
                playerDataChain.addProperty("address", playerData.getAddress());
                playerDataChain.addProperty("lastSeen", playerData.getLastSeen());
                playerDataChain.addProperty("rankWeight", playerData.getHighestRank().getWeight());
                playerDataChain.addProperty("firstJoined", playerData.getFirstJoined());
                playerDataChain.addProperty("op", player.isOp());
                playerDataChain.addProperty("vanished", playerData.isVanished());
                playerDataChain.addProperty("lastActivity", System.currentTimeMillis());
                playerDataChain.addProperty("lastServer", essentialsManagement.getServerName());

                playerDataChain.addProperty("staffChatAlerts", playerData.isStaffChatAlerts());
                playerDataChain.addProperty("adminChatAlerts", playerData.isAdminChatAlerts());
                playerDataChain.addProperty("helpopAlerts", playerData.isHelpopAlerts());
                playerDataChain.addProperty("reportAlerts", playerData.isReportAlerts());

                redisData.write(JedisAction.PLAYER_DATA, playerDataChain.get());
            });
        }
    }

    private void loadLanguages() {
        if (this.language == null) {
            return;
        }
        Arrays.stream(Language.values()).forEach(language -> {
            if (this.language.getString(language.getPath(), true) == null) {
                if (language.getValue() != null) {
                    this.language.set(language.getPath(), language.getValue());
                } else if (language.getListValue() != null && this.language.getStringList(language.getPath(), true) == null) {
                    this.language.set(language.getPath(), language.getListValue());
                }
            }
        });
        this.language.save();
    }


    public boolean isVaultEnabled(){
        return getServer().getPluginManager().getPlugin("Vault") != null;
    }

    public boolean isRevampHCFEnabled(){
        return getServer().getPluginManager().getPlugin("RevampHCF") != null;
    }

    private void log(String message) {
        Bukkit.getConsoleSender().sendMessage(Color.translate(message));
    }

    public void reloadFiles() {
        this.coreConfig = new ConfigFile(this, "settings.yml");
        this.language = new ConfigFile(this, "messages.yml");
        this.loadLanguages();
        this.dataBase = new ConfigFile(this, "database.yml");
        this.ranks = new ConfigFile(this, "ranks.yml");
        this.tags = new ConfigFile(this, "tags.yml");
        this.staffModeFile = new ConfigFile(this, "staffmode.yml");
        this.kitsFile = new ConfigFile(this, "kits.yml");
    }

    private class StaffAuthUpdate implements Runnable {

        @Override
        public void run() {
            if (!getCoreConfig().getBoolean("staff-auth.enabled", true)) return;

            for (Player online : PlayerUtils.getOnlinePlayers()) {
                PlayerData playerData = playerManagement.getPlayerData(online.getUniqueId());

                if (playerData == null) continue;
                if (!online.hasPermission(coreConfig.getString("staff-auth.permission"))) continue;

                if (playerData.isStaffAuth()) {
                    if (!playerData.getAuthPassword().equalsIgnoreCase("")) {
                        coreConfig.getStringList("auth-message").forEach(online::sendMessage);
                    } else {
                        coreConfig.getStringList("auth-message-register").forEach(online::sendMessage);
                    }
                }
            }
        }
    }
}
