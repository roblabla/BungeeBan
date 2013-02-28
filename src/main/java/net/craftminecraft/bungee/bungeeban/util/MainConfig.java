package net.craftminecraft.bungee.bungeeban.util;

import net.craftminecraft.bungee.bungeeyaml.AwesomeConfig;
import net.md_5.bungee.api.plugin.Plugin;

public class MainConfig extends AwesomeConfig{
	public MainConfig(Plugin plugin) {
		this.setFile(plugin);
		this.load();
	}
	public String storagetype = "file";
	public String database_address = "localhost";
	public Integer database_port = 3306;
	public String database_name = "minecraft";
	public String database_username = "root";
	public String database_password = "foobar";
}