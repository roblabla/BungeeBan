package fr.lambertz.robin.bungeeban.command;

import fr.lambertz.robin.bungeeban.banstore.IBanStore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class GBanIpCommand extends Command {
	private IBanStore banstore;
	public GBanIpCommand(IBanStore banstore) {
		super("gbanip", "bungeeban.command.gbanip");
		this.banstore = banstore;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/gban <username> [reason]");
			return;
		} else if (args.length == 1) {
			banstore.gbanIP(args[0], sender.getName(), "");
		} else {
			StringBuilder reasonBuilder = new StringBuilder();
			for (int i = 1;i < args.length; i++) {
				reasonBuilder.append(args[i]);
			}
			banstore.gbanIP(args[0], sender.getName(), reasonBuilder.toString());
		}
	}
}
