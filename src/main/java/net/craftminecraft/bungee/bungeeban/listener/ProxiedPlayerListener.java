package net.craftminecraft.bungee.bungeeban.listener;

import com.google.common.eventbus.Subscribe;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;

public class ProxiedPlayerListener implements Listener {
	public ProxiedPlayerListener() {
	}
	
	@Subscribe
	public void onPlayerJoin(PostLoginEvent e) {
		BanEntry ban = BanManager.getBan(e.getPlayer().getName(), "(GLOBAL)");
		if (ban != null) {
			e.getPlayer().disconnect(Utils.formatMessage(ban.getReason(), ban));
			return;
		}
		ban = BanManager.getBan(e.getPlayer().getAddress().getAddress().getHostAddress(), "(GLOBAL)");
		if (ban != null) {
			e.getPlayer().disconnect(Utils.formatMessage(ban.getReason(), ban));
			return;
		}
	}
	
	@Subscribe
	public void onServerConnect(ServerConnectEvent e) {
		BanEntry ban = BanManager.getBan(e.getPlayer().getName(), e.getTarget().getName());
		if (ban != null) {
			e.getPlayer().disconnect(Utils.formatMessage(ban.getReason(), ban));
			return;
		} 
		ban = BanManager.getBan(e.getPlayer().getAddress().getAddress().getHostAddress(), e.getTarget().getName());
		if (ban != null) {
			e.getPlayer().disconnect(Utils.formatMessage(ban.getReason(), ban));
		}
		return;
	}
}
