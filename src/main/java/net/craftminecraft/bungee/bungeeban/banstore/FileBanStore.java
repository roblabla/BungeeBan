package net.craftminecraft.bungee.bungeeban.banstore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


import net.craftminecraft.bungee.bungeeban.BungeeBan;
import net.md_5.bungee.api.ProxyServer;


public class FileBanStore implements IBanStore {
	private List<BanEntry> playerBanned;
	private List<BanEntry> ipBanned;
	private File fileplayer = new File("plugins" + File.separator + "BungeeBan", "banned-players.txt");
	private File fileip = new File("plugins" + File.separator + "BungeeBan", "banned-ips.txt");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	
	public FileBanStore() {
		playerBanned = new ArrayList<BanEntry>();
		ipBanned = new ArrayList<BanEntry>();
		reloadBanList();
	}
	
	@Override
	public boolean ban(BanEntry entry) {
		if (entry.isIPBan())
			ipBanned.add(entry);
		else
			playerBanned.add(entry);
		save();
		return true;
	}

	@Override
	public boolean unban(String player, String server) {
		BanEntry entry = new BanEntry.Builder(player).server(server).build();
		if (playerBanned.contains(entry)) {
			playerBanned.remove(entry);
			save();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean gunban(String player) {
		BanEntry entry = new BanEntry.Builder(player).global().build();
		if (playerBanned.contains(entry)) {
			playerBanned.remove(entry);
			save();
			return true;
		}
		return false;
	}

	@Override
	public boolean unbanIP(String address, String server) {
		BanEntry entry = new BanEntry.Builder(address).server(server).ipban().build();
		if (ipBanned.contains(entry)) {
			ipBanned.remove(entry);
			save();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean gunbanIP(String address) {
		BanEntry entry = new BanEntry.Builder(address).global().ipban().build();
		if (ipBanned.contains(entry)) {
			ipBanned.remove(entry);
			save();
			return true;
		}
		return false;	
	}
	
	@Override
	public List<BanEntry> getBanList() {
		removeExpired();
		return playerBanned;
	}

	@Override
	public List<BanEntry> getIPBanList() {
		removeExpired();
		return ipBanned;
	}
	
	private void save() {
        try {
        	//save player bans
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.fileplayer, false));

            for (BanEntry entry : playerBanned) {
                printwriter.println(entryToString(entry));
            }
            printwriter.close();
            
            //save ip bans
            printwriter = new PrintWriter(new FileWriter(this.fileip, false));

            for (BanEntry entry : ipBanned) {
                printwriter.println(entryToString(entry));
            }
            printwriter.close();
        } catch (IOException ioexception) {
            ProxyServer.getInstance().getLogger().severe("Could not save ban list");
        }
	}
	
	private void removeExpired() {
        Iterator<BanEntry> iterator = this.playerBanned.iterator();

        while (iterator.hasNext()) {
            BanEntry banentry = iterator.next();

            if (banentry.hasExpired()) {
                iterator.remove();
            }
        }
	}

	private BanEntry entryFromFile(String line) {
		String[] astring = line.trim().split(Pattern.quote("|"));
		BanEntry.Builder banentry = new BanEntry.Builder(astring[0].trim());
	
		// Support old-style banlist, one username per line.
	    if (astring.length == 1)
	        return banentry.build();
	
	    try {
	   		banentry.created(dateFormat.parse(astring[1].trim()));
	   	} catch (ParseException parseexception) {
	   		ProxyServer.getInstance().getLogger().severe("[BungeeBan] Could not read creation date format for ban entry '" + astring[0].trim() + "'.");
	    }
	   	if (astring.length == 2) 
	    	return banentry.build();
	
	    banentry.source(astring[2].trim());
	    if (astring.length == 3)
	    	return banentry.build();
	
	    try {
	    	String expiry = astring[3].trim();
	    	if (!expiry.equalsIgnoreCase("Forever") && expiry.length() > 0)
	    		banentry.expiry(dateFormat.parse(expiry));
	    
	    } catch (ParseException parseexception1) {
	    	System.out.println("Could not read expiry date format for ban entry '" + astring[0].trim() + "'");
	    }
	    if (astring.length == 4)
	    	return banentry.build();
	    
	    banentry.reason(astring[4].trim());
	    if (astring.length == 5)
	    	return banentry.build();
	    
	    banentry.server(astring[5].trim());
	    return banentry.build();
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
				playerBanned.add(entryFromFile(strentry));
			}
			s.close();
			
			s = new Scanner(fileip);
			while (s.hasNext()) {
				String strentry = s.nextLine();
				ipBanned.add(entryFromFile(strentry));
			}
			s.close();
			
		} catch (Exception e) {
			ProxyServer.getInstance().getLogger().severe("[BungeeBan] Could not load config file. Please send me the following stacktrace :");
			e.printStackTrace();
			return;
		}

	}
}
