package dev.garvis.bungeetransfer;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Events implements Listener {

    Plugin plugin; // could really just pass the servers in.
    
    public Events(Plugin plugin) {
	this.plugin = plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
	event.getPlayer().connect(this.plugin.getProxy().getServers().get("world2"));
    }

    @EventHandler
    public void onChatEvent(ChatEvent event) {
	System.out.println("Hi");
	for (ProxiedPlayer player : this.plugin.getProxy().getPlayers()) {
	    player.connect(this.plugin.getProxy().getServers().get("world2"));
	}
    }
}
