package net.craftminecraft.bungee.bungeeban.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

public class Utils {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private final static Map<Character, ChatColor> BY_CHAR = Maps.newHashMap();

    static {
        for (ChatColor color : ChatColor.values()) {
            BY_CHAR.put(color.toString().charAt(1), color);
        }
    }
    
    public static ChatColor getColorByChar(char code) {
        return BY_CHAR.get(code);
    }

    public static ChatColor getByChar(String code) {
        Preconditions.checkNotNull(code, "Code cannot be null");
        Preconditions.checkArgument(code.length() > 0, "Code must have at least one char");

        return BY_CHAR.get(code.charAt(0));
    }
    
    public static boolean hasPermission(CommandSender player, String command, String stringargs) {
		if (player.hasPermission("bans.superadmin")) {
			return true;
		}
		String[] args = stringargs.split(",");
		switch (command) {
		case "ban":
		case "banip":
		case "tempban":
		case "tempbanip":
			if (args.length == 1) {
				Collection<String> groups = ProxyServer.getInstance().getConfigurationAdapter().getGroups(args[1]);
				if (groups != null) {	
					for (String group : groups) {
						Collection<String> perms = ProxyServer.getInstance().getConfigurationAdapter().getPermissions(group);
						if (perms != null) {
							if (perms.contains("bans.exempt") || perms.contains("bans.exempt.*")
							    || perms.contains("bans.exempt." + args[0])) {
								return false;
							}
						}
					}
				}
			}
		case "unban":
		case "unbanip":
			if (player.hasPermission("bans.admin.*") || player.hasPermission("bans.admin"))
				return true;
			else if (player.hasPermission("bans.command." + command + ".*")) 
				return true;
			else if (player.hasPermission("bans.command." + command))
				return true;
			else if (!args[0].isEmpty()) {
				if (player.hasPermission("bans.command." + command + "." + args[0])) {
					return true;
				} if (player.hasPermission("bans.admin." + args[0])) {
					return true;
				}
			}
			break;
		case "gban":
		case "gbanip":
		case "gtempban":
		case "gtempbanip":
			Collection<String> groups = ProxyServer.getInstance().getConfigurationAdapter().getGroups(args[0]);
			if (groups != null) {	
				for (String group : groups) {
					Collection<String> perms = ProxyServer.getInstance().getConfigurationAdapter().getPermissions(group);
					if (perms != null) {
						if (perms.contains("bans.exempt") || perms.contains("bans.exempt.*")
						    || perms.contains("bans.exempt.global")) {
							return false;
						}
					}
				}
			}
		case "gunban":
		case "gunbanip":
			if (player.hasPermission("bans.globaladmin"))
				return true;
			else if (player.hasPermission("bans.command." + command)) {
				return true;
			}
			break;
		case "reloadbans":
			if (player.hasPermission("bans.command.reloadbans"))
				return true;
			break;
		case "migrate":
			if (player.hasPermission("bans.command.migrate"))
				return true;
			break;
		case "lookup":
			if (player.hasPermission("bans.command.lookup") || player.hasPermission("bans.command.lookup." + args[0])) {
				return true;
			}
			break;
		case "banlist":
			if (player.hasPermission("bans.command.banlist")) {
				return true;
			}
			break;
		case "see":
			if (player.hasPermission("bans.player")) {
				return true;
			} else if (player.hasPermission("bans.see." + args[0])) {
				return true;
			}
			break;
		}
		return false;
	}
	
	public static String formatMessage(String format, BanEntry entry) {
		/* One day this will be beautiful. For now it's just ugly.
		 * String until;
		if (entry.getExpiry() != null) {
			long diff = entry.getExpiry().getTime() - new Date().getTime();
			double days = Math.floor(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
			double hours = Math.floor(TimeUnit.HOURS.convert((long) (diff - days), TimeUnit.MILLISECONDS));
			double minutes = Math.floor(TimeUnit.MINUTES.convert((long) (diff - days - hours), TimeUnit.MILLISECONDS));
			double seconds = Math.floor(TimeUnit.SECONDS.convert((long) (diff - days - hours - minutes), TimeUnit.MILLISECONDS));
			StringBuilder untilbuilder = new StringBuilder();
			untilbuilder.append((days != 0) ? "days : 
		}
		*/
		format = format.replaceAll("%banned%", entry.getBanned())
					   .replaceAll("%source%", entry.getSource())
					   .replaceAll("%created%", dateFormat.format(entry.getCreated()))
					   .replaceAll("%server%", entry.getServer())
					   .replaceAll("%reason%", entry.getReason());
		if (entry.isTempBan()) {
			format = format.replaceAll("%until%", dateFormat.format(entry.getExpiry()));
		//			 .replaceAll("%time%", replacement);
		}
		return ChatColor.translateAlternateColorCodes('&', format);
	}
	
	
	// Date parsing function from HawkEye.
	public static Date parseDate(String date) {
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int mins = 0;
		int secs = 0;

		String nums = "";
		for (int j = 0; j < date.length(); j++) {
			String c = date.substring(j, j+1);
			try { 
				Integer.parseInt(c);
				nums += c;
				continue;
			} catch (Exception e) {}
			int num = Integer.parseInt(nums);
			if (c.equals("w")) weeks = num;
			else if (c.equals("d")) days = num;
			else if (c.equals("h")) hours = num;
			else if (c.equals("m")) mins = num;
			else if (c.equals("s")) secs = num;
			else throw new IllegalArgumentException("Invalid time measurement: &7" + c);
			nums = "";
		}

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.WEEK_OF_YEAR, weeks);
		cal.add(Calendar.DAY_OF_MONTH, days);
		cal.add(Calendar.HOUR, hours);
		cal.add(Calendar.MINUTE, mins);
		cal.add(Calendar.SECOND, secs);
		return cal.getTime();
	}
	
	public static String buildReason(String[] args, int start) {
		StringBuilder reasonBuilder = new StringBuilder();
		for (;start < args.length; start++) {
			reasonBuilder.append(args[start] + " ");
		}
		return reasonBuilder.toString().trim();
	}
	
	public static Integer parseInt(String str) {
		try {
			return new Integer(str);
		} catch (NumberFormatException ex) {
			return null;
		}
	}
}
