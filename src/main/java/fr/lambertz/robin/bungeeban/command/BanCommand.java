package fr.lambertz.robin.bungeeban.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import fr.lambertz.robin.bungeeban.banstore.IBanStore;

public class BanCommand extends Command {
	private IBanStore banstore;
	public BanCommand(IBanStore banstore) {
		super("ban", "bungeeban.command.ban");
		this.banstore = banstore;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		ProxiedPlayer player;
		if (sender instanceof ProxiedPlayer) {
			player = (ProxiedPlayer) sender;
		} else {
			sender.sendMessage(ChatColor.RED + "Console can't local-ban yet. Get your ass in-game.");
			return;
		}
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/ban <username> [reason]");
			return;
		} else if (args.length == 1) {
			banstore.ban(args[0], player.getServer().getInfo().getName(), sender.getName(), "");
		} else {
			StringBuilder reasonBuilder = new StringBuilder();
			for (int i = 1;i < args.length; i++) {
				reasonBuilder.append(args[i]);
			}
			banstore.ban(args[0], player.getServer().getInfo().getName(), sender.getName(), reasonBuilder.toString());
		}
	}
}
