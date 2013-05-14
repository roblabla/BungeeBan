package net.craftminecraft.bungee.bungeeban.command;


import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class UnbanIpCommand extends Command {
	private Plugin plugin;
	
	public UnbanIpCommand(Plugin plugin) {
		super("unbanip");
		this.plugin = plugin;
	}
	
	@Override
	public void execute(final CommandSender sender, final String[] args) {
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
				ProxiedPlayer player = null;
				if (sender instanceof ProxiedPlayer) {
					if (args.length < 1 || args.length > 2) {
						sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
						sender.sendMessage(ChatColor.RED + "/unbanip <ip> [server]");
						return;
					}
					player = (ProxiedPlayer) sender;
				} else {
					if (args.length != 2) {
						sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
						sender.sendMessage(ChatColor.RED + "/unbanip <ip> <server>");
						return;
					}
				}
				
				try {
					if (args.length == 2) {
						if (!Utils.hasPermission(sender, "unbanip", args[1])) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
							return;
						}
						if (BanManager.unban(args[0], args[1])) {
							sender.sendMessage(ChatColor.RED + args[0] + " has been unbanned.");
						} else {
							sender.sendMessage(ChatColor.RED + "An error has occured. Check the proxy.log or notify an admin.");
						}
			
					} else if (player != null) {
						if (Utils.hasPermission(sender, "unbanip", player.getServer().getInfo().getName())) {
							sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
							return;
						}
						if (BanManager.unban(args[0], player.getServer().getInfo().getName())) {
							sender.sendMessage(ChatColor.RED + args[0] + " has been unbanned.");
						} else {
							sender.sendMessage(ChatColor.RED + args[0] + " was not banned.");
						}
					}
				} catch (IllegalArgumentException ex) {
					sender.sendMessage(ChatColor.RED + ex.getMessage());
				}
			}
		});
	}
}