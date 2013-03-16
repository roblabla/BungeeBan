package net.craftminecraft.bungee.bungeeban.command;


import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class GUnbanCommand extends Command {

	public GUnbanCommand() {
		super("gunban");
	}
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/gunban <username>");
			return;
		} 
		if (!Utils.hasPermission(sender, "gunban", "")) {
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
