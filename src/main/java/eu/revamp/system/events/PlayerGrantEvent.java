package eu.revamp.system.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.api.rank.grant.Grant;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called whenever player or console tries to grant rank to a player
 */
@RequiredArgsConstructor
@Getter
public class PlayerGrantEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();

    private final Grant grant;
    private final PlayerData targetData;
    private final CommandSender executor;

    @Getter @Setter private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
