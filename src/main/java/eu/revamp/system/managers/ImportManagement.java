package eu.revamp.system.managers;

import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.api.rank.RankData;
import eu.revamp.system.api.rank.grant.Grant;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.Manager;
import lombok.Getter;
import lombok.Setter;
import me.lucko.luckperms.api.LuckPermsApi;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ImportManagement extends Manager {
    private LuckPermsApi luckPermsApi;
    private boolean loadingUsers = false;
    private String importingUsersPlayer = "";

    public ImportManagement(RevampSystem plugin) {
        super(plugin);
        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") != null) {
            RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
            if (provider != null) {
                this.luckPermsApi = provider.getProvider();
            }
        }
    }

    public void importLuckPerms() {
        if (plugin.getServer().getPluginManager().getPlugin("LuckPerms") == null) return;
        RankManagement rankManagement = plugin.getRankManagement();

        rankManagement.getRanks().clear();
        this.luckPermsApi.getGroups().forEach(group -> {
            RankData rankData = new RankData(group.getName());
            rankData.setWeight(group.getWeight().isPresent() ? group.getWeight().getAsInt() : 0);

        });
    }

    public void importPermissionEx() {
        if (plugin.getServer().getPluginManager().getPlugin("PermissionsEx") == null) return;

        plugin.getRankManagement().getRanks().clear();
        plugin.getMongoManager().getRanks().drop();

        PermissionManager permManager = PermissionsEx.getPermissionManager();
        permManager.getGroupList().forEach(permissionGroup -> {
            RankData rankData = new RankData(eu.revamp.spigot.utils.string.StringUtils.convertFirstUpperCase(permissionGroup.getName()));
            rankData.setWeight(permissionGroup.getWeight());
            rankData.setPrefix(permissionGroup.getPrefix());
            rankData.setSuffix(permissionGroup.getSuffix());

            permissionGroup.getAllPermissions().values().forEach(list -> list.forEach(permission -> {
                rankData.getPermissions().add(permission);
            }));

            List<String> inheritances = this.getChildren(permissionGroup, new ArrayList<>());
            inheritances.removeIf(name -> name.equalsIgnoreCase(permissionGroup.getName()));

            rankData.getInheritance().addAll(inheritances.stream().map(eu.revamp.spigot.utils.string.StringUtils::convertFirstUpperCase).collect(Collectors.toList()));
            plugin.getRankManagement().getRanks().add(rankData);
        });
        plugin.getRankManagement().createDefaultRank();
    }

    @SuppressWarnings("deprecation")
    public void importPermissionExUsers() {
        if (plugin.getServer().getPluginManager().getPlugin("PermissionsEx") == null) return;

        this.loadingUsers = true;
        plugin.getPlayerManagement().getPlayerData().clear();

        PermissionManager permManager = PermissionsEx.getPermissionManager();
        permManager.getBackend().getUserNames().forEach(name -> {
            PermissionUser permissionUser = permManager.getUser(name);

            OfflinePlayer target = Bukkit.getOfflinePlayer(permissionUser.getName());

            PlayerData playerData = new PlayerData(target.getUniqueId(), target.getName());
            playerData.setPexImport(true);
            permissionUser.getAllPermissions().values().forEach(list -> list.forEach(permission -> {
                playerData.getPermissions().add(permission);
            }));
            permissionUser.getAllParents().values().forEach(permissionGroups -> permissionGroups.forEach(group -> {
                Grant grant = new Grant();
                RankData rankData = plugin.getRankManagement().getRank(eu.revamp.spigot.utils.string.StringUtils.convertFirstUpperCase(group.getName()));

                if (rankData == null) return;

                grant.setRankName(eu.revamp.spigot.utils.string.StringUtils.convertFirstUpperCase(group.getName()));
                grant.setAddedAt(System.currentTimeMillis());
                grant.setReason("Imported from PEX");
                grant.setPermanent(true);
                grant.setAddedBy("Console");
                grant.setActive(true);

                playerData.getGrants().add(grant);
            }));
            playerData.saveData();
        });
        this.loadingUsers = false;
    }

    private List<String> getChildren(PermissionGroup group, List<String> list) {
        if (!list.contains(group.getName())) {
            list.add(group.getName());
        }
        for (final PermissionGroup child : group.getParents()) {
            this.getChildren(child, list);
        }
        return list;
    }
}
