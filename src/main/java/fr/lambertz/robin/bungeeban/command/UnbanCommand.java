package fr.lambertz.robin.bungeeban.command;

import fr.lambertz.robin.bungeeban.banstore.IBanStore;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class UnbanCommand extends Command {
	private IBanStore banstore;

	public UnbanCommand(IBanStore banstore) {
		super("unban", "bungeeban.command.unban");
		this.banstore = banstore;
	}
	@Override
	public void execute(CommandSender arg0, String[] arg1) {
		banstore.unban(arg1[0]);
	}
}
