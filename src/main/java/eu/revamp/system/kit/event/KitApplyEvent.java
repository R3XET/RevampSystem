package eu.revamp.system.kit.event;

import eu.revamp.system.kit.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class KitApplyEvent extends PlayerEvent implements Cancellable {
    private static HandlerList handlers = new HandlerList();


    private Kit kit;
    private boolean force;
    private boolean cancelled;

    public KitApplyEvent(Kit kit, Player player, boolean force) {
        super(player);
        this.cancelled = false;
        this.kit = kit;
        this.force = force;
    }

    public static HandlerList getHandlerList() {
        return KitApplyEvent.handlers;
    }

    public Kit getKit() {
        return this.kit;
    }

    public boolean isForce() {
        return this.force;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return KitApplyEvent.handlers;
    }
}
