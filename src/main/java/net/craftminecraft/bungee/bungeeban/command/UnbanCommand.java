package net.craftminecraft.bungee.bungeeban.command;


import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class UnbanCommand extends Command {

	public UnbanCommand() {
		super("unban");
	}
	@Override
	public void execute(CommandSender sender, String[] args) {
		ProxiedPlayer player = null;
		if (sender instanceof ProxiedPlayer) {
			if (args.length < 1 || args.length > 2) {
				sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
				sender.sendMessage(ChatColor.RED + "/unban [server] <username>");
				return;
			}
			player = (ProxiedPlayer) sender;
		} else {
			if (args.length != 2) {
				sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
				sender.sendMessage(ChatColor.RED + "/unban <server> <username>");
				return;
			}
		}
		
		if (args.length == 2) {
			if (!Utils.hasPermission(sender, "unban", args[1])) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
			}
			BanManager.unban(args[0], args[1]);

		} else if (player != null) {
			if (Utils.hasPermission(sender, "unban", player.getServer().getInfo().getName())) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
			}
			BanManager.unban(args[0], player.getServer().getInfo().getName());
		}
	}
}
