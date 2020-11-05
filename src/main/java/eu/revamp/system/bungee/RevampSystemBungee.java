package eu.revamp.system.bungee;

import eu.revamp.system.bungee.color.BungeeColor;
import eu.revamp.system.bungee.listeners.InComingChannelListener;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class RevampSystemBungee extends Plugin {

    public static RevampSystemBungee INSTANCE;

    @Override @SuppressWarnings("deprecation")
    public void onEnable() {
        ProxyServer.getInstance().registerChannel("RevampPermissions");
        ProxyServer.getInstance().registerChannel("RevampSync");
        ProxyServer.getInstance().getPluginManager().registerListener(this, new InComingChannelListener());

        ProxyServer.getInstance().getConsole().sendMessage(BungeeColor.translate("&aRevamp Bungee is now enabled."));
    }
}
