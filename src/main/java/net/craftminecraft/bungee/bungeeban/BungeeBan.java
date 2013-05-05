package net.craftminecraft.bungee.bungeeban;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.craftminecraft.bungee.bungeeban.banstore.*;
import net.craftminecraft.bungee.bungeeban.command.*;
import net.craftminecraft.bungee.bungeeban.listener.PluginMessageListener;
import net.craftminecraft.bungee.bungeeban.listener.ProxiedPlayerListener;
import net.craftminecraft.bungee.bungeeban.util.MainConfig;
import net.craftminecraft.bungee.bungeeban.util.PluginLogger;
import net.craftminecraft.bungee.bungeeban.util.Metrics;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;


public class BungeeBan extends Plugin {
	private MainConfig config;
	private Metrics metrics;
	private File configdir;
	private Logger logger = null;
	@Override
	public void onEnable() {
		try {
			metrics = new Metrics(this);
			metrics.start();
		} catch (IOException ex) {
			// Metrics failed to load
		}
		configdir = new File("plugins" + File.separator + this.getDescription().getName());
		this.logger = new PluginLogger(this);
		
		if (!configdir.exists())
			configdir.mkdirs();

		config = new MainConfig(this);
		if (config.storagetype.equalsIgnoreCase("file")) {
			BanManager.setBanStore(new FileBanStore(this));
		} else if (config.storagetype.equalsIgnoreCase("mysql")) {
			BanManager.setBanStore(new MySQLBanStore(logger, config));
		} else {
			getLogger().warning("No valid storage type specified in config. Defaulting to file");
			BanManager.setBanStore(new FileBanStore(this));
		}
		ProxyServer.getInstance().getPluginManager().registerListener(this, new ProxiedPlayerListener(this));
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMessageListener());
		
		if(config.commands_Migrate) ProxyServer.getInstance().getPluginManager().registerCommand(this, new MigrateCommand(this));
		if(config.commands_BanList) ProxyServer.getInstance().getPluginManager().registerCommand(this, new BanListCommand());
		if(config.commands_Lookup) ProxyServer.getInstance().getPluginManager().registerCommand(this, new LookupCommand());
		if(config.commands_ReloadBans) ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReloadBansCommand());
		if(config.commands_Ban) ProxyServer.getInstance().getPluginManager().registerCommand(this, new BanCommand());
		if(config.commands_TempBan) ProxyServer.getInstance().getPluginManager().registerCommand(this, new TempBanCommand());
		if(config.commands_GBan) ProxyServer.getInstance().getPluginManager().registerCommand(this, new GBanCommand());
		if(config.commands_GTempBan) ProxyServer.getInstance().getPluginManager().registerCommand(this, new GTempBanCommand());
		if(config.commands_Unban) ProxyServer.getInstance().getPluginManager().registerCommand(this, new UnbanCommand());
		if(config.commands_BanIp) ProxyServer.getInstance().getPluginManager().registerCommand(this, new BanIpCommand());
		if(config.commands_TempBanIp) ProxyServer.getInstance().getPluginManager().registerCommand(this, new TempBanIpCommand());
		if(config.commands_GBanIp) ProxyServer.getInstance().getPluginManager().registerCommand(this, new GBanIpCommand());
		if(config.commands_GTempBanIp) ProxyServer.getInstance().getPluginManager().registerCommand(this, new GTempBanIpCommand());
		if(config.commands_UnbanIp) ProxyServer.getInstance().getPluginManager().registerCommand(this, new UnbanIpCommand());
		if(config.commands_GUnban) ProxyServer.getInstance().getPluginManager().registerCommand(this, new GUnbanCommand());
		if(config.commands_GUnbanIp) ProxyServer.getInstance().getPluginManager().registerCommand(this, new GUnbanIpCommand());
			
		getLogger().log(Level.INFO,"Now loaded.");
	}

	@Override
	public void onDisable() {
		BanManager.setBanStore(null);
		MainConfig.setInstance(null);
	}
	
	public Logger getLogger() {
		return this.logger;
	}
}