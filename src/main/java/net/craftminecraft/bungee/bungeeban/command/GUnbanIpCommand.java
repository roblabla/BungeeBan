package net.craftminecraft.bungee.bungeeban.command;


import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class GUnbanIpCommand extends Command {
	private Plugin plugin;
	
	public GUnbanIpCommand(Plugin plugin) {
		super("gunbanip");
		this.plugin = plugin;
	}
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
					sender.sendMessage(ChatColor.RED + "/gunbanip <ip>");
					return;
				}
				
				if (!Utils.hasPermission(sender, "gunbanip", "")) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
					return;
				}
				try {
					if (BanManager.gunban(args[0])) {
						sender.sendMessage(ChatColor.RED + args[0] + " has been unbanned.");
					} else {
						sender.sendMessage(ChatColor.RED + args[0] + " was not banned.");
					}
				} catch (IllegalArgumentException ex) {
					sender.sendMessage(ChatColor.RED + ex.getMessage());
				}	
			}
		});
	}
}
