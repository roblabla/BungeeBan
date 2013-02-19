package com.craftminecraft.bungee.bungeeban.command;

import com.craftminecraft.bungee.bungeeban.BanManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class GUnbanCommand extends Command {

	public GUnbanCommand() {
		super("gunban", "bungeeban.command.gunban");
	}
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/gunban <username>");
			return;
		} 
		BanManager.gunban(args[0]);
		sender.sendMessage(ChatColor.RED + args[0] + " has been unbanned");
	}

}
