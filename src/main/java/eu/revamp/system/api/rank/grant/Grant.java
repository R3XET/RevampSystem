package eu.revamp.system.api.rank.grant;

import eu.revamp.spigot.utils.date.DateUtils;
import eu.revamp.system.api.ServerData;
import eu.revamp.system.api.rank.RankData;
import eu.revamp.system.plugin.RevampSystem;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
public class Grant {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    private String rankName;
    private long addedAt, duration, removedAt;
    private String addedBy, reason, removedBy;
    private boolean active, permanent;
    private String server = "Global";

    public boolean hasExpired() {
        if (this.server.equalsIgnoreCase("Global")
                || this.server.equalsIgnoreCase(plugin.getEssentialsManagement().getServerName())) {
            if (!this.isActive()) return true;
            if (plugin.getRankManagement().getRank(this.rankName) == null) return true;
            if (this.isPermanent()) return false;
            return System.currentTimeMillis() >= this.addedAt + this.duration;
        }
        return true;
    }

    public boolean isActiveSomewhere() {
        if (!this.isActive()) return false;
        if (plugin.getRankManagement().getRank(this.rankName) == null) return false;

        if (!this.server.equalsIgnoreCase("Global")) {
            ServerData serverData = plugin.getServerManagement().getServerData(this.server);
            if (serverData != null && !serverData.getServerName().equalsIgnoreCase(plugin
                    .getEssentialsManagement().getServerName())) {
                if (isPermanent()) return true;

                return System.currentTimeMillis() < this.addedAt + this.duration;
            }
        }
        return false;
    }

    public String getNiceDuration() {
        if (isPermanent()) return "Permanent";

        return DateUtils.formatTimeMillis(this.duration);
    }

    public String getNiceExpire() {
        if (!isActive()) return "Expired";
        if (isPermanent()) return "Never";
        if (hasExpired()) return "Expired";

        Calendar from = Calendar.getInstance();
        Calendar to = Calendar.getInstance();

        from.setTime(new Date(System.currentTimeMillis()));
        to.setTime(new Date(this.addedAt + this.getDuration()));

        return DateUtils.formatDateDiff(from, to);
    }

    public RankData getRank() {
        return RevampSystem.INSTANCE.getRankManagement().getRank(this.rankName);
    }
}
