package fr.lambertz.robin.bungeeban.banstore;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import net.md_5.bungee.api.ProxyServer;

import fr.lambertz.robin.bungeeban.BungeeBan;

public class FileBanStore implements IBanStore {
	private List<BanEntry> playerBanned;
	private List<BanEntry> ipBanned;
	private File fileplayer = new File(BungeeBan.configdir, "banned-players.txt");
	private File fileip = new File(BungeeBan.configdir, "banned-ips.txt");
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	
	public FileBanStore() {
		playerBanned = new ArrayList<BanEntry>();
		ipBanned = new ArrayList<BanEntry>();
		
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
				playerBanned.add(entryFromFile(strentry));
			}
			s.close();
			
			s = new Scanner(fileip);
			while (s.hasNext()) {
				String strentry = s.next();
				ipBanned.add(entryFromFile(strentry));
			}
			s.close();
			
		} catch (Exception e) {
			ProxyServer.getInstance().getLogger().severe("[BungeeBan] Could not load config file. Please send me the following stacktrace :");
			e.printStackTrace();
			return;
		}

	}

	@Override
	public boolean isBanned(String player, String server) {
		removeExpired();
		for (BanEntry entry : playerBanned) {
			boolean entrysrv = entry.getServer().equalsIgnoreCase(server) ||
							 entry.getServer().equalsIgnoreCase("(GLOBAL)");
			
			if (entry.getBanned().equalsIgnoreCase(player) && entrysrv) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isGBanned(String player) {
		removeExpired();
		for (BanEntry entry : playerBanned) {			
			if (entry.getBanned().equalsIgnoreCase(player) 
				&& entry.getServer().equalsIgnoreCase("(GLOBAL)")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void ban(String banned, String server, String banner, String reason) {
		BanEntry newban = new BanEntry(banned);
		newban.setServer(server);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
			
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);

		playerBanned.add(newban);
		save();
	}
	
	@Override
	public void gban(String banned, String banner, String reason) {
		BanEntry newban = new BanEntry(banned);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);
		
		playerBanned.add(newban);
		save();
	}

	@Override
	public void tempban(String banned, String server, String banner, String reason, Date until) {
		BanEntry newban = new BanEntry(banned);
		newban.setServer(server);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
		
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);
		
		newban.setExpiry(until);
		
		playerBanned.add(newban);
		save();
	}
	
	@Override
	public void gtempban(String banned, String banner, String reason, Date until) {
		BanEntry newban = new BanEntry(banned);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
		
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);
		
		newban.setExpiry(until);
		
		playerBanned.add(newban);
		save();
	}

	@Override
	public void unban(String player) {
		playerBanned.remove(player);
		save();
	}
	
	@Override
	public boolean isIPBanned(String address, String server) {
		removeExpired();
		for (BanEntry entry : ipBanned) {
			boolean entrysrv = entry.getServer().equalsIgnoreCase(server) ||
							 entry.getServer().equalsIgnoreCase("(GLOBAL)");
			
			if (entry.getBanned().equalsIgnoreCase(address) && entrysrv) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isGIPBanned(String address) {
		removeExpired();
		for (BanEntry entry : ipBanned) {			
			if (entry.getBanned().equalsIgnoreCase(address) 
				&& entry.getServer().equalsIgnoreCase("(GLOBAL)")) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void banIP(String address, String server, String banner, String reason) {
		BanEntry newban = new BanEntry(address);
		newban.setServer(server);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
			
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);

		ipBanned.add(newban);
		save();
	}
	
	@Override
	public void gbanIP(String address, String banner, String reason) {
		BanEntry newban = new BanEntry(address);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
			
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);

		ipBanned.add(newban);
		save();
	}
	
	@Override
	public void tempbanIP(String address, String server, String banner, String reason, Date until) {
		BanEntry newban = new BanEntry(address);
		newban.setServer(server);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
		
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);
		
		newban.setExpiry(until);
		
		ipBanned.add(newban);
		save();
	}
	
	@Override
	public void gtempbanIP(String address, String banner, String reason, Date until) {
		BanEntry newban = new BanEntry(address);
		if (reason != null && !reason.isEmpty())
			newban.setReason(reason);
		
		if (banner != null && !banner.isEmpty())
			newban.setSource(banner);
		
		newban.setExpiry(until);
		
		ipBanned.add(newban);
		save();
	}

	@Override
	public void unbanIP(String address) {
		ipBanned.remove(address);
		save();
	}

	@Override
	public List<BanEntry> getBanList() {
		return playerBanned;
	}

	@Override
	public List<BanEntry> getIPBanList() {
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
	    if (astring.length == 5)
	    	return banentry;
	    
	    banentry.setServer(astring[5].trim());
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
        stringbuilder.append("|");
        stringbuilder.append(entry.getServer());
        return stringbuilder.toString();
	}
}
