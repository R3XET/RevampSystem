package eu.revamp.system.kit;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;

public interface KitManager {
    public static int UNLIMITED_USES = Integer.MAX_VALUE;

    List<Kit> getKits();

    Kit getKit(String p0);

    Kit getKit(UUID p0);

    boolean containsKit(Kit p0);

    void createKit(Kit p0);

    void removeKit(Kit p0);

    Inventory getGui(Player p0);

    void reloadKitData();

    void saveKitData();
}
