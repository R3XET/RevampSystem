package eu.revamp.system.bungee.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;

public class InComingChannelListener implements Listener {

    @EventHandler
    public void onPermissionRequest(PluginMessageEvent event) {
        try {
            String tag = event.getTag();
            if (tag.equalsIgnoreCase("RevampPermissions")) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                String channel = in.readUTF();

                if (!channel.equals("RevampChannel")) {
                    return;
                }
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(in.readUTF());

                String permission = in.readUTF();
                boolean set = Boolean.parseBoolean(in.readUTF());

                if (player != null) {
                    player.setPermission(permission, set);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onSyncRequest(PluginMessageEvent event) {
        try {
            String tag = event.getTag();
            if (tag.equalsIgnoreCase("RevampSync")) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
                String channel = in.readUTF();

                if (!channel.equals("RevampChannel")) {
                    return;
                }

                String payload = in.readUTF();

                System.out.println("Payload has been received");

                for (ServerInfo serverInfo : ProxyServer.getInstance().getServers().values()) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);
                    try {
                        out.writeUTF("RevampChannel");
                        out.writeUTF(payload);
                    } catch (IOException e) {
                        System.out.println("Failed to send synchronization to spigot, &ccontact plugin developer if you think this is an issue.");
                    }
                    serverInfo.sendData("RevampSync", b.toByteArray());
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
