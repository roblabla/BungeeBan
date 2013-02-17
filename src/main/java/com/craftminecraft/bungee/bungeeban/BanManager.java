package com.craftminecraft.bungee.bungeeban;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import com.craftminecraft.bungee.bungeeban.banstore.IBanStore;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;


public class BanManager {
	private static IBanStore banstore;
	
	// Static class.
	private BanManager() {}
	
	public static void setBanStore(IBanStore banstore) {
		BanManager.banstore = banstore;
	}
	
	public static void ban(BanEntry entry) {
		if (isIP(entry.getBanned()))
			banstore.banIP(entry);
		else
			banstore.ban(entry);
	//	if (entry.isGlobal())
			kick(entry.getBanned(), entry.getReason());
		/*
		else
			returnToLobby(entry.getBanned());
		will be implemented when config is done.
		*/
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
	
	public static void gunban(String playerorip) {
		if (isIP(playerorip))
			banstore.gunbanIP(playerorip);
		else
			banstore.gunban(playerorip);
	}
	
	public static void unban(String playerorip, String server) {
		if (isIP(playerorip))
			banstore.unbanIP(playerorip, server);
		else
			banstore.unban(playerorip, server);
	}
	
	public static void kick(String playerorip) {
		kick(playerorip, "You have been kicked");
	}
	
	public static void kick(String playerorip, String reason) {
		try {
			InetAddress address = InetAddress.getByName(playerorip);
			for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				if (player.getAddress().getAddress().equals(address)) {
					player.disconnect(reason);
				}
			}
		} catch (UnknownHostException e) {
			ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerorip);
			if (player != null)
				player.disconnect(reason);
		}
	}
	
	private static boolean isIP(String playerorip) {
		try {
			InetAddress.getByName(playerorip);
			return true;
		} catch (UnknownHostException e) {
			return false;
		}
	}
}
