package fr.lambertz.robin.bungeeban.command;

import net.md_5.bungee.api.CommandSender;
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
		if (args.length < 1)
			return;
		if (args.length == 1)
			banstore.ban(args[0], "", "");
		else if (args.length == 2)
			banstore.ban(args[0],args[1],"");
		else {
			StringBuilder reasonBuilder = new StringBuilder();
			for (int i = 2;i < args.length; i++) {
				reasonBuilder.append(args[i]);
			}
			banstore.ban(args[0],args[1],reasonBuilder.toString());
		}
	}
}
