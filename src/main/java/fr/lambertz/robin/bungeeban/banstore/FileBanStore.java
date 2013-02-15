package fr.lambertz.robin.bungeeban.banstore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import net.md_5.bungee.api.ProxyServer;

import fr.lambertz.robin.bungeeban.BungeeBan;
import fr.lambertz.robin.bungeeban.util.InsensitiveStringMap;

public class FileBanStore implements IBanStore {
	private InsensitiveStringMap playerBanned;
	private InsensitiveStringMap ipBanned;
	private File fileplayer = new File(BungeeBan.configdir, "banned-players.txt");
	private File fileip = new File(BungeeBan.configdir, "banned-ips.txt");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	
	public FileBanStore() {
		playerBanned = new InsensitiveStringMap();
		ipBanned = new InsensitiveStringMap();
		
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
				String strentry = s.next();
				playerBanned.put(strentry.split("|")[0].trim(), entryFromFile(strentry));
			}
			s.close();
			
			s = new Scanner(fileip);
			while (s.hasNext()) {
				String strentry = s.next();
				ipBanned.put(strentry.split("|")[0].trim(), entryFromFile(strentry));
			}
			s.close();
			
		} catch (Exception e) {
			ProxyServer.getInstance().getLogger().severe("[BungeeBan] Could not load config file. Please send me the following stacktrace :");
			e.printStackTrace();
			return;
		}

	}

	@Override
	public boolean isBanned(String player) {
		this.removeExpired();
		return playerBanned.containsKey(player);
	}

	@Override
	public void ban(String banned, String banner, String reason) {
		BanEntry newban = new BanEntry(banned);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
			
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);

		playerBanned.put(newban.getBanned(),newban);
		save();
	}

	@Override
	public void tempban(String banned, String banner, String reason, Date until) {
		BanEntry newban = new BanEntry(banned);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
		
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);
		
		newban.setExpiry(until);
		
		playerBanned.put(newban.getBanned(),newban);
		save();
	}

	@Override
	public void unban(String player) {
		playerBanned.remove(player);
		save();
	}

	@Override
	public boolean isIPBanned(String address) {
		this.removeExpired();
		return ipBanned.containsKey(address);
	}

	@Override
	public void banIP(String address, String banner, String reason) {
		BanEntry newban = new BanEntry(address);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
			
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);

		ipBanned.put(newban.getBanned(),newban);
		save();
	}
	
	@Override
	public void tempbanIP(String address, String banner, String reason, Date until) {
		BanEntry newban = new BanEntry(address);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
		
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);
		
		newban.setExpiry(until);
		
		ipBanned.put(newban.getBanned(),newban);
		save();
	}
	
	@Override
	public void unbanIP(String address) {
		ipBanned.remove(address);
		save();
	}

	@Override
	public Map getBanList() {
		return playerBanned;
	}

	@Override
	public Map getIPBanList() {
		return ipBanned;
	}
	
	private void save() {
        try {
        	//save player bans
            PrintWriter printwriter = new PrintWriter(new FileWriter(this.fileplayer, false));
            Iterator iterator = this.playerBanned.values().iterator();

            while (iterator.hasNext()) {
                BanEntry banentry = (BanEntry) iterator.next();
                printwriter.println(entryToString(banentry));
            }
            printwriter.close();
            
            //save ip bans
            printwriter = new PrintWriter(new FileWriter(this.fileip, false));
            iterator = this.ipBanned.values().iterator();

            while (iterator.hasNext()) {
                BanEntry banentry = (BanEntry) iterator.next();
                printwriter.println(entryToString(banentry));
            }
            printwriter.close();
        } catch (IOException ioexception) {
            ProxyServer.getInstance().getLogger().severe("Could not save ban list");
        }
	}
	
	private void removeExpired() {
        Iterator iterator = this.playerBanned.values().iterator();

        while (iterator.hasNext()) {
            BanEntry banentry = (BanEntry) iterator.next();

            if (banentry.hasExpired()) {
                iterator.remove();
            }
        }
	}

	private BanEntry entryFromFile(String line) {
		String[] astring = line.trim().split("|");
		BanEntry banentry = new BanEntry(astring[0].trim());
	
		// Support old-style banlist, one username per line.
	    if (astring.length == 1)
	        return banentry;
	
	    try {
	   		banentry.setCreated(dateFormat.parse(astring[1].trim()));
	   	} catch (ParseException parseexception) {
	   		ProxyServer.getInstance().getLogger().severe("[BungeeBan] Could not read creation date format for ban entry '" + banentry.getBanned() + "'.");
	    }
	   	if (astring.length == 2) 
	    	return banentry;
	
	    banentry.setSource(astring[2].trim());
	    if (astring.length == 3)
	    	return banentry;
	
	    try {
	    	String expiry = astring[3].trim();
	    	if (!expiry.equalsIgnoreCase("Forever") && expiry.length() > 0)
	    		banentry.setExpiry(dateFormat.parse(expiry));
	    
	    } catch (ParseException parseexception1) {
	    	System.out.println("Could not read expiry date format for ban entry '" + banentry.getBanned() + "'");
	    }
	    if (astring.length == 4)
	    	return banentry;
	    
	    banentry.setReason(astring[4].trim());
	    return banentry;
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
        return stringbuilder.toString();
	}
}
