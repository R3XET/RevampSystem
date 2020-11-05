package eu.revamp.system.data.grant;

import eu.revamp.spigot.utils.date.DateUtils;
import eu.revamp.system.api.player.PlayerData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GrantProcedure {

    private final PlayerData targetData;

    private GrantProcedureState grantProcedureState = GrantProcedureState.START;
    private long enteredDuration;
    private String enteredReason, rankName, server;
    private boolean permanent = false;

    public String getNiceDuration() {
        if (isPermanent()) return "Permanent";

        return DateUtils.formatTimeMillis(this.enteredDuration);
    }
}
