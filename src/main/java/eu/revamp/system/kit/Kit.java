package eu.revamp.system.kit;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import eu.revamp.spigot.utils.generic.GenericUtils;
import eu.revamp.system.kit.event.KitApplyEvent;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;

import java.util.*;

public class Kit implements ConfigurationSerializable {
    private static final ItemStack DEFAULT_IMAGE = new ItemStack(Material.EMERALD, 1);

    protected UUID uniqueID;
    protected String name;
    protected String description;
    protected ItemStack[] items;
    protected ItemStack[] armour;
    protected ItemStack image;
    protected boolean enabled;
    protected long delayMillis;
    protected String delayWords;
    protected long minPlaytimeMillis;
    protected int maximumUses;

    public Kit(String name, String description, PlayerInventory inventory) {
        this(name, description, inventory, 0L);
    }

    public Kit(String name, String description, Inventory inventory, long milliseconds) {
        this.enabled = true;
        this.uniqueID = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.setItems(inventory.getContents());
        if (inventory instanceof PlayerInventory) {
            PlayerInventory playerInventory = (PlayerInventory) inventory;
            this.setArmour(playerInventory.getArmorContents());
            this.setImage(playerInventory.getItemInHand());
        }
        this.delayMillis = milliseconds;
        this.maximumUses = Integer.MAX_VALUE;
    }

    public Kit(Map<String, Object> map) {
        this.uniqueID = UUID.fromString((String) map.get("uniqueID"));
        this.setName((String) map.get("name"));
        this.setDescription((String) map.get("description"));
        this.setEnabled((Boolean) map.get("enabled"));
        List<ItemStack> items = GenericUtils.createList(map.get("items"), ItemStack.class);
        this.setItems(items.toArray(new ItemStack[items.size()]));
        List<ItemStack> armour = GenericUtils.createList(map.get("armour"), ItemStack.class);
        this.setArmour(armour.toArray(new ItemStack[armour.size()]));
        this.setImage((ItemStack) map.get("image"));
        this.setDelayMillis(Long.parseLong((String) map.get("delay")));
        this.setMaximumUses((Integer) map.get("maxUses"));
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> map = Maps.newLinkedHashMap();
        map.put("uniqueID", this.uniqueID.toString());
        map.put("name", this.name);
        map.put("description", this.description);
        map.put("enabled", this.enabled);
        map.put("items", this.items);
        map.put("armour", this.armour);
        map.put("image", this.image);
        map.put("delay", Long.toString(this.delayMillis));
        map.put("maxUses", this.maximumUses);
        return map;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ItemStack[] getItems() {
        return Arrays.copyOf(this.items, this.items.length);
    }

    public void setItems(ItemStack[] items) {
        int length = items.length;
        this.items = new ItemStack[length];
        for (int i = 0; i < length; ++i) {
            ItemStack next = items[i];
            this.items[i] = ((next == null) ? null : next.clone());
        }
    }

    public ItemStack[] getArmour() {
        return Arrays.copyOf(this.armour, this.armour.length);
    }

    public void setArmour(ItemStack[] armour) {
        int length = armour.length;
        this.armour = new ItemStack[length];
        for (int i = 0; i < length; ++i) {
            ItemStack next = armour[i];
            this.armour[i] = ((next == null) ? null : next.clone());
        }
    }

    public ItemStack getImage() {
        if (this.image == null || this.image.getType() == Material.AIR) {
            this.image = Kit.DEFAULT_IMAGE;
        }
        return this.image;
    }

    public void setImage(ItemStack image) {
        this.image = ((image != null && image.getType() != Material.AIR) ? image.clone() : null);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getDelayMillis() {
        return this.delayMillis;
    }

    public void setDelayMillis(long delayMillis) {
        if (this.delayMillis != delayMillis) {
            Preconditions.checkArgument(this.minPlaytimeMillis >= 0L, "Minimum delay millis cannot be negative");
            this.delayMillis = delayMillis;
            this.delayWords = DurationFormatUtils.formatDurationWords(delayMillis, true, true);
        }
    }

    public String getDelayWords() {
        return DurationFormatUtils.formatDurationWords(this.delayMillis, true, true);
    }

    public long getMinPlaytimeMillis() {
        return this.minPlaytimeMillis;
    }

    public int getMaximumUses() {
        return this.maximumUses;
    }

    public void setMaximumUses(int maximumUses) {
        Preconditions.checkArgument(maximumUses >= 0, "Maximum uses cannot be negative");
        this.maximumUses = maximumUses;
    }

    public String getPermissionNode() {
        //TODO ADD SUPPORT FOR REVAMPHCF
        //return "command.kit." + (RevampSystem.INSTANCE.getConfigManager().getFactionConfig().KIT_MAP ? "" : "hcf.") + this.name;
        return "command.kit." + this.name;
    }

    public Permission getBukkitPermission() {
        String node = this.getPermissionNode();
        return (node == null) ? null : new Permission(node);
    }

    public boolean applyTo(Player player, boolean force, boolean inform) {
        KitApplyEvent event = new KitApplyEvent(this, player, force);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return true;
        }
        ItemStack cursor = player.getItemOnCursor();
        Location location = player.getLocation();
        World world = player.getWorld();
        //TODO ADD SUPPORT FOR REVAMPHCF KITMAP
//        if (RevampSystem.INSTANCE.getConfigManager().getFactionConfig().KIT_MAP) {
//            player.getInventory().setArmorContents(null);
//            player.getInventory().clear();
//        }

        if (cursor != null && cursor.getType() != Material.AIR) {
            player.setItemOnCursor(new ItemStack(Material.AIR, 1));
            world.dropItemNaturally(location, cursor);
        }
        PlayerInventory inventory = player.getInventory();
        ItemStack[] items;
        for (int length = (items = this.items).length, i = 0; i < length; ++i) {
            ItemStack previous = items[i];
            if (previous != null && previous.getType() != Material.AIR) {
                previous = previous.clone();
                for (Map.Entry<Integer, ItemStack> excess : inventory.addItem(new ItemStack[]{previous.clone()}).entrySet()) {
                    world.dropItemNaturally(location, excess.getValue());
                }
            }
        }
        if (this.armour != null) {
            for (int var15 = Math.min(3, this.armour.length); var15 >= 0; --var15) {
                ItemStack var16 = this.armour[var15];
                if (var16 != null && var16.getType() != Material.AIR) {
                    int armourSlot = var15 + 36;
                    ItemStack previous2 = inventory.getItem(armourSlot);
                    var16 = var16.clone();
                    if (previous2 != null && previous2.getType() != Material.AIR) {
                        previous2.setType(Material.AIR);
                        world.dropItemNaturally(location, var16);
                    } else {
                        inventory.setItem(armourSlot, var16);
                    }
                }
            }
        }
        if (inform) {
            player.sendMessage(ChatColor.YELLOW + this.name + ChatColor.YELLOW + " has been applied.");
        }
        player.updateInventory();
        return true;
    }
}
