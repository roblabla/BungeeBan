package net.craftminecraft.bungee.bungeeban;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.banstore.IBanStore;
import net.craftminecraft.bungee.bungeeban.util.MainConfig;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class BanManager {
	private static Pattern pattern = Pattern.compile(
			"^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");

	private static IBanStore banstore;
	
	// Static class.
	private BanManager() {}
	
	public static void setBanStore(IBanStore banstore) {
		BanManager.banstore = banstore;
	}
	
	public static boolean ban(BanEntry entry) {
		if (entry.isGlobal()) {
			gkick(entry.getBanned(), Utils.formatMessage(entry.getReason(), entry));
		} else {
			kick(entry.getBanned(), entry.getServer(), Utils.formatMessage(entry.getReason(), entry));
		}
		
		String type = "ban";
		type += (entry.isIPBan()) ? "ip" : "";
		type = ((entry.isTempBan()) ? "temp" : "") + type;
		type = ((entry.isGlobal()) ? "g" : "") + type;
		if (banstore.ban(entry)) {
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				if (player.getServer().getInfo().getName().equals(entry.getServer())
					|| entry.isGlobal() 
					|| MainConfig.getInstance().message_sendLocalMsgGlobally) {
					if (Utils.hasPermission(player, "see", type))
						player.sendMessage(Utils.formatMessage(MainConfig.getInstance().getMessageByType(type), entry));
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	public static BanEntry getBan(String playerorip, String server) {
		if (isIP(playerorip)) {
			return banstore.getIPBanList().get(playerorip, server);
		} else {
			return banstore.getBanList().get(playerorip, server);
		}
	}
	
	public static List<BanEntry> getBanList(String playerorip) {
		if (isIP(playerorip)) {
			return ImmutableList.copyOf(banstore.getIPBanList().row(playerorip).values());
		} else {
			return ImmutableList.copyOf(banstore.getBanList().row(playerorip).values());
		}
	}
	
	public static boolean gunban(String playerorip) {
		BanEntry.Builder builder = new BanEntry.Builder(playerorip).global();
		BanEntry entry;
		if (isIP(playerorip)) {
			entry = builder.ipban().build();
			if(banstore.gunbanIP(playerorip)) {
				for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
					if (Utils.hasPermission(player, "see", "gunbanip"))
						player.sendMessage(Utils.formatMessage(MainConfig.getInstance().getMessageByType("gunbanip"), entry));
				}
				return true;
			}
		} else {
			entry = builder.build();
			if (banstore.gunban(playerorip)) {
				for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
					if (Utils.hasPermission(player, "see", "gunban"))
						player.sendMessage(Utils.formatMessage(MainConfig.getInstance().getMessageByType("gunban"), entry));
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean unban(String playerorip, String server) {
		BanEntry.Builder builder = new BanEntry.Builder(playerorip, server);
		BanEntry entry;
		if (isIP(playerorip)) {
			entry = builder.ipban().build();
			if (banstore.unbanIP(playerorip, server)) {
				for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
					if ((MainConfig.getInstance().message_sendLocalMsgGlobally
						|| player.getServer().getInfo().getName().equalsIgnoreCase(server))
						&& Utils.hasPermission(player, "see", "unbanip")) {
						player.sendMessage(Utils.formatMessage(MainConfig.getInstance().getMessageByType("unbanip"), entry));
					}
				}
				return true;
			}
		} else {
			entry = builder.build();
			if (banstore.unban(playerorip, server)) {
				for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
					if ((player.getServer().getInfo().getName().equalsIgnoreCase(server)
						|| MainConfig.getInstance().message_sendLocalMsgGlobally)
						&& Utils.hasPermission(player, "see", "unban")) {
						player.sendMessage(Utils.formatMessage(MainConfig.getInstance().getMessageByType("unban"), entry));
					}
				}
				return true;
			}
		}
		return false;
	}
	
	public static void kick(String playerorip, String server) {
		kick(playerorip, server, "You have been kicked");
	}
	
	public static void kick(String playerorip, String server, String reason) {
		ServerInfo info = ProxyServer.getInstance().getServerInfo(server);
		if (info != null) {
			kick(playerorip, info, reason);
		}
	}
	
	public static void kick(String playerorip, ServerInfo server) {
		kick(playerorip, server, "You have been kicked");
	}
	
	public static void kick(String playerorip, ServerInfo server, String reason) {
		ArrayList<ProxiedPlayer> tokick = new ArrayList<ProxiedPlayer>();
		for (ProxiedPlayer player : server.getPlayers()) {
			if (player.getName().equalsIgnoreCase(playerorip)
				|| player.getAddress().getAddress().getHostAddress().equals(playerorip)) {
				tokick.add(player);
			}
		}
		for (ProxiedPlayer player : tokick) {
			player.disconnect(reason);
		}
		tokick.clear();
	}
	
	public static void gkick(String playerorip) {
		gkick(playerorip, "You have been kicked");
	}
	
	public static void gkick(String playerorip, String reason) {
		ArrayList<ProxiedPlayer> tokick = new ArrayList<ProxiedPlayer>();
		for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			if (player.getAddress().getAddress().getHostAddress().equalsIgnoreCase(playerorip)
				|| player.getName().equalsIgnoreCase(playerorip)) {
				tokick.add(player);
			}
		}
		for (ProxiedPlayer player : tokick) {
			player.disconnect(reason);
		}
		tokick.clear();
	}
	
	public static void reload() {
		banstore.reloadBanList();
	}
	
	public static boolean isIP(String playerorip) {
	      Matcher matcher = pattern.matcher(playerorip);
	      return matcher.matches();
	}
}
