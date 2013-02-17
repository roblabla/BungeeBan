package fr.lambertz.robin.bungeeban.command;

import fr.lambertz.robin.bungeeban.BanManager;
import fr.lambertz.robin.bungeeban.banstore.BanEntry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class BanIpCommand extends Command {
	public BanIpCommand() {
		super("banip", "bungeeban.command.banip");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		ProxiedPlayer player;
		BanEntry newban;
		if (sender instanceof ProxiedPlayer) {
			player = (ProxiedPlayer) sender;
		} else {
			sender.sendMessage(ChatColor.RED + "Console can't local-ban yet. Get your ass in-game.");
			return;
		}
		
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/banip <ip> [reason]");
			return;
		}
		
		newban = new BanEntry(args[0])
					.setServer(player.getServer().getInfo().getName())
					.setSource(sender.getName()); 
		
		if (args.length > 1) {
			StringBuilder reasonBuilder = new StringBuilder();
			for (int i = 1;i < args.length; i++) {
				reasonBuilder.append(args[i]);
			}
			newban.setReason(reasonBuilder.toString());
		}
		BanManager.ban(newban);
		sender.sendMessage(ChatColor.RED + newban.getBanned() + " has been banned.");
	}
}
