package eu.revamp.system.menu;

import eu.revamp.system.menu.menu.AquaMenu;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.Manager;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MenuManager extends Manager {
    public Map<UUID, AquaMenu> openedMenus = new HashMap<>();
    public Map<UUID, AquaMenu> lastOpenedMenus = new HashMap<>();

    public MenuManager(RevampSystem plugin) {
        super(plugin);
    }
}
