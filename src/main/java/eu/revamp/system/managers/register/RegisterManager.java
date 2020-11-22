package eu.revamp.system.managers.register;

import eu.revamp.system.commands.*;
import eu.revamp.system.commands.coins.CoinsCommand;
import eu.revamp.system.commands.economy.BalanceCommand;
import eu.revamp.system.commands.economy.EconomyCommand;
import eu.revamp.system.commands.economy.PayCommand;
import eu.revamp.system.commands.essentials.*;
import eu.revamp.system.commands.essentials.gamemode.GamemodeAdventureCommand;
import eu.revamp.system.commands.essentials.gamemode.GamemodeCommand;
import eu.revamp.system.commands.essentials.gamemode.GamemodeCreativeCommand;
import eu.revamp.system.commands.essentials.gamemode.GamemodeSurvivalCommand;
import eu.revamp.system.commands.essentials.messages.MessageCommand;
import eu.revamp.system.commands.essentials.messages.ReplyCommand;
import eu.revamp.system.commands.essentials.messages.ToggleMessagesCommand;
import eu.revamp.system.commands.essentials.messages.ToggleSoundsCommand;
import eu.revamp.system.commands.essentials.messages.ignore.IgnoreCommand;
import eu.revamp.system.commands.essentials.panic.PanicCommand;
import eu.revamp.system.commands.essentials.panic.UnPanicCommand;
import eu.revamp.system.commands.essentials.staff.*;
import eu.revamp.system.commands.essentials.staff.item.AddLoreCommand;
import eu.revamp.system.commands.essentials.staff.item.EnchantCommand;
import eu.revamp.system.commands.essentials.staff.item.RemoveLoreCommand;
import eu.revamp.system.commands.essentials.staff.item.RenameCommand;
import eu.revamp.system.commands.essentials.staff.teleport.*;
import eu.revamp.system.commands.permission.BlacklistedPermissionsCommand;
import eu.revamp.system.commands.permission.InfoCommand;
import eu.revamp.system.commands.permission.SetPermissionCommand;
import eu.revamp.system.commands.punishments.PunishInfoCommand;
import eu.revamp.system.commands.punishments.StaffHistoryCommand;
import eu.revamp.system.commands.punishments.StaffRollBackCommand;
import eu.revamp.system.commands.punishments.punish.*;
import eu.revamp.system.commands.punishments.undo.UnBanCommand;
import eu.revamp.system.commands.punishments.undo.UnBlacklistCommand;
import eu.revamp.system.commands.punishments.undo.UnMuteCommand;
import eu.revamp.system.commands.rank.GrantCommand;
import eu.revamp.system.commands.rank.GrantsCommand;
import eu.revamp.system.commands.rank.RankCommand;
import eu.revamp.system.commands.rank.SetRankCommand;
import eu.revamp.system.commands.tags.TagsCommand;
import eu.revamp.system.commands.tags.TagsReloadCommand;
import eu.revamp.system.listeners.*;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.Manager;
import eu.revamp.system.utilities.RegisterMethod;
import eu.revamp.system.utilities.command.BaseCommand;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RegisterManager {

    private PlayerListener playerListener;

    private CoinsCommand coinsCommand;
    private GamemodeAdventureCommand gamemodeAdventureCommand;
    private GamemodeCommand gamemodeCommand;
    private GamemodeCreativeCommand gamemodeCreativeCommand;
    private GamemodeSurvivalCommand gamemodeSurvivalCommand;
    private IgnoreCommand ignoreCommand;
    private MessageCommand messageCommand;
    private ReplyCommand replyCommand;
    private ToggleMessagesCommand toggleMessagesCommand;
    private ToggleSoundsCommand toggleSoundsCommand;
    private PanicCommand panicCommand;
    private UnPanicCommand unPanicCommand;
    private AddLoreCommand addLoreCommand;
    private RemoveLoreCommand removeLoreCommand;
    private EnchantCommand enchantCommand;
    private RenameCommand renameCommand;
    private TeleportCommand teleportCommand;
    private TeleportHereCommand teleportHereCommand;
    private TeleportPositionCommand teleportPositionCommand;
    private TeleportWorldCommand teleportWorldCommand;
    private TeleportAllCommand teleportAllCommand;
    private TopCommand topCommand;
    private AdminChatCommand adminChatCommand;
    private AlertCommand alertCommand;
    private BroadcastCommand broadcastCommand;
    private ChatCommand chatCommand;
    private ClearCommand clearCommand;
    private CraftCommand craftCommand;
    private FeedCommand feedCommand;
    private FlyCommand flyCommand;
    private FreezeCommand freezeCommand;
    private HealCommand healCommand;
    private InvseeCommand invseeCommand;
    private IPsCommand iPsCommand;
    private MassayCommand massayCommand;
    private MoreCommand moreCommand;
    private RepairCommand repairCommand;
    private ReportsCommand reportsCommand;
    private ServerManager serverManager;
    private SetJoinLocationCommand setJoinLocationCommand;
    private SkullCommand skullCommand;
    private SpeedCommand speedCommand;
    private StaffChatCommand staffChatCommand;
    private StaffModeCommand staffModeCommand;
    private SudoCommand sudoCommand;
    private VanishCommand vanishCommand;
    //private LagCommand lagCommand;
    private DiscordCommand discordCommand;
    private GodCommand godCommand;
    private HelpopCommand helpopCommand;
    private PingCommand pingCommand;
    private PlaytimeCommand playtimeCommand;
    private ReportCommand reportCommand;
    private StoreCommand storeCommand;
    private TeamspeakCommand teamspeakCommand;
    private TwitterCommand twitterCommand;
    private BlacklistedPermissionsCommand blacklistedPermissionsCommand;
    private InfoCommand infoCommand;
    private SetPermissionCommand setPermissionCommand;
    private BanCommand banCommand;
    private BanIPCommand banIPCommand;
    private BlacklistCommand blacklistCommand;
    private CheckCommand checkCommand;
    private KickCommand kickCommand;
    private MuteCommand muteCommand;
    private WarnCommand warnCommand;
    private UnBanCommand unBanCommand;
    private UnBlacklistCommand unBlacklistCommand;
    private UnMuteCommand unMuteCommand;
    private PunishInfoCommand punishInfoCommand;
    private StaffHistoryCommand staffHistoryCommand;
    private StaffRollBackCommand staffRollBackCommand;
    private GrantCommand grantCommand;
    private GrantsCommand grantsCommand;
    private RankCommand rankCommand;
    private SetRankCommand setRankCommand;
    private TagsCommand tagsCommand;
    private TagsReloadCommand tagsReloadCommand;
    private RevampCommand revampCommand;
    private ColorCommand colorCommand;
    private ListCommand listCommand;
    private SettingsCommand settingsCommand;
    private ChatColorCommand chatColorCommand;
    private NotesCommand notesCommand;
    private AuthCommand authCommand;
    private BackCommand backCommand;
    private StaffAlertsCommand staffAlertsCommand;
    private DayCommand dayCommand;
    private NightCommand nightCommand;
    private GlobalListCommand globalListCommand;
    private GUIFreezeCommand guiFreezeCommand;
    private SetWarpCommand setWarpCommand;
    private DelWarpCommand delWarpCommand;
    private WarpCommand warpCommand;

    private EconomyCommand economyCommand;
    private BalanceCommand balanceCommand;
    private PayCommand payCommand;
    private NickCommand nickCommand;
    private ExpCommand expCommand;
    private SpawnCommand spawnCommand;
    private AllPlayers allPlayers;

    @RegisterMethod
    public void loadCommands() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (BaseCommand.class.isAssignableFrom(field.getType()) && field.getType().getSuperclass() == BaseCommand.class) {
                field.setAccessible(true);
                try {
                    Constructor constructor = field.getType().getDeclaredConstructor();
                    constructor.newInstance();
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RegisterMethod
    public void loadManagers() {
        for (Field field : RevampSystem.INSTANCE.getClass().getDeclaredFields()) {
            if (Manager.class.isAssignableFrom(field.getType()) && field.getType().getSuperclass() == Manager.class) {
                field.setAccessible(true);
                try {
                    Constructor<?> constructor = field.getType().getDeclaredConstructor(RevampSystem.INSTANCE.getClass());
                    field.set(RevampSystem.INSTANCE, constructor.newInstance(RevampSystem.INSTANCE));
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadListeners(RevampSystem plugin) {
        try {
            List<Listener> listeners = new ArrayList<>();

            if (plugin.isRevampHCFEnabled()) {
                listeners.add(new ChatHCFListener());
            } else {
                listeners.add(new ChatNormalListener());
            }

            listeners.add(new ChatListener());
                    listeners.add(new FreezeListener());
            listeners.add(new GodModeListener());
                    listeners.add(new MenuListener());
            listeners.add(new NameTagListener());
                    listeners.add(new PanicListener());
            listeners.add(this.playerListener = new PlayerListener());
            listeners.add(new PunishmentsListener());
            listeners.add(new QuickAccessListener());
                    listeners.add(new StaffModeListener());
            listeners.add(new WorldListener());
                    //listeners.add(new StaffAuthListener());

            listeners.forEach(listener -> plugin.getServer().getPluginManager().registerEvents(listener, plugin));
        } catch (Exception ignored) {
        }
    }
}
