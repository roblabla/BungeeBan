package net.craftminecraft.bungee.bungeeban.command;


import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class UnbanCommand extends Command {
	private Plugin plugin;
	public UnbanCommand(Plugin plugin) {
		super("unban");
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
						sender.sendMessage(ChatColor.RED + "/unban <username> [server]");
						return;
					}
					player = (ProxiedPlayer) sender;
				} else {
					if (args.length != 2) {
						sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
						sender.sendMessage(ChatColor.RED + "/unban <username> <server>");
						return;
					}
				}
				String server;
				if (args.length == 2) {
					server = args[1];
				} else if (player != null) {
					server = player.getServer().getInfo().getName();
				} else {
					return;
				}
				
				if (!Utils.hasPermission(sender, "unban", server)) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
					return;
				}

				try {
					if (BanManager.unban(args[0], server)) {
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
