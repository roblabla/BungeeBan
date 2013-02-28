package net.craftminecraft.bungee.bungeeban.listener;

import java.util.List;

import com.google.common.eventbus.Subscribe;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;

public class ProxiedPlayerListener implements Listener {
	public ProxiedPlayerListener() {
	}
	
	@Subscribe
	public void onPlayerJoin(LoginEvent e) {
		List<BanEntry> ban = BanManager.getBanList(e.getConnection().getName());
		for(BanEntry entry : ban) {
			if (entry.isGlobal()) {
				e.setCancelled(true);
				e.setCancelReason(entry.getReason());
				return;
			}
		}
		
		ban = BanManager.getBanList(e.getConnection().getAddress().getAddress().getHostAddress());
		for(BanEntry entry : ban) {
			if (entry.isGlobal()) {
				e.setCancelled(true);
				e.setCancelReason(entry.getReason());
				return;
			}
		}
		return;
	}
	
	@Subscribe
	public void onServerConnect(ServerConnectEvent e) {
		BanEntry ban = BanManager.getBan(e.getPlayer().getName(), e.getTarget().getName());
		if (ban != null) {
			e.getPlayer().disconnect(ban.getReason());
			return;
		} 
		ban = BanManager.getBan(e.getPlayer().getAddress().getAddress().getHostAddress(), e.getTarget().getName());
		if (ban != null) {
			e.getPlayer().disconnect(ban.getReason());
		}
		return;
	}
}
