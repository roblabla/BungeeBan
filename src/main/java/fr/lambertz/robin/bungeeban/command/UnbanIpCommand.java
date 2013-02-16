package fr.lambertz.robin.bungeeban.command;

import fr.lambertz.robin.bungeeban.banstore.IBanStore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class UnbanIpCommand extends Command {
	private IBanStore banstore;

	public UnbanIpCommand(IBanStore banstore) {
		super("unbanip", "bungeeban.command.unbanip");
		this.banstore = banstore;
	}
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/unbanip <ip>");
			return;
		} 
		banstore.unbanIP(args[0]);
	}
}