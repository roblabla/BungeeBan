package net.craftminecraft.bungee.bungeeban.command;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.banstore.SimpleBanEntry;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class TempBanCommand extends Command {
	private Plugin plugin;
	
	public TempBanCommand(Plugin plugin) {
		super("tempban");
		this.plugin = plugin;
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
				ProxiedPlayer player = null;
				SimpleBanEntry.Builder newban;
				
				if (sender instanceof ProxiedPlayer) {
					player = (ProxiedPlayer) sender;
					if (args.length < 1) {
						sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
						sender.sendMessage(ChatColor.RED + "/tempban <username> [time] [server] [reason]");
						return;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Console can't local-ban yet. Get your ass in-game.");
					if (args.length < 2) {
						sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
						sender.sendMessage(ChatColor.RED + "/tempban <username> [time] <server> [reason]");
						return;
					}
				}

				newban = new SimpleBanEntry.Builder(args[0])
							.source(sender.getName())
							.expiry();
				
				if (args.length > 1) {
					String reason;
					try {
						// If first argument is time.
						newban.expiry(Utils.parseDate(args[1]));
						if (args.length > 2 && ProxyServer.getInstance().getServerInfo(args[2]) != null) {
							newban.server(args[2]);
							reason = Utils.buildReason(args, 3);
						} else if (player != null) {
							newban.server(player.getServer().getInfo().getName());
							reason = Utils.buildReason(args, 2);
						} else {
							// console sender
							sender.sendMessage(ChatColor.RED + args[1] + " is not a valid server.");
							return;
						}

					} catch (IllegalArgumentException ex) {
						// If first argument ISN'T a time.
						if (ProxyServer.getInstance().getServerInfo(args[1]) != null) {
							newban.server(args[1]);
							reason = Utils.buildReason(args, 2);
						} else if (player != null) {
							newban.server(player.getServer().getInfo().getName());
							reason = Utils.buildReason(args, 1);
						} else {
							// Console sender
							sender.sendMessage(ChatColor.RED + args[1] + " is not a valid server.");
							return;
						}
					}
					if (!reason.isEmpty()) {
						newban.reason(reason);
					}
				} else {
					newban.server(player.getServer().getInfo().getName());
				}
				
				BanEntry entry;
				try {
					entry = newban.build();
				} catch (IllegalArgumentException ex) {
					sender.sendMessage(ChatColor.RED + ex.getMessage());
					return;
				}
				if (!Utils.hasPermission(sender, "tempban", entry.getServer() + "," + entry.getBanned())) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
					return;
				}
				
				if (BanManager.ban(entry)) {
					sender.sendMessage(ChatColor.RED + args[0] + " has been banned.");
				} else {
					sender.sendMessage(ChatColor.RED + "An error has occured. Check the proxy.log or notify an admin.");
				}
			}
		});
	}
}
