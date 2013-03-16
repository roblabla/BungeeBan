package net.craftminecraft.bungee.bungeeban.command;


import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class GUnbanIpCommand extends Command {
	public GUnbanIpCommand() {
		super("gunbanip");
	}
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/gunbanip <ip>");
			return;
		}
		
		if (!Utils.hasPermission(sender, "gunbanip", "")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
			return;
		}
		if (BanManager.gunban(args[0])) {
			sender.sendMessage(ChatColor.RED + args[0] + " has been unbanned.");
		} else {
			sender.sendMessage(ChatColor.RED + args[0] + " was not banned.");
		}
	}

}
