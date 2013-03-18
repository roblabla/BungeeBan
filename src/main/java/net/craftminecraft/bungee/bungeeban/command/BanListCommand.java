package net.craftminecraft.bungee.bungeeban.command;

import java.util.ArrayList;
import java.util.List;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BanListCommand extends Command {
	public BanListCommand() {
		super("banlist");
	}
	@Override
	public void execute(CommandSender sender, String[] args) {
		ProxiedPlayer player = null;
		if (sender instanceof ProxiedPlayer) {
			if (args.length > 1) {
				sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
				sender.sendMessage(ChatColor.RED + "/banlist [server|global|all]");
			}
			player = (ProxiedPlayer) sender;
		} else {
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
				sender.sendMessage(ChatColor.RED + "/banlist <server|global|all>");
			}
		}
		List<BanEntry> entries = new ArrayList<BanEntry>();
		String servers = "";
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("ALL")) {
				servers = "*";
				entries = BanManager.getBanList();
			} else if (args[0].equalsIgnoreCase("global")) {
				servers = "(GLOBAL)";
				entries = BanManager.getServerBanList(servers);
			} else {
				servers = args[0];
				entries = BanManager.getServerBanList(servers);
			}
		} else {
			servers = player.getServer().getInfo().getName();
			entries = BanManager.getServerBanList(servers);
		}
		
		if (!Utils.hasPermission(sender, "banlist", servers)) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
			return;
		}
	
		if (entries.size() == 0) {
			sender.sendMessage(ChatColor.RED + "No bans to show.");
			return;
		}
		
		for (BanEntry entry : entries) {
			sender.sendMessage(ChatColor.RED + Utils.formatMessage("%banned% | %server% | %reason%", entry));
		}
	}
}
