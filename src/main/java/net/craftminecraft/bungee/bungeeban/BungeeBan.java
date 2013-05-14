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
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new MigrateCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new BanListCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new LookupCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReloadBansCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new BanCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TempBanCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new GBanCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new GTempBanCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new UnbanCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new BanIpCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TempBanIpCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new GBanIpCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new GTempBanIpCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new UnbanIpCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new GUnbanCommand(this));
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new GUnbanIpCommand(this));
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