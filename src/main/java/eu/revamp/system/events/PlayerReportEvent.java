package eu.revamp.system.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import eu.revamp.system.api.player.GlobalPlayer;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called whenever global player tries to report someone
 */
@Getter
@RequiredArgsConstructor
public class PlayerReportEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();

    private final Player player;
    private final GlobalPlayer hacker;
    private final String reason;

    @Getter @Setter private boolean cancelled;


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
