package com.craftminecraft.bungee.bungeeban;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import com.craftminecraft.bungee.bungeeban.banstore.*;
import com.craftminecraft.bungee.bungeeban.command.*;
import com.craftminecraft.bungee.bungeeban.listener.PluginMessageListener;
import com.craftminecraft.bungee.bungeeban.listener.ProxiedPlayerListener;
import com.google.common.eventbus.Subscribe;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;


public class BungeeBan extends Plugin implements Listener {
	//private Map<?,?> config;
	public static File configdir = new File("plugins/BungeeBan");
	
	public void onEnable() {
		if (!configdir.exists())
			configdir.mkdirs();
		// 		Will see later for a config. For now, ain't much use. 
/*		File configfile = new File(configdir, "config.yml");
		if (!configfile.exists() || !configfile.isFile()) {
			try {
				configfile.createNewFile();
			} catch (IOException e1) {
				// Can this happen ?
				e1.printStackTrace();
			}
		}
		
		Yaml yaml = new Yaml();
		try {
			config = (Map<?,?>) yaml.load(new FileReader(configfile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (YAMLException e) {
			ProxyServer.getInstance().getLogger().severe("[BungeeBan] Yaml Exception Occured : " + e.getMessage());
		} catch (ClassCastException e) {
			ProxyServer.getInstance().getLogger().severe("[BungeeBan] Top-level object in config.yml is not a Map");
		}
		if (((String) config.get("store")).equalsIgnoreCase("mcfile"))*/
			BanManager.setBanStore(new FileBanStore());
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
		ProxyServer.getInstance().getLogger().log(Level.INFO,"[BungeeBan] Now loaded.");
	}
	
	public void onDisable() {

	}
}