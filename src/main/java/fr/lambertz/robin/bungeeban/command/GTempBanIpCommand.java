package fr.lambertz.robin.bungeeban.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.lambertz.robin.bungeeban.banstore.IBanStore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class GTempBanIpCommand extends Command {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("d'd'k'h'm'm's's'");
	private IBanStore banstore;
	
	public GTempBanIpCommand(IBanStore banstore) {
		super("gtempbanip", "bungeeban.command.gtempbanip");
		this.banstore = banstore;
	}
	

	@Override
	public void execute(CommandSender sender, String[] args) {
		Date date;
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/gtempbanip <ip> <bantime> [reason]");
			return;
		} else {
			try {
				date = dateFormat.parse(args[1]);
			} catch (ParseException ex) {
				return;
			}
		}
		if (args.length == 2) {
			banstore.gtempbanIP(args[0], sender.getName(), "", date);
		} else {
			StringBuilder reasonBuilder = new StringBuilder();
			for (int i = 2;i < args.length; i++) {
				reasonBuilder.append(args[i]);
			}
			banstore.gtempbanIP(args[0],sender.getName(),reasonBuilder.toString(), date);
		}
	}
}
