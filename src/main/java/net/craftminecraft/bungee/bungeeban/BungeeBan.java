package net.craftminecraft.bungee.bungeeban;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.craftminecraft.bungee.bungeeban.banstore.*;
import net.craftminecraft.bungee.bungeeban.command.*;
import net.craftminecraft.bungee.bungeeban.listener.PluginMessageListener;
import net.craftminecraft.bungee.bungeeban.listener.ProxiedPlayerListener;
import net.craftminecraft.bungee.bungeeban.util.MainConfig;
import net.craftminecraft.bungee.bungeeban.util.PluginLogger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;


public class BungeeBan extends Plugin {
	private MainConfig config;
	private File configdir;
	private Logger logger = null;
	
	public void onEnable() {
		configdir = new File("plugins" + File.separator + this.getDescription().getName());
		this.logger = new PluginLogger(this);
		
		if (!configdir.exists())
			configdir.mkdirs();

		config = new MainConfig(this);
		if (config.storagetype.equalsIgnoreCase("file")) {
			BanManager.setBanStore(new FileBanStore());
		} else if (config.storagetype.equalsIgnoreCase("mysql")) {
			BanManager.setBanStore(new MySQLBanStore(logger, config));
		} else {
			getLogger().warning("No valid storage type specified in config. Defaulting to file");
			BanManager.setBanStore(new FileBanStore());
		}
		ProxyServer.getInstance().getPluginManager().registerListener(new ProxiedPlayerListener());
		ProxyServer.getInstance().getPluginManager().registerListener(new PluginMessageListener());
		ProxyServer.getInstance().getPluginManager().registerCommand(new ReloadBansCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new BanCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new TempBanCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new GBanCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new GTempBanCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new UnbanCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new BanIpCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new TempBanIpCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new GBanIpCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new GTempBanIpCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new UnbanIpCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new GUnbanCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(new GUnbanIpCommand());
		getLogger().log(Level.INFO,"Now loaded.");
	}
	
	public void onDisable() {

	}
	
	public Logger getLogger() {
		return this.logger;
	}
}