package net.craftminecraft.bungee.bungeeban.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.BungeeBan;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxiedPlayerListener implements Listener {

    private BungeeBan plugin;

    public ProxiedPlayerListener(BungeeBan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final LoginEvent e) {
        e.registerIntent(plugin);
        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    BanEntry ban = BanManager.getBan(e.getConnection().getName(), "(GLOBAL)");
                    if (ban != null) {
                        e.setCancelled(true);
                        String reason = Utils.formatMessage(ban.getReason(), ban);
                        e.setCancelReason(reason != null ? reason : "");
                    }
                    ban = BanManager.getBan(e.getConnection().getAddress().getAddress().getHostAddress(), "(GLOBAL)");
                    if (ban != null) {
                        e.setCancelled(true);
                        String reason = Utils.formatMessage(ban.getReason(), ban);
                        e.setCancelReason(reason != null ? reason : "");
                    }
                } finally {
                    e.completeIntent(plugin);
                }
            }
        });
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        BanEntry ban = BanManager.getBan(e.getPlayer().getName(), e.getTarget().getName());
        if (ban != null) {
            // Ugly workaround the player joined... player left messages
            Server srv = e.getPlayer().getServer();
            ServerInfo target = e.getTarget();
            if (srv != null) {
                e.setTarget(srv.getInfo());
            }
            BanManager.silentKick(e.getPlayer().getName(), target, Utils.formatMessage(ban.getReason(), ban));
            return;
        }
        ban = BanManager.getBan(e.getPlayer().getAddress().getAddress().getHostAddress(), e.getTarget().getName());
        if (ban != null) {
            // Ugly workaround the player joined... player left messages
            Server srv = e.getPlayer().getServer();
            ServerInfo target = e.getTarget();
            if (srv != null) {
                e.setTarget(srv.getInfo());
            }
            BanManager.silentKick(e.getPlayer().getName(), target, Utils.formatMessage(ban.getReason(), ban));
        }
        return;
    }
}
