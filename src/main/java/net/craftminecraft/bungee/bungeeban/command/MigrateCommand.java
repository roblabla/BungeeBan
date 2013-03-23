package net.craftminecraft.bungee.bungeeban.command;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.BungeeBan;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.banstore.FileBanStore;
import net.craftminecraft.bungee.bungeeban.banstore.IBanStore;
import net.craftminecraft.bungee.bungeeban.banstore.MySQLBanStore;
import net.craftminecraft.bungee.bungeeban.util.MainConfig;
import net.craftminecraft.bungee.bungeeyaml.InvalidConfigurationException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class MigrateCommand extends Command {
	BungeeBan plugin;
	public MigrateCommand(BungeeBan plugin) {
		super("migrate", "bans.command.importbans");
		this.plugin = plugin;
	}
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/migrate <mysql|file>");
			return;
		}
		IBanStore store;
		if (args[0] == "file") {
			store = new FileBanStore();
		} else if (args[0] == "mysql") {
			store = new MySQLBanStore(this.plugin.getLogger(), MainConfig.getInstance());
		} else {
			sender.sendMessage(ChatColor.RED + "Wrong command format. <required> [optional]");
			sender.sendMessage(ChatColor.RED + "/migrate <mysql|file>");
			return;
		}
		for (BanEntry entry : BanManager.getBanList()) {
			store.ban(entry);
		}
		BanManager.setBanStore(store);
		MainConfig.getInstance().storagetype = args[0];
		try {
			MainConfig.getInstance().save();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		sender.sendMessage("Bans successfully moved to new banstore type");
	}
}
