package fr.lambertz.robin.bungeeban.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.lambertz.robin.bungeeban.banstore.IBanStore;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TempBanCommand extends Command {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("d'd'k'h'm'm's's'");
	private IBanStore banstore;
	
	public TempBanCommand(IBanStore banstore) {
		super("tempban", "bungeeban.command.tempban");
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

		Date date;
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/tempban <username> <bantime> [reason]");
			return;
		} else {
			try {
				date = dateFormat.parse(args[1]);
			} catch (ParseException ex) {
				return;
			}
		}
		if (args.length == 2) {
			banstore.tempban(args[0], player.getServer().getInfo().getName(), sender.getName(), "", date);
		} else {
			StringBuilder reasonBuilder = new StringBuilder();
			for (int i = 2;i < args.length; i++) {
				reasonBuilder.append(args[i]);
			}
			banstore.tempban(args[0],player.getServer().getInfo().getName(), sender.getName(),
					reasonBuilder.toString(), date);
		}
	}
}
