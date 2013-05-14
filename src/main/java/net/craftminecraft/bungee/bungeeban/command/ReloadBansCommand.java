package net.craftminecraft.bungee.bungeeban.command;


import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.util.MainConfig;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.InvalidConfigurationException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class ReloadBansCommand extends Command {
	private Plugin plugin;

	public ReloadBansCommand(Plugin plugin) {
		super("reloadbans");
		this.plugin = plugin;
	}

	@Override
	public void execute(final CommandSender sender, final String[] args) {
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
				if (!Utils.hasPermission(sender, "reloadbans", "")) {
					sender.sendMessage(ChatColor.RED + "You don't have permission to do this.");
					return;
				}
				BanManager.reload();
				try {
					MainConfig.getInstance().reload();
				} catch (InvalidConfigurationException e) {
					sender.sendMessage(ChatColor.RED + "There is an error in your config.yml.");
					return;
				}
				sender.sendMessage(ChatColor.RED + "Reloaded banlist.");
			}
		});
	}
}
