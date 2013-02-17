package com.craftminecraft.bungee.bungeeban.command;

import com.craftminecraft.bungee.bungeeban.BanManager;
import com.craftminecraft.bungee.bungeeban.banstore.BanEntry;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;


public class BanCommand extends Command {
	public BanCommand() {
		super("ban", "bungeeban.command.ban");
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
		
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/ban <username> [reason]");
			return;
		}
		
		newban = new BanEntry(args[0])
					.setServer(player.getServer().getInfo().getName())
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
