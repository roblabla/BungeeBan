package net.craftminecraft.bungee.bungeeban.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.common.eventbus.Subscribe;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;

public class PluginMessageListener implements Listener {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("d'd'k'h'm'm's's'");

	@Subscribe
	public void onPluginmessage(PluginMessageEvent e) {
		// Checks if the one sending the message is the server.
		if (e.getTag() != "BungeeBan" || !(e.getSender() instanceof Server))
			return;
		//String servername = ((Server)e.getSender()).getInfo().getName();
		ByteArrayInputStream bytestream = new ByteArrayInputStream(e.getData());
		DataInputStream datastream = new DataInputStream(bytestream);
		try {
			String method = datastream.readUTF();
			if (method.equalsIgnoreCase("ban")) {
				BanEntry.Builder entry = new BanEntry.Builder(datastream.readUTF());
				entry.server(datastream.readUTF())
					.source(datastream.readUTF())
					.reason(datastream.readUTF());
				BanManager.ban(entry.build());
				return;
			} else if(method.equalsIgnoreCase("banip")) {
				BanEntry.Builder entry = new BanEntry.Builder(datastream.readUTF())
										.server(datastream.readUTF())
										.source(datastream.readUTF())
										.reason(datastream.readUTF());
				BanManager.ban(entry.build());
			} else if (method.equalsIgnoreCase("gban")) {
				BanEntry.Builder entry = new BanEntry.Builder(datastream.readUTF())
										.global()
										.source(datastream.readUTF())
										.reason(datastream.readUTF());
				BanManager.ban(entry.build());
			} else if (method.equalsIgnoreCase("gbanip")) {
				BanEntry.Builder entry = new BanEntry.Builder(datastream.readUTF())
										.global()
										.source(datastream.readUTF())
										.reason(datastream.readUTF());
				BanManager.ban(entry.build());
			} else if (method.equalsIgnoreCase("gtempban")) {
				BanEntry.Builder entry = new BanEntry.Builder(datastream.readUTF())
										.global()
										.source(datastream.readUTF())
										.reason(datastream.readUTF())
										.expiry(dateFormat.parse(datastream.readUTF()));
				BanManager.ban(entry.build());
			} else if (method.equalsIgnoreCase("gtempbanip")) {
				BanEntry.Builder entry = new BanEntry.Builder(datastream.readUTF())
										.global()
										.source(datastream.readUTF())
										.reason(datastream.readUTF())
										.expiry(dateFormat.parse(datastream.readUTF()));
				BanManager.ban(entry.build());
			} else if (method.equalsIgnoreCase("tempban")) {
				BanEntry.Builder entry = new BanEntry.Builder(datastream.readUTF())
										.server(datastream.readUTF())
										.source(datastream.readUTF())
										.reason(datastream.readUTF())
										.expiry(dateFormat.parse(datastream.readUTF()));
				BanManager.ban(entry.build());
			} else if (method.equalsIgnoreCase("tempbanip")) {
				BanEntry.Builder entry = new BanEntry.Builder(datastream.readUTF())
										.server(datastream.readUTF())
										.source(datastream.readUTF())
										.reason(datastream.readUTF())
										.expiry(dateFormat.parse(datastream.readUTF()));
				BanManager.ban(entry.build());
			} else if (method.equalsIgnoreCase("unban") || method.equalsIgnoreCase("unbanip")) {
				BanManager.unban(datastream.readUTF(), datastream.readUTF());
			} else if (method.equalsIgnoreCase("gunban") || method.equalsIgnoreCase("gunbanip")) {
				BanManager.gunban(datastream.readUTF());
			} else {
				ProxyServer.getInstance().getLogger().warning("[BungeeBan] PluginMessage error");
			}
		} catch(IOException ex) {
			
		} catch(ParseException ex) {
			ProxyServer.getInstance().getLogger().warning("[BungeeBan] PluginMessage error : Invalid date format");
		}
		return;
	}
}

