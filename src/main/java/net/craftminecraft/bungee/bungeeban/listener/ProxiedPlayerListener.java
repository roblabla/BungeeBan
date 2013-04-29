package net.craftminecraft.bungee.bungeeban.listener;

import com.google.common.eventbus.Subscribe;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.BungeeBan;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;

public class ProxiedPlayerListener implements Listener {
	private BungeeBan plugin;
	public ProxiedPlayerListener(BungeeBan plugin) {
		this.plugin = plugin;
	}
	
	@Subscribe
	public void onPlayerJoin(final LoginEvent e) {
		e.registerIntent(plugin);
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			@Override
			public void run() {
				BanEntry ban = BanManager.getBan(e.getConnection().getName(), "(GLOBAL)");
				if (ban != null) {
					e.completeIntent(plugin);
					e.setCancelled(true);
					e.setCancelReason(Utils.formatMessage(ban.getReason(), ban));
				}
				ban = BanManager.getBan(e.getConnection().getAddress().getAddress().getHostAddress(), "(GLOBAL)");
				if (ban != null) {
		            e.setCancelled(true);
		            e.setCancelReason(Utils.formatMessage(ban.getReason(), ban));
				}
				e.completeIntent(plugin);
			}
		});
	}
	
	@Subscribe
	public void onServerConnect(ServerConnectEvent e) {
		BanEntry ban = BanManager.getBan(e.getPlayer().getName(), e.getTarget().getName());
		if (ban != null) {
			// Ugly workaround the player joined... player left messages
			Server srv = e.getPlayer().getServer();
			if (srv != null)
				e.setTarget(srv.getInfo());
			e.getPlayer().disconnect(Utils.formatMessage(ban.getReason(), ban));
			return;
		} 
		ban = BanManager.getBan(e.getPlayer().getAddress().getAddress().getHostAddress(), e.getTarget().getName());
		if (ban != null) {
			// Ugly workaround the player joined... player left messages
			Server srv = e.getPlayer().getServer();
			if (srv != null)
				e.setTarget(srv.getInfo());
			e.getPlayer().disconnect(Utils.formatMessage(ban.getReason(), ban));
		}
		return;
	}
}
