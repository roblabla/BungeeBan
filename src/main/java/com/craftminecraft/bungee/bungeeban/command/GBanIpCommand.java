package com.craftminecraft.bungee.bungeeban.command;

import com.craftminecraft.bungee.bungeeban.BanManager;
import com.craftminecraft.bungee.bungeeban.banstore.BanEntry;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class GBanIpCommand extends Command {
	public GBanIpCommand() {
		super("gbanip", "bungeeban.command.gbanip");
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		BanEntry newban;
		
		if (args.length < 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/gbanip <ip> [reason]");
			return;
		}
		
		newban = new BanEntry(args[0])
					.setGlobal()
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
