package com.craftminecraft.bungee.bungeeban.listener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.craftminecraft.bungee.bungeeban.BanManager;
import com.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import com.google.common.eventbus.Subscribe;

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
		String servername = ((Server)e.getSender()).getInfo().getName();
		ByteArrayInputStream bytestream = new ByteArrayInputStream(e.getData());
		DataInputStream datastream = new DataInputStream(bytestream);
		try {
			String method = datastream.readUTF();
			if (method.equalsIgnoreCase("ban")) {
				BanEntry entry = new BanEntry(datastream.readUTF());
				entry.setServer(datastream.readUTF())
					.setSource(datastream.readUTF())
					.setReason(datastream.readUTF());
				BanManager.ban(entry);
				return;
			} else if(method.equalsIgnoreCase("banip")) {
				BanEntry entry = new BanEntry(datastream.readUTF())
										.setServer(datastream.readUTF())
										.setSource(datastream.readUTF())
										.setReason(datastream.readUTF());
				BanManager.ban(entry);
			} else if (method.equalsIgnoreCase("gban")) {
				BanEntry entry = new BanEntry(datastream.readUTF())
										.setGlobal()
										.setSource(datastream.readUTF())
										.setReason(datastream.readUTF());
				BanManager.ban(entry);
			} else if (method.equalsIgnoreCase("gbanip")) {
				BanEntry entry = new BanEntry(datastream.readUTF())
										.setGlobal()
										.setSource(datastream.readUTF())
										.setReason(datastream.readUTF());
				BanManager.ban(entry);
			} else if (method.equalsIgnoreCase("gtempban")) {
				BanEntry entry = new BanEntry(datastream.readUTF())
										.setGlobal()
										.setSource(datastream.readUTF())
										.setReason(datastream.readUTF())
										.setExpiry(dateFormat.parse(datastream.readUTF()));
				BanManager.ban(entry);
			} else if (method.equalsIgnoreCase("gtempbanip")) {
				BanEntry entry = new BanEntry(datastream.readUTF())
										.setGlobal()
										.setSource(datastream.readUTF())
										.setReason(datastream.readUTF())
										.setExpiry(dateFormat.parse(datastream.readUTF()));
				BanManager.ban(entry);
			} else if (method.equalsIgnoreCase("tempban")) {
				BanEntry entry = new BanEntry(datastream.readUTF())
										.setServer(datastream.readUTF())
										.setSource(datastream.readUTF())
										.setReason(datastream.readUTF())
										.setExpiry(dateFormat.parse(datastream.readUTF()));
				BanManager.ban(entry);
			} else if (method.equalsIgnoreCase("tempbanip")) {
				BanEntry entry = new BanEntry(datastream.readUTF())
										.setServer(datastream.readUTF())
										.setSource(datastream.readUTF())
										.setReason(datastream.readUTF())
										.setExpiry(dateFormat.parse(datastream.readUTF()));
				BanManager.ban(entry);
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

