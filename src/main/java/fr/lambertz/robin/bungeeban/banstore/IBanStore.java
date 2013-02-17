package fr.lambertz.robin.bungeeban.banstore;

import java.util.List;

/**
 * @author roblabla
 * 
 */
public interface IBanStore {
	
	/**
	 * Adds the specified player to the banned players list, with a specified server.
	 * @param player The player to ban.
	 * @param server The server from which the player is banned.
	 * @param banner The player that triggered the ban.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 * @param reason The reason why the player was banned.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 */
	void ban(BanEntry entry);
	
	/**
	 * Adds the specified player to the banned players list.
	 * @param player The player to ban.
	 * @param banner The player that triggered the ban.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 * @param reason The reason why the player was banned.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 */
	
	void banIP(BanEntry entry);
	
	/**
	 * Removes the specified player from the banned players list .
	 * @param player
	 */
	void unban(String player, String server);
	
	/**
	 * Removes the specified player from the banned players list.
	 * @param player
	 */
	void gunban(String player);
	
	/**
	 * Removes the specified ip from the banned ip list.
	 * @param ip
	 */
	void unbanIP(String ip, String server);
	/**
	 * Removes the specified ip from the banned ip list.
	 * @param ip
	 */
	void gunbanIP(String ip);

	
	List<BanEntry> getBanList();
	List<BanEntry> getIPBanList();
}
