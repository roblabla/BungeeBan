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

public class LookupCommand extends Command {
	public LookupCommand() {
		super("lookup");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		ProxiedPlayer player = null;
		if (sender instanceof ProxiedPlayer) {
			if (args.length < 1 || args.length > 2) {
				sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
				sender.sendMessage(ChatColor.RED + "/lookup <username> [server|global|all]");
			}
			player = (ProxiedPlayer) sender;
		} else {
			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
				sender.sendMessage(ChatColor.RED + "/lookup <username> <server|global|all>");
			}
		}
		List<BanEntry> entries = new ArrayList<BanEntry>();
		String servers = "";
		if (args.length == 2) {
			if (args[1].equalsIgnoreCase("ALL")) {
				servers = "*";
				entries = BanManager.getPlayerBanList(args[0]);
			} else if (args[1].equalsIgnoreCase("global")) {
				servers = "(GLOBAL)";
			} else {
				servers = args[1];
			}
		} else {
			servers = player.getServer().getInfo().getName();
		}
		
		if (!Utils.hasPermission(sender, "lookup", servers)) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");

		}
		
		if (servers != "*") {
			BanEntry entry = BanManager.getBan(args[0], servers);
			if (entry != null) {
				entries.add(entry);
			}
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
