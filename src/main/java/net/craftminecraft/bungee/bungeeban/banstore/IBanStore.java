package net.craftminecraft.bungee.bungeeban.banstore;

import com.google.common.collect.Table;

/**
 * @author roblabla
 * 
 */
public interface IBanStore {
	
	/**
	 * Adds the specified player to the banned list, with a specified server.
	 * @param player The player to ban.
	 * @param server The server from which the player is banned.
	 * @param banner The player that triggered the ban.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 * @param reason The reason why the player was banned.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 */
	boolean ban(BanEntry entry);
	
	/**
	 * Removes the specified player from the banned players list .
	 * @param player
	 */
	boolean unban(String player, String server);
	
	/**
	 * Removes the specified player from the banned players list.
	 * @param player
	 */
	boolean gunban(String player);
	
	/**
	 * Removes the specified ip from the banned ip list.
	 * @param ip
	 */
	boolean unbanIP(String ip, String server);
	/**
	 * Removes the specified ip from the banned ip list.
	 * @param ip
	 */
	boolean gunbanIP(String ip);

	Table<String,String,BanEntry> getBanList();
	Table<String,String,BanEntry> getIPBanList();
	
	BanEntry isBanned(String player, String server);
	BanEntry isIPBanned(String ip, String server);
	
	/**
	 * Reloads the list of bans from whatever storage into memory.
	 * If no list is kept in memory, should just return without doing anything.
	 */
	void reloadBanList();
}
