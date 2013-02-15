package fr.lambertz.robin.bungeeban.banstore;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author roblabla
 * 
 */
public interface IBanStore {
	
	/**
	 * @param player 	The <code>ProxiedPlayer</code> to check. 
	 * @return 			Returns true if the specified <code>ProxiedPlayer</code> is banned, 
	 * 					otherwise false. 
	 */
	boolean isBanned(String player);
	boolean isIPBanned(String address);
	
	
	/**
	 * Adds the specified player to the banned players list.
	 * @param player The player to ban.
	 * @param banner The player that triggered the ban.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 * @param reason The reason why the player was banned.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 */
	void ban(String player, String banner, String reason);
	/**
	 * Temporarely adds the specified player to the banned player list. The player will automatically
	 * be removed from the banned list when until is reached.
	 * @param player The player to ban.
	 * @param banner The player that triggered the ban.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 * @param reason The reason why the player was banned.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 * @param until  The date at which the banned player must be unbanned.&nbsp;
	 */
	void tempban(String player, String banner, String reason, Date until);
	/**
	 * Removes the specified player from the banned players list.
	 * @param player
	 */
	void unban(String player);
	/**
	 * Adds the specified IP to the banned ip list.
	 * @param ip The IP to ban.
	 * @param banner The player that triggered the ban.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 * @param reason The reason why the ip was banned.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 */
	void banIP(String ip, String banner, String reason);
	/**
	 * Temporarely adds the specified ip to the banned ip list. The ip will automatically
	 * be removed from the banned list when until is reached.
	 * @param ip The ip to ban.
	 * @param banner The player that triggered the ban.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 * @param reason The reason why the ip was banned.&nbsp; MAY BE NULL OR EMPTY FOR DEFAULT.
	 * @param until  The date at which the banned ip must be unbanned.&nbsp;
	 */
	void tempbanIP(String ip, String banner, String reason, Date until);
	/**
	 * Removes the specified ip from the banned ip list.
	 * @param ip
	 */
	void unbanIP(String ip);

	
	Map getBanList();
	Map getIPBanList();
}
