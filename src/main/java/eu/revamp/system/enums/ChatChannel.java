package eu.revamp.system.enums;

import eu.revamp.hcf.RevampHCF;
import eu.revamp.system.plugin.RevampSystemAPI;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Locale;

public enum ChatChannel
{

    FACTION("FACTION", 0, "Faction"),
    ALLIANCE("ALLIANCE", 1, "Alliance"),
    CAPTAIN("CAPTAIN", 2, "Captain"),
    PUBLIC("PUBLIC", 3, "Public");

    @Getter
    private final String name;

    ChatChannel(String s, int n, String name) {
        this.name = name;
    }

    public String getDisplayName() {
        String prefix;
        switch (this) {
            case FACTION: {
                prefix = RevampHCF.getInstance().getConfiguration().getTeammateColor().toString();
                break;
            }
            case ALLIANCE: {
                prefix = RevampHCF.getInstance().getConfiguration().getAllyColor().toString();
                break;
            }
            case CAPTAIN: {
                prefix = RevampHCF.getInstance().getConfiguration().getCaptainColor().toString();
                break;
            }
            default: {
                prefix = RevampHCF.getInstance().getConfiguration().getEnemyColor().toString();
                break;
            }
        }
        return prefix + this.name;
    }

    public String getShortName() {
        switch (this) {
            case FACTION: {
                return "FC";
            }
            case CAPTAIN: {
                return "CC";
            }
            case ALLIANCE: {
                return "AC";
            }
            default: {
                return "PC";
            }
        }
    }

    public static ChatChannel parse(String id) {
        return parse(id, ChatChannel.PUBLIC);
    }

    public static ChatChannel parse(String id, ChatChannel def) {
        id = id.toLowerCase(Locale.ENGLISH);
        String s;
        switch ((s = id).hashCode()) {
            case -1243020381: {
                if (!s.equals("global")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case -1091888612: {
                if (!s.equals("faction")) {
                    return (def == null) ? null : def.getRotation();
                }
                break;
            }
            case -977423767: {
                if (!s.equals("public")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 97: {
                if (!s.equals("a")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.ALLIANCE;
            }
            case 99: {
                if (!s.equals("c")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.CAPTAIN;
            }
            case 102: {
                if (!s.equals("f")) {
                    return (def == null) ? null : def.getRotation();
                }
                break;
            }
            case 103: {
                if (!s.equals("g")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 112: {
                if (!s.equals("p")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 3106: {
                if (!s.equals("ac")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.ALLIANCE;
            }
            case 3166: {
                if (!s.equals("ca")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.CAPTAIN;
            }
            case 3261: {
                if (!s.equals("fc")) {
                    return (def == null) ? null : def.getRotation();
                }
                break;
            }
            case 3292: {
                if (!s.equals("gc")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 3571: {
                if (!s.equals("pc")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 98258: {
                if (!s.equals("cap")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.CAPTAIN;
            }
            case 101128: {
                if (!s.equals("fac")) {
                    return (def == null) ? null : def.getRotation();
                }
                break;
            }
            case 111357: {
                if (!s.equals("pub")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 2996984: {
                if (!s.equals("ally")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.ALLIANCE;
            }
            case 3135084: {
                if (!s.equals("fact")) {
                    return (def == null) ? null : def.getRotation();
                }
                break;
            }
            case 107017530: {
                if (!s.equals("publi")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.PUBLIC;
            }
            case 552565540: {
                if (!s.equals("captain")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.CAPTAIN;
            }
            case 1806944311: {
                if (!s.equals("alliance")) {
                    return (def == null) ? null : def.getRotation();
                }
                return ChatChannel.ALLIANCE;
            }
        }
        return ChatChannel.FACTION;
    }

    public ChatChannel getRotation() {
        switch (this) {
            case FACTION: {
                return ChatChannel.PUBLIC;
            }
            case CAPTAIN:
            case ALLIANCE: {
                return ChatChannel.FACTION;
            }
            case PUBLIC: {
                return (RevampHCF.getInstance().getConfiguration().getMaxAllysPerFaction() > 0) ? ChatChannel.ALLIANCE : ChatChannel.FACTION;
            }
            default: {
                return ChatChannel.PUBLIC;
            }
        }
    }

    public String getRawFormat(Player player) {
        switch (this) {
            case FACTION: {
                return RevampSystemAPI.plugin.getCoreConfig().getString("CHAT.FACTION").replace("%ign%", player.getName()).replace("%message%", "%2$s");
            }
            case ALLIANCE: {
                return RevampSystemAPI.plugin.getCoreConfig().getString("CHAT.ALLY").replace("%ign%", player.getName()).replace("%message%", "%2$s");
            }
            case CAPTAIN: {
                return RevampSystemAPI.plugin.getCoreConfig().getString("CHAT.OFFICER").replace("%ign%", player.getName()).replace("%message%", "%2$s");
            }
            default: {
                throw new IllegalArgumentException("Cannot get the raw format for public chat channel");
            }
        }
    }
}

