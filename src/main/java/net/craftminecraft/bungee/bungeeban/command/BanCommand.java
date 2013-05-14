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


public class BanCommand extends Command {
	
	private Plugin plugin;
	
	public BanCommand(Plugin plugin) {
		super("ban");
		this.plugin = plugin;
	}
	
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
				ProxiedPlayer player = null;
				SimpleBanEntry.Builder newban;

				// Check if console or player
				if (sender instanceof ProxiedPlayer) {
					if (args.length < 1) {
						sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
						sender.sendMessage(ChatColor.RED + "/ban <username> [server] [reason]");
						return;
					}
					player = (ProxiedPlayer) sender;
				} else {
					if (args.length < 2) {
						sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
						sender.sendMessage(ChatColor.RED + "/ban <username> <server> [reason]");
						return;
					}
				}
				
				// Parse arguments & create BanEntry
				newban = new SimpleBanEntry.Builder(args[0])
							.source(sender.getName());
				
				String reason = null;
				if (args.length > 1 && ProxyServer.getInstance().getServerInfo(args[1]) != null) {
					newban.server(args[1]);
					reason = Utils.buildReason(args, 2);
				} else if (player != null) {
					newban.server(player.getServer().getInfo().getName());
					reason = Utils.buildReason(args, 1);
				} else {
					sender.sendMessage(ChatColor.RED + args[1] + " is not a valid server.");
					return;
				}
				
				if (reason != null && !reason.isEmpty()) {
					newban.reason(reason);
				}
				
				// Build entry & check for permission
				BanEntry entry;
				try {
					entry = newban.build();
				} catch (IllegalArgumentException ex) {
					sender.sendMessage(ChatColor.RED + ex.getMessage());
					return;
				}
				if (!Utils.hasPermission(sender, "ban", entry.getServer() + "," + entry.getBanned())) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
					return;
				}
				if (BanManager.ban(entry)) {
					sender.sendMessage(ChatColor.RED + entry.getBanned() + " has been banned.");
				} else {
					sender.sendMessage(ChatColor.RED + "An error has occured. Check the proxy.log or notify an admin.");
				}				
			}
		});
	}
}
