package net.craftminecraft.bungee.bungeeban.command;


import net.craftminecraft.bungee.bungeeban.BanManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnbanCommand extends Command {

	public UnbanCommand() {
		super("unban", "bungeeban.command.unban");
	}
	@Override
	public void execute(CommandSender sender, String[] args) {
		ProxiedPlayer player;
		if (sender instanceof ProxiedPlayer) {
			player = (ProxiedPlayer) sender;
		} else { 
			sender.sendMessage(ChatColor.RED + "Cannot local-unban from console yet.");
			return;
		}
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/unban <username>");
			return;
		} 
		BanManager.unban(args[0], player.getServer().getInfo().getName());
		sender.sendMessage(ChatColor.RED + args[0] + " has been unbanned");
	}
}
