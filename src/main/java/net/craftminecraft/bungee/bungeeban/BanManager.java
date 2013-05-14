package net.craftminecraft.bungee.bungeeban;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.banstore.IBanStore;
import net.craftminecraft.bungee.bungeeban.banstore.SimpleBanEntry;
import net.craftminecraft.bungee.bungeeban.util.MainConfig;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
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
			silentKick(entry.getBanned(), entry.getServer(), Utils.formatMessage(entry.getReason(), entry));
		}
		
		String type = "ban";
		type += (entry.isIPBan()) ? "ip" : "";
		type = ((entry.isTempBan()) ? "temp" : "") + type;
		type = ((entry.isGlobal()) ? "g" : "") + type;
		if (banstore.ban(entry)) {
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				if ((player != null && player.getServer() != null && 
						player.getServer().getInfo().getName().equals(entry.getServer()))
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
			return banstore.isIPBanned(playerorip, server);
		} else {
			return banstore.isBanned(playerorip.toLowerCase(), server);
		}
	}

	public static List<BanEntry> getBanList() {
		return ImmutableList.copyOf(banstore.getBanList().values());
	}

	@Deprecated
	public static List<BanEntry> getBanList(String playerorip) {
		return getPlayerBanList(playerorip.toLowerCase());
	}

	public static List<BanEntry> getPlayerBanList(String player) {
		return ImmutableList.copyOf(banstore.getBanList().row(player.toLowerCase()).values());
	}

	public static List<BanEntry> getIPBanList(String ip) {
		return ImmutableList.copyOf(banstore.getIPBanList().row(ip).values());
	}

	public static List<BanEntry> getServerBanList(String server) {
		ArrayList<BanEntry> entries = new ArrayList<BanEntry>();
		entries.addAll(banstore.getIPBanList().column(server).values());
		entries.addAll(banstore.getBanList().column(server).values());
		return ImmutableList.copyOf(entries);
	}

	public static List<BanEntry> getServerPlayerBanList(String server) {
		return ImmutableList.copyOf(banstore.getBanList().column(server).values());
	}

	public static List<BanEntry> getServerIPBanList(String server) {
		return ImmutableList.copyOf(banstore.getIPBanList().column(server).values());
	}

	public static boolean gunban(String playerorip) {
		SimpleBanEntry.Builder builder = new SimpleBanEntry.Builder(playerorip.toLowerCase()).global();
		BanEntry entry;
		if (isIP(playerorip)) {
			entry = builder.ipban().build();
			if (!MainConfig.getInstance().gunbanRemovesLocalBans) {
				if(banstore.gunbanIP(playerorip.toLowerCase())) {
					for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
						if (Utils.hasPermission(player, "see", "gunbanip"))
							player.sendMessage(Utils.formatMessage(MainConfig.getInstance().getMessageByType("gunbanip"), entry));
					}
					return true;
				}
			} else {
				if (banstore.getIPBanList().row(playerorip).size() > 0) {
					for (Entry<String, BanEntry> entries : banstore.getIPBanList().row(playerorip).entrySet()) {
						banstore.unbanIP(playerorip, entries.getKey());
					}
					for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
						if (Utils.hasPermission(player, "see", "gunbanip"))
							player.sendMessage(Utils.formatMessage(MainConfig.getInstance().getMessageByType("gunbanip"), entry));
					}
					return true;
				} else {
					return false;
				}
			}
		} else {
			entry = builder.build();
			if (!MainConfig.getInstance().gunbanRemovesLocalBans) {
				if (banstore.gunban(playerorip.toLowerCase())) {
					for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
						if (Utils.hasPermission(player, "see", "gunban"))
							player.sendMessage(Utils.formatMessage(MainConfig.getInstance().getMessageByType("gunban"), entry));
					}
					return true;
				}
			} else {
				if (banstore.getBanList().row(playerorip.toLowerCase()).size() > 0) {
					for (Entry<String, BanEntry> entries : banstore.getBanList().row(playerorip.toLowerCase()).entrySet()) {
						banstore.unban(playerorip.toLowerCase(), entries.getKey());
					}
					for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
						if (Utils.hasPermission(player, "see", "gunban"))
							player.sendMessage(Utils.formatMessage(MainConfig.getInstance().getMessageByType("gunban"), entry));
					}
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
	
	public static boolean unban(String playerorip, String server) {
		SimpleBanEntry.Builder builder = new SimpleBanEntry.Builder(playerorip.toLowerCase(), server);
		BanEntry entry;
		if (isIP(playerorip)) {
			entry = builder.ipban().build();
			if (banstore.unbanIP(playerorip.toLowerCase(), server)) {
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
			if (banstore.unban(playerorip.toLowerCase(), server)) {
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

	public static void silentKick(String playerorip, String server) {
		silentKick(playerorip, server, "You have been kicked");
	}
	
	public static void silentKick(String playerorip, String server, String reason) {
		ServerInfo info = ProxyServer.getInstance().getServerInfo(server);
		if (info != null) {
			silentKick(playerorip, info, reason);
		}
	}
	
	public static void silentKick(String playerorip, ServerInfo server) {
		silentKick(playerorip, server, "You have been kicked");
	}
	
	public static void silentKick(String playerorip, ServerInfo server, String reason) {
		ArrayList<ProxiedPlayer> tokick = new ArrayList<ProxiedPlayer>();
		for (ProxiedPlayer player : server.getPlayers()) {
			if (player.getName().equalsIgnoreCase(playerorip)
				|| player.getAddress().getAddress().getHostAddress().equals(playerorip)) {
				tokick.add(player);
			}
		}
		ServerInfo kicksrv = ProxyServer.getInstance().getServerInfo(MainConfig.getInstance().defaults_kickto);
		if (MainConfig.getInstance().defaults_localkickmove && kicksrv != null) {
			for (ProxiedPlayer player : tokick) {
				player.connect(kicksrv);
				player.sendMessage(ChatColor.RED + "You have been kicked : " + reason);
			}
		} else {
			for (ProxiedPlayer player : tokick) {
				player.disconnect(reason);
			}
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
