package fr.lambertz.robin.bungeeban.command;

import fr.lambertz.robin.bungeeban.banstore.IBanStore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class BanIpCommand extends Command {
	private IBanStore banstore;
	public BanIpCommand(IBanStore banstore) {
		super("banip", "bungeeban.command.banip");
		this.banstore = banstore;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length < 1)
			return;
		if (args.length == 1)
			banstore.banIP(args[0], "", "");
		else if (args.length == 2)
			banstore.banIP(args[0],args[1],"");
		else {
			StringBuilder reasonBuilder = new StringBuilder();
			for (int i = 2;i < args.length; i++) {
				reasonBuilder.append(args[i]);
			}
			banstore.banIP(args[0],args[1],reasonBuilder.toString());
		}
	}
}
