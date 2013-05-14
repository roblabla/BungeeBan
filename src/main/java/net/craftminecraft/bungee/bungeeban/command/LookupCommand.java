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
import net.md_5.bungee.api.plugin.Plugin;

public class LookupCommand extends Command {
	String helpStr = "Page %d/%d";
	Plugin plugin;

	public LookupCommand(Plugin plugin) {
		super("lookup");
		this.plugin = plugin;
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
				ProxiedPlayer player = null;
				if (sender instanceof ProxiedPlayer) {
					if (args.length < 1 || args.length > 2) {
						sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
						sender.sendMessage(ChatColor.RED + "/lookup <username> [server|global|all] [page]");
						return;
					}
					player = (ProxiedPlayer) sender;
				} else {
					if (args.length != 2) {
						sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
						sender.sendMessage(ChatColor.RED + "/lookup <username> <server|global|all>");
						return;
					}
				}
				List<BanEntry> entries;
				if (player != null) { // If CommandSender is player. 
					String servers = player.getServer().getInfo().getName(); // Default values
					entries = BanManager.getPlayerBanList(args[0]);
					Integer page = 1;
					
					if (args.length > 1) {                            // If there is arguments
						if (args[1].equalsIgnoreCase("global")) {
							servers = "global";
							entries = new ArrayList<BanEntry>();
							if (BanManager.getBan(args[1], "(GLOBAL)") != null) {
								entries.add(BanManager.getBan(args[1], "(GLOBAL)"));
							}
							if (args.length > 2) {
								page = Utils.parseInt(args[2]);
								if (page == null) {
									sender.sendMessage(ChatColor.RED + "Invalid argument. <required> [optional]");
									sender.sendMessage(ChatColor.RED + "/banlist [server|global|all] [page]");
									return;
								}
							}
						} else if (args[1].equalsIgnoreCase("all")) {
							servers = "*";
							entries = BanManager.getPlayerBanList(args[1]);
							if (args.length > 2) {
								page = Utils.parseInt(args[2]);
								if (page == null) {
									sender.sendMessage(ChatColor.RED + "Invalid argument. <required> [optional]");
									sender.sendMessage(ChatColor.RED + "/banlist [server|global|all] [page]");
									return;
								}
							}
						} else if (Utils.parseInt(args[1]) != null) {
							page = Utils.parseInt(args[1]);
							if (args.length > 2) {
								sender.sendMessage(ChatColor.RED + "Invalid argument. <required> [optional]");
								sender.sendMessage(ChatColor.RED + "/banlist [server|global|all] [page]");
								return;
							}
						} else {
							servers = args[1];
							entries = new ArrayList<BanEntry>();
							if (BanManager.getBan(args[0], args[1]) != null) {
								entries.add(BanManager.getBan(args[0], args[1]));
							}
							page = Utils.parseInt(args[1]);
							if (args.length > 2) {
								sender.sendMessage(ChatColor.RED + "Invalid argument. <required> [optional]");
								sender.sendMessage(ChatColor.RED + "/banlist [server|global|all] [page]");
								return;
							}
						}
					} // End if args.length > 0
					if (!Utils.hasPermission(sender, "lookup", servers)) {
						sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
						return;
					}
					if (entries == null || entries.size() == 0) {
						sender.sendMessage(ChatColor.RED + "No bans to show.");
						return;
					}
					StringBuilder srvlistbuilder = new StringBuilder();
					for (BanEntry entry : entries) {
						srvlistbuilder.append(ChatColor.RED + Utils.formatMessage("%banned% | %server% | %reason%\n", entry));
					}
					String srvlist = srvlistbuilder.toString();
					ChatPaginator.ChatPage chatpage = ChatPaginator.paginate(srvlist, page, ChatPaginator.GUARANTEED_NO_WRAP_CHAT_PAGE_WIDTH, ChatPaginator.CLOSED_CHAT_PAGE_HEIGHT-1);
					for (int i = 0;i < chatpage.getLines().length;i++) {
						sender.sendMessage(chatpage.getLines()[i]);
					}
					
					sender.sendMessage(String.format(helpStr,chatpage.getPageNumber(),chatpage.getTotalPages()));
				} else { // If console
					if (args[1].equalsIgnoreCase("ALL")) {
						entries = BanManager.getPlayerBanList(args[0]);
					} else if (args[1].equalsIgnoreCase("global")) {
						entries = new ArrayList<BanEntry>();
						if (BanManager.getBan(args[0], "(GLOBAL)") != null) {
							entries.add(BanManager.getBan(args[0], "(GLOBAL)"));
						}
					} else if (ProxyServer.getInstance().getServerInfo(args[1]) != null) {
						entries = new ArrayList<BanEntry>();
						if (BanManager.getBan(args[0], args[1]) != null) {
							entries.add(BanManager.getBan(args[0], args[1]));
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Server " + args[1] + " does not exist.");
						return;
					}
					StringBuilder srvlistbuilder = new StringBuilder();
					for (BanEntry entry : entries) {
						srvlistbuilder.append(ChatColor.RED + Utils.formatMessage("%banned% | %server% | %reason%\n", entry));
					}
					String srvlist = srvlistbuilder.toString();
					String[] srvlistmsgs = srvlist.split("\n");
					for (int i = 0;i < srvlistmsgs.length;i++) {
						sender.sendMessage(srvlistmsgs[i]);
					}
				}
			}
		});
	}
}
