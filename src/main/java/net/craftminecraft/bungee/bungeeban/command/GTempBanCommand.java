package net.craftminecraft.bungee.bungeeban.command;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class GTempBanCommand extends Command {
	public GTempBanCommand() {
		super("gtempban");
	}
	

	@Override
	public void execute(CommandSender sender, String[] args) {
		BanEntry.Builder newban;

		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/gtempban <username> [time] [reason]");
			return;
		}
		
		newban = new BanEntry.Builder(args[0])
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
		
		BanEntry entry;
		try {
			entry = newban.build();
		} catch (IllegalArgumentException ex) {
			sender.sendMessage(ChatColor.RED + ex.getMessage());
			return;
		}

		if (!Utils.hasPermission(sender, "gtempban", "")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
			return;
		}
		
		if (BanManager.ban(entry)) {
			sender.sendMessage(ChatColor.RED + entry.getBanned() + " has been banned.");
		} else {
			sender.sendMessage(ChatColor.RED + "An error has occured. Check the proxy.log or notify an admin.");
		}
	}
}
