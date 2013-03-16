package net.craftminecraft.bungee.bungeeban.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

public class Utils {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	
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
		case "see":
			if (player.hasPermission("bans.player")) {
				return true;
			} else if (player.hasPermission("bans.see." + args[0])) {
				return true;
			}
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
}
