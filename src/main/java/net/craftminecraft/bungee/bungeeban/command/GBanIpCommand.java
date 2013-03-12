package net.craftminecraft.bungee.bungeeban.command;


import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GBanIpCommand extends Command {
	public GBanIpCommand() {
		super("gbanip");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		BanEntry.Builder newban;
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/gbanip <username/ip> [reason]");
			return;
		}

		newban = new BanEntry.Builder(args[0])
					.global()
					.source(sender.getName());
		
		if (args.length > 1) {
			newban.reason(Utils.buildReason(args, 1));
		}
		newban.ipban();
		BanEntry entry = newban.build();
		if (!Utils.hasPermission(sender, "gbanip", "")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
			return;
		}
		BanManager.ban(entry);
	}
}
