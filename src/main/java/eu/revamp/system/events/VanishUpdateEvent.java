package eu.revamp.system.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called whenever a staff player execute vanish command
 */
@Getter
@RequiredArgsConstructor
public class VanishUpdateEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();

    private final Player player;
    @Getter @Setter
    private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
