package net.craftminecraft.bungee.bungeeban;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.banstore.IBanStore;
import net.craftminecraft.bungee.bungeeban.util.MainConfig;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class BanManager {
	private static Pattern pattern = Pattern.compile(
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
	        "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	private static IBanStore banstore;
	
	// Static class.
	private BanManager() {}
	
	public static void setBanStore(IBanStore banstore) {
		BanManager.banstore = banstore;
	}
	
	public static boolean ban(BanEntry entry) {
		kick(entry.getBanned(), entry.getReason());
		String type = "ban";
		type += (entry.isIPBan()) ? "ip" : "";
		type = type + ((entry.isTempBan()) ? "temp" : "");
		type = type + ((entry.isGlobal()) ? "g" : "");
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
			for (BanEntry entry : banstore.getIPBanList()) {
				if (entry.getBanned().equalsIgnoreCase(playerorip) 
					&& entry.getServer().equalsIgnoreCase(server)) {
					return entry;
				}
			}
		} else {
			for (BanEntry entry : banstore.getBanList()) {
				if (entry.getBanned().equalsIgnoreCase(playerorip) 
					&& entry.getServer().equalsIgnoreCase(server)) {
					return entry;
				}
			}
		}
		return null;
	}
	
	public static List<BanEntry> getBanList(String playerorip) {
		List<BanEntry> entries = new ArrayList<BanEntry>();
		if (isIP(playerorip)) {
			for (BanEntry entry : banstore.getIPBanList()) {
				if (entry.getBanned().equalsIgnoreCase(playerorip)) {
					entries.add(entry);
				}
			}
		} else {
			for (BanEntry entry : banstore.getBanList()) {
				if (entry.getBanned().equalsIgnoreCase(playerorip)) {
					entries.add(entry);
				}
			}
		}
		return entries;
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
	
	public static void kick(String playerorip) {
		kick(playerorip, "You have been kicked");
	}
	
	public static void kick(String playerorip, String reason) {
		for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			if (player.getAddress().getAddress().getHostAddress().equalsIgnoreCase(playerorip)
				|| player.getName().equalsIgnoreCase(playerorip)) {
				player.disconnect(reason);
				return;
			}
		} 
	}
	
	public static void reload() {
		banstore.reloadBanList();
	}
	
	public static boolean isIP(String playerorip) {
	      Matcher matcher = pattern.matcher(playerorip);
	      return matcher.matches();
	}
}
