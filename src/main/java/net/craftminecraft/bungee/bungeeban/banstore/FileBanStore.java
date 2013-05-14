package net.craftminecraft.bungee.bungeeban.banstore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


import net.craftminecraft.bungee.bungeeban.BungeeBan;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;


public class FileBanStore implements IBanStore {
	private Table<String, String, BanEntry> playerBanned;
	private Table<String, String, BanEntry> ipBanned;
	private File fileplayer = new File("plugins" + File.separator + "BungeeBan", "banned-players.txt");
	private File fileip = new File("plugins" + File.separator + "BungeeBan", "banned-ips.txt");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private BungeeBan plugin;
	
	public FileBanStore(BungeeBan plugin) {
		this.plugin = plugin;
		playerBanned = HashBasedTable.create();
		ipBanned = HashBasedTable.create();
		reloadBanList();
	}
	
	@Override
	public boolean ban(BanEntry entry) {
		if (entry.isIPBan())
			ipBanned.put(entry.getBanned(), entry.getServer(), entry);
		else
			playerBanned.put(entry.getBanned(), entry.getServer(), entry);
		save();
		return true;
	}

	@Override
	public boolean unban(String player, String server) {
		if (playerBanned.contains(player, server)) {
			playerBanned.remove(player, server);
			save();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean gunban(String player) {
		return unban(player, "(GLOBAL)");
	}

	@Override
	public boolean unbanIP(String address, String server) {
		if (ipBanned.contains(address, server)) {
			ipBanned.remove(address, server);
			save();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean gunbanIP(String address) {
		return unbanIP(address, "(GLOBAL)");
	}
	
	@Override
	public Table<String, String, BanEntry> getBanList() {
		removeExpired();
		return playerBanned;
	}

	@Override
	public Table<String, String, BanEntry> getIPBanList() {
		removeExpired();
		return ipBanned;
	}
	
	@Override
	public BanEntry isBanned(String player, String server) {
		removeExpired();
		return playerBanned.get(player, server);
	}

	@Override
	public BanEntry isIPBanned(String ip, String server) {
		removeExpired();
		return ipBanned.get(ip, server);
	}
	
	private void save() {
        try {
        	//save player bans
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.fileplayer, false));

            for (Table.Cell<String, String, BanEntry> cell : playerBanned.cellSet()) {
                printwriter.println(entryToString(cell.getValue()));
            }
            printwriter.close();
            
            //save ip bans
            printwriter = new PrintWriter(new FileWriter(this.fileip, false));

            for (Table.Cell<String, String, BanEntry> cell : ipBanned.cellSet()) {
                printwriter.println(entryToString(cell.getValue()));
            }
            printwriter.close();
        } catch (IOException ioexception) {
            ProxyServer.getInstance().getLogger().severe("Could not save ban list");
        }
	}
	
	private void removeExpired() {
        Iterator<Table.Cell<String,String,BanEntry>> iterator = this.playerBanned.cellSet().iterator();

        while (iterator.hasNext()) {
            Table.Cell<String, String, BanEntry> cell = iterator.next();

            if (cell.getValue().hasExpired()) {
                iterator.remove();
            }
        }
        
        iterator = this.ipBanned.cellSet().iterator();

        while (iterator.hasNext()) {
            Table.Cell<String, String, BanEntry> cell = iterator.next();

            if (cell.getValue().hasExpired()) {
                iterator.remove();
            }
        }
	}

	private BanEntry entryFromFile(String line, boolean ipban) {
		String[] astring = line.trim().split(Pattern.quote("|"));
		SimpleBanEntry.Builder banentry = new SimpleBanEntry.Builder(astring[0].trim()).global().ipban(ipban);

		// Support old-style banlist, one username per line.
	    if (astring.length == 1)
	        return banentry.build(true);
	
	    try {
	   		banentry.created(dateFormat.parse(astring[1].trim()));
	   	} catch (ParseException parseexception) {
	   		ProxyServer.getInstance().getLogger().severe("[BungeeBan] Could not read creation date format for ban entry '" + astring[0].trim() + "'.");
	    }
	   	if (astring.length == 2) 
	    	return banentry.build(true);
	
	    banentry.source(astring[2].trim());
	    if (astring.length == 3)
	    	return banentry.build(true);
	
	    try {
	    	String expiry = astring[3].trim();
	    	if (!expiry.equalsIgnoreCase("Forever") && expiry.length() > 0)
	    		banentry.expiry(dateFormat.parse(expiry));
	    
	    } catch (ParseException parseexception1) {
	    	System.out.println("Could not read expiry date format for ban entry '" + astring[0].trim() + "'");
	    }
	    if (astring.length == 4)
	    	return banentry.build(true);
	    
	    banentry.reason(astring[4].trim());
	    if (astring.length == 5)
	    	return banentry.build(true);
	    
	    banentry.server(astring[5].trim());
	    
	    return banentry.build(true);
	}
	
	public String entryToString(BanEntry entry) {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(entry.getBanned());
        stringbuilder.append("|");
        stringbuilder.append(dateFormat.format(entry.getCreated()));
        stringbuilder.append("|");
        stringbuilder.append(entry.getSource());
        stringbuilder.append("|");
        stringbuilder.append(entry.getExpiry() == null ? "Forever" : dateFormat.format(entry.getExpiry()));
        stringbuilder.append("|");
        stringbuilder.append(entry.getReason());
        stringbuilder.append("|");
        stringbuilder.append(entry.getServer());
        return stringbuilder.toString();
	}

	@Override
	public void reloadBanList() {
		playerBanned.clear();
		ipBanned.clear();
		// Create the files and directories if they don't exist
		try {
			if ((!fileplayer.isFile() && !fileplayer.createNewFile()) || 
				(!fileip.isFile() 	  && !fileip.createNewFile())) {
				ProxyServer.getInstance().getLogger().severe("[BungeeBan] Error creating new file banned-ips.txt or banned-players.txt. Check your permissions.");
				return;
			}
			
			// Read the file and add the entries to the maps
			Scanner s = new Scanner(fileplayer);
			while (s.hasNext()) {
				String strentry = s.nextLine();
				BanEntry entry;
				try {
					entry = entryFromFile(strentry, false);
				} catch (IllegalArgumentException ex) {
					plugin.getLogger().log(Level.WARNING, "Malformed entry in player-bans.txt :\n " 
															+ strentry, ex);
					continue;
				}
				playerBanned.put(entry.getBanned(), entry.getServer(), entry);

			}
			s.close();
			
			s = new Scanner(fileip);
			while (s.hasNext()) {
				String strentry = s.nextLine();
				BanEntry entry;
				try {
					entry = entryFromFile(strentry, true);
				} catch (IllegalArgumentException ex) {
					plugin.getLogger().log(Level.WARNING, "Malformed entry in ip-bans.txt :\n " 
															+ strentry, ex);
					continue;
				}
				ipBanned.put(entry.getBanned(), entry.getServer(), entry);
			}
			s.close();
			
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Could not load banlist files.", e);
			return;
		}
	}
}
