package eu.revamp.system.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import eu.revamp.system.api.player.GlobalPlayer;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a player leave network, called if player has 2 seconds of inactivity.
 */
@RequiredArgsConstructor
@Getter
public class GlobalPlayerDestroyEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();
    private final GlobalPlayer globalPlayer;

    @Getter @Setter private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
