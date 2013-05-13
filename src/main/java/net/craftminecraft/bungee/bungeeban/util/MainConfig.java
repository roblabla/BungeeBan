package net.craftminecraft.bungee.bungeeban.util;

import java.io.File;
import java.util.logging.Level;

import net.craftminecraft.bungee.bungeeban.BungeeBan;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.InvalidConfigurationException;
import net.craftminecraft.bungee.bungeeyaml.supereasyconfig.Comment;
import net.craftminecraft.bungee.bungeeyaml.supereasyconfig.Config;

public class MainConfig extends Config {
	private static MainConfig instance = null;
	public static MainConfig getInstance() {
		return MainConfig.instance;
	}
	public static void setInstance(MainConfig instance) {
		MainConfig.instance = instance;
	}
	
	public MainConfig(BungeeBan plugin) {
        CONFIG_FILE = new File("plugins" + File.separator + plugin.getDescription().getName(), "config.yml");
        CONFIG_HEADER = "BungeeBan Main Config";
		try {
			this.init();
		} catch (InvalidConfigurationException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not load config!", e);
		}
		MainConfig.instance = this;
	}
	
	public String getReasonByType(String type) {
		switch (type) {
		case "ban": return defaults_banreason;
		case "gban": return defaults_gbanreason;
		case "tempban": return defaults_tempbanreason;
		case "gtempban": return defaults_gtempbanreason;
		default: return "";
		}
	}
	public String getMessageByType(String type) {
		switch (type) {
		case "ban": return message_ban;
		case "banip": return message_banip;
		case "tempban": return message_tempban;
		case "tempbanip": return message_tempbanip;
		case "gban": return message_gban;
		case "gbanip": return message_gbanip;
		case "gtempban": return message_gtempban;
		case "gtempbanip": return message_gtempbanip;
		case "unban": return message_unban;
		case "gunban": return message_gunban;
		case "unbanip": return message_unbanip;
		case "gunbanip": return message_gunbanip;
		default: return "";
		}
	}

	@Comment("How to store the bans. Can either be mysql or file") 
	public String storagetype = "file";
	
	@Comment("The details of the database. Only needed for mysql store")
	public String database_address = "localhost";
	public int database_port = 3306;
	public String database_name = "minecraft";
	public String database_username = "root";
	public String database_password = "foobar";

	@Comment("Should /gunban also remove local bans (along with global ones).")
	public boolean gunbanRemovesLocalBans = false;

	@Comment("Should you put this to true, default reason can contain %reason%, and\n" +
			"setting a reason on ban will still use the default. Example : \n" +
			"banreason: 'Banned for %reason%'\n" +
			"/ban spamming\n" +
			"Will ban with reason 'Banned for spamming'")
	public boolean defaults_reasonExtend = false;
	public String defaults_banreason = "Banned by an operator.";
	public String defaults_gbanreason = "Banned by an operator.";
	public String defaults_tempbanreason = "Banned by an operator for %until%.";
	public String defaults_gtempbanreason = "Banned by an operator for %until%.";
	public String defaults_tempbantime = "1d";
	@Comment("Set this to true to redirect player to server defined in kickto instead of kicking for local kicks")
	public boolean defaults_localkickmove = false;
	public String defaults_kickto = "lobby";

	@Comment("Should messages for /ban sent to every server, or just server on which ban occured.")
	public boolean message_sendLocalMsgGlobally = false;
	public String message_ban = "%source% banned %banned% from %server% for %reason%";
	public String message_banip = "%source% banned %banned% from %server% for %reason%";
	public String message_gban = "%source% global banned %banned% for %reason%";
	public String message_gbanip = "%source% global banned %banned% for %reason%";
	public String message_tempban = "%source% tempbanned %banned% from %server% until %until% for %reason%";
	public String message_tempbanip = "%source% tempbanned %banned% from %server% until %until% for %reason%";
	public String message_gtempban = "%source% global tempbanned %banned% until %until% for %reason%";
	public String message_gtempbanip = "%source% global tempbanned %banned% until %until% for %reason%";
	public String message_unban = "%banned% got unbanned from %server%!";
	public String message_unbanip = "%banned% got unbanned from %server%!";
	public String message_gunban = "%banned% got unbanned globally!";
	public String message_gunbanip = "%banned% got unbanned globally!";
}