package fr.lambertz.robin.bungeeban.command;

import fr.lambertz.robin.bungeeban.BanManager;
import fr.lambertz.robin.bungeeban.banstore.BanEntry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class GBanCommand extends Command {
	public GBanCommand() {
		super("gban", "bungeeban.command.gban");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		BanEntry newban;
		
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/gban <username> [reason]");
			return;
		}
		
		newban = new BanEntry(args[0])
					.setGlobal()
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
