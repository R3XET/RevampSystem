package eu.revamp.system.kit;

import com.google.common.collect.Lists;
import eu.revamp.spigot.utils.generic.GenericUtils;
import eu.revamp.system.kit.event.KitRenameEvent;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.file.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.ChatPaginator;

import java.util.*;

public class FlatFileKitManager implements KitManager, Listener {
    private static final int INV_WIDTH = 9;
    private final Map<String, Kit> kitNameMap;
    private final Map<UUID, Kit> kitUUIDMap;
    private final RevampSystem plugin;
    private List<Kit> kits;
    private ConfigFile kitsFile;

    public FlatFileKitManager(RevampSystem plugin) {
        this.plugin = plugin;
        this.kitNameMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.kitUUIDMap = new HashMap<>();
        this.kits = new ArrayList<>();
        this.kitsFile = plugin.getKitsFile();
        this.reloadKitData();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onKitRename(KitRenameEvent event) {
        this.kitNameMap.remove(event.getOldName());
        this.kitNameMap.put(event.getNewName(), event.getKit());
    }

    @Override
    public List<Kit> getKits() {
        return this.kits;
    }

    @Override
    public Kit getKit(UUID uuid) {
        return this.kitUUIDMap.get(uuid);
    }

    @Override
    public Kit getKit(String id) {
        return this.kitNameMap.get(id);
    }

    @Override
    public boolean containsKit(Kit kit) {
        return this.kits.contains(kit);
    }

    @Override
    public void createKit(Kit kit) {
        if (this.kits.add(kit)) {
            this.kitNameMap.put(kit.getName(), kit);
            this.kitUUIDMap.put(kit.getUniqueID(), kit);
        }
    }

    @Override
    public void removeKit(Kit kit) {
        if (this.kits.remove(kit)) {
            this.kitNameMap.remove(kit.getName());
            this.kitUUIDMap.remove(kit.getUniqueID());
        }
    }

    @Override
    public Inventory getGui(Player player) {
        UUID uuid = player.getUniqueId();
        Inventory inventory = Bukkit.createInventory(player, (this.kits.size() + INV_WIDTH - 1) / INV_WIDTH * INV_WIDTH, ChatColor.BLUE + "Kit Selector");
        for (Kit kit : this.kits) {
            ItemStack stack = kit.getImage();
            String description = kit.getDescription();
            String kitPermission = kit.getPermissionNode();
            List<String> lore;
            if (kitPermission != null && !player.hasPermission(kitPermission)) {
                lore = Lists.newArrayList(ChatColor.RED + "You do not own this kit.");
            } else {
                lore = new ArrayList<>();
                if (kit.isEnabled()) {
                    if (kit.getDelayMillis() > 0L) {
                        lore.add(ChatColor.YELLOW + kit.getDelayWords() + " cooldown");
                    }
                } else {
                    lore.add(ChatColor.RED + "Disabled");
                }
                int cloned = kit.getMaximumUses();
                //TODO FINISH
                /*
                if (cloned != Integer.MAX_VALUE) {
                    lore.add(ChatColor.YELLOW + "Used " + this.plugin.getUserManager().getUser(uuid).getKitUses(kit) + '/' + cloned + " times.");
                }*/
                if (description != null) {
                    lore.add(" ");
                    String[] wordWrap;
                    for (int length = (wordWrap = ChatPaginator.wordWrap(description, 24)).length, i = 0; i < length; ++i) {
                        String part = wordWrap[i];
                        lore.add(ChatColor.WHITE + part);
                    }
                }
            }
            ItemStack var7 = stack.clone();
            ItemMeta var8 = var7.getItemMeta();
            var8.setDisplayName(ChatColor.GREEN + kit.getName());
            var8.setLore(lore);
            var7.setItemMeta(var8);
            inventory.addItem(var7);
        }
        return inventory;
    }

    @Override
    public void reloadKitData() {
        this.kitsFile = new ConfigFile(plugin, "kits.yml");
        Object object = this.kitsFile.get("kits");
        if (object instanceof List) {
            this.kits = GenericUtils.createList(object, Kit.class);
            for (Kit kit : this.kits) {
                this.kitNameMap.put(kit.getName(), kit);
                this.kitUUIDMap.put(kit.getUniqueID(), kit);
            }
        }
    }

    @Override
    public void saveKitData() {
        this.kitsFile.set("kits", new LinkedList(this.kits));
        this.kitsFile.save();
    }
}
