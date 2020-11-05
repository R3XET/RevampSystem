package eu.revamp.system.punishments;

import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.punishments.api.PunishmentsAPI;
import eu.revamp.system.punishments.managers.MessagesManager;
import eu.revamp.system.punishments.managers.PunishmentsProfileManager;
import eu.revamp.system.punishments.player.PunishPlayerData;
import eu.revamp.system.punishments.utilities.PunishmentsConfigFile;
import lombok.Getter;
import eu.revamp.system.enums.PunishmentsLanguage;
import eu.revamp.system.utilities.Manager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Iterator;

@Getter
public class PunishmentPlugin extends Manager {

    @Getter public static PunishmentPlugin INSTANCE;

    @Getter public static ChatColor MAIN_COLOR = ChatColor.AQUA;
    @Getter public static ChatColor SECONDARY_COLOR = ChatColor.BLUE;
    @Getter public static ChatColor MIDDLE_COLOR = ChatColor.GRAY;

    private PunishmentsConfigFile configFile, languageFile;

    private PunishmentsProfileManager profileManager;
    private MessagesManager messagesManager;

    private PunishmentsAPI api;

    public PunishmentPlugin(RevampSystem plugin) {
        super(plugin);
    }

    public void onEnable() {
        INSTANCE = this;
        this.configFile = new PunishmentsConfigFile(plugin, "config.yml");
        this.languageFile = new PunishmentsConfigFile(plugin, "lang.yml");
        PunishmentsLanguage.setConfig(this.languageFile);
        this.loadLanguage();

        try {
            MAIN_COLOR = ChatColor.valueOf(configFile.getString("COLORS.MAIN"));
        } catch (Exception ignored) {}
        try {
            MIDDLE_COLOR = ChatColor.valueOf(configFile.getString("COLORS.MIDDLE"));
        } catch (Exception ignored) {}
        try {
            SECONDARY_COLOR = ChatColor.valueOf(configFile.getString("COLORS.SECONDARY"));
        } catch (Exception ignored) {}

        this.profileManager = new PunishmentsProfileManager(plugin);
        this.messagesManager = new MessagesManager(plugin);
        this.messagesManager.setup();

        api = new PunishmentsAPI(plugin);

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new PlayerDataUpdate(), 20L * 5, 20L * 5);

        //RevampSystem.INSTANCE.getRegisterManager().getPlayerListener().iOw();
    }

    private void loadLanguage() {
        if (this.languageFile == null) {
            return;
        }
        Arrays.stream(PunishmentsLanguage.values()).forEach(language -> {
            if (this.languageFile.getString(language.getPath(), true) == null) {
                this.languageFile.set(language.getPath(), language.getValue());
            }
        });
        this.languageFile.save();
    }

    private class PlayerDataUpdate implements Runnable {

        @Override
        public void run() {
            Iterator<PunishPlayerData> playerDataIterator = profileManager.getPlayerData().values().iterator();
            try {
                do {
                    PunishPlayerData data = playerDataIterator.next();
                    if (!data.isLoading()) {
                        data.updateBannedAlts();
                    }
                } while (playerDataIterator.hasNext());
            } catch (Exception ignored) { }
        }
    }
}
