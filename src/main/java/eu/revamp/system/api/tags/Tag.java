package eu.revamp.system.api.tags;

import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.system.plugin.RevampSystem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

@Getter
@Setter
public class Tag {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    private String prefix = "", name = "";
    private ChatColor color = ChatColor.WHITE;
    private int weight;

    public String getFormat() {
        Replacement format = new Replacement(plugin.getCoreConfig().getString("TAGS_FORMAT"));
        format.add("%color%", color.toString());
        format.add("%uniqueColor%", "");
        format.add("%tag%", this.prefix);
        return format.toString();
    }
}
