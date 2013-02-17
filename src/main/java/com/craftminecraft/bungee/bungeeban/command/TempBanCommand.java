package com.craftminecraft.bungee.bungeeban.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.craftminecraft.bungee.bungeeban.BanManager;
import com.craftminecraft.bungee.bungeeban.banstore.BanEntry;


import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TempBanCommand extends Command {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("d'd'k'h'm'm's's'");
	
	public TempBanCommand() {
		super("tempban", "bungeeban.command.tempban");
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
		
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/tempban <username> <time> [reason]");
			return;
		}
		
		try {
			newban = new BanEntry(args[0])
						.setServer(player.getServer().getInfo().getName())
						.setSource(sender.getName())
						.setExpiry(dateFormat.parse(args[1]));
		} catch (ParseException e) {
			sender.sendMessage(ChatColor.RED + "Wrong time format.");
			sender.sendMessage(ChatColor.RED + "#d#h#m#s");
			return;
		}
		
		if (args.length > 2) {
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
