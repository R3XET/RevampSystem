package eu.revamp.system.kit.event;

import eu.revamp.system.kit.Kit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KitRemoveEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();

    private Kit kit;
    private boolean cancelled;

    public KitRemoveEvent(Kit kit) {
        this.cancelled = false;
        this.kit = kit;
    }

    public static HandlerList getHandlerList() {
        return KitRemoveEvent.handlers;
    }

    public Kit getKit() {
        return this.kit;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return KitRemoveEvent.handlers;
    }
}
