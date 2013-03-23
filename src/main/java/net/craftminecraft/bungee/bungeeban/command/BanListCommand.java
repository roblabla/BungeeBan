package net.craftminecraft.bungee.bungeeban.command;

import java.util.ArrayList;
import java.util.List;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.util.ChatPaginator;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
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
			if (args.length > 2) {
				sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
				sender.sendMessage(ChatColor.RED + "/banlist [server|global|all] [page]");
				return;
			}
			player = (ProxiedPlayer) sender;
		} else {
			if (args.length != 1) {
				sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
				sender.sendMessage(ChatColor.RED + "/banlist <server|global|all>");
				return;
			}
		}
		
		Integer page = 0;
		List<BanEntry> entries = new ArrayList<BanEntry>();
		String servers = "";
		if (player != null) {
			servers = player.getServer().getInfo().getName();
			entries = BanManager.getServerBanList(servers);
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("global")) {
					servers = "(GLOBAL)";
					entries = BanManager.getServerBanList(servers);
					if (args.length > 1) {
						page = Utils.parseInt(args[1]);
						if (page == null) {
							sender.sendMessage(ChatColor.RED + "Invalid argument. <required> [optional]");
							sender.sendMessage(ChatColor.RED + "/banlist [server|global|all] [page]");
							return;
						}
					}
				} else if (BanManager.getServerBanList(args[0]) != null) {
					servers = args[0];
					entries = BanManager.getServerBanList(servers);
					if (args.length > 1) {
						page = Utils.parseInt(args[1]);
						if (page == null) {
							sender.sendMessage(ChatColor.RED + "Invalid argument. <required> [optional]");
							sender.sendMessage(ChatColor.RED + "/banlist [server|global|all] [page]");
							return;
						}
					}
				} else if (Utils.parseInt(args[0]) != null) {
					page = Utils.parseInt(args[0]);
					if (args.length > 1) {
						sender.sendMessage(ChatColor.RED + "Invalid argument. <required> [optional]");
						sender.sendMessage(ChatColor.RED + "/banlist [server|global|all] [page]");
						return;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Invalid argument. <required> [optional]");
					sender.sendMessage(ChatColor.RED + "/banlist [server|global|all] [page]");
					return;
				}
			} // End if args.length > 0
		} else { // If console.
			if (args[0].equalsIgnoreCase("ALL")) {
				servers = "*";
				entries = BanManager.getBanList();
			} else if (args[0].equalsIgnoreCase("global")) {
				servers = "(GLOBAL)";
				entries = BanManager.getServerBanList(servers);
			} else if (ProxyServer.getInstance().getServerInfo(args[0]) != null) {
				servers = args[0];
				entries = BanManager.getServerBanList(servers);
			} else {
				sender.sendMessage(ChatColor.RED + "Server " + args[0] + " does not exist.");
				return;
			}
		}
		if (!Utils.hasPermission(sender, "banlist", servers)) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
			return;
		}

		if (entries.size() == 0) {
			sender.sendMessage(ChatColor.RED + "No bans to show.");
			return;
		}

		StringBuilder srvlistbuilder = new StringBuilder();
		for (BanEntry entry : entries) {
			srvlistbuilder.append(ChatColor.RED + Utils.formatMessage("%banned% | %server% | %reason%\n", entry));
		}
		String srvlist = srvlistbuilder.toString();
		ChatPaginator.ChatPage chatpage = ChatPaginator.paginate(srvlist, page);
		for (int i = 0;i < chatpage.getLines().length;i++) {
			sender.sendMessage(chatpage.getLines()[i]);
		}
	}
}
