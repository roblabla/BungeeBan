package fr.lambertz.robin.bungeeban.command;

import fr.lambertz.robin.bungeeban.banstore.IBanStore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class UnbanIpCommand extends Command {
	private IBanStore banstore;

	public UnbanIpCommand(IBanStore banstore) {
		super("unbanip", "bungeeban.command.unbanip");
		this.banstore = banstore;
	}
	@Override
	public void execute(CommandSender arg0, String[] arg1) {
		banstore.unbanIP(arg1[0]);
	}
}