package net.craftminecraft.bungee.bungeeban.command;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.banstore.SimpleBanEntry;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class GTempBanIpCommand extends Command {
	Plugin plugin;
	public GTempBanIpCommand(Plugin plugin) {
		super("gtempbanip");
		this.plugin = plugin;
	}
	

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
				SimpleBanEntry.Builder newban;

				if (args.length < 1) {
					sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
					sender.sendMessage(ChatColor.RED + "/gtempbanip <ip> [time] [reason]");
					return;
				}
				
				newban = new SimpleBanEntry.Builder(args[0])
							.global()
							.source(sender.getName())
							.expiry();
				
				if (args.length > 1) {
					try {
						newban.expiry(Utils.parseDate(args[1]));
						String reason = Utils.buildReason(args, 2);
						if (!reason.isEmpty()) {
							newban.reason(reason);
						}
					} catch (IllegalArgumentException e) {
						newban.reason(Utils.buildReason(args, 1));
					}
				}
				
				newban.ipban();

				BanEntry entry;
				try {
					entry = newban.build();
				} catch (IllegalArgumentException ex) {
					sender.sendMessage(ChatColor.RED + ex.getMessage());
					return;
				}

				if (!Utils.hasPermission(sender, "gtempbanip", args[0])) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
					return;
				}
				
				if(BanManager.ban(entry)) {
					sender.sendMessage(ChatColor.RED + entry.getBanned() + " has been banned.");
				} else {
					sender.sendMessage(ChatColor.RED + "An error has occured. Check the proxy.log or notify an admin.");
				}	
			}
		});
	}
}
