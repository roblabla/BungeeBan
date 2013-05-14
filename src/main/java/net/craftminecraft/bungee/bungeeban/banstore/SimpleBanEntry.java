package net.craftminecraft.bungee.bungeeban.banstore;

import java.util.Date;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.util.MainConfig;
import net.craftminecraft.bungee.bungeeban.util.Utils;
import net.md_5.bungee.api.ProxyServer;

/**
 * Ban Representation Object
 * At it's base level, this object simply contains all the info a ban could have, and the default values.
 * @author roblabla
 */
public class SimpleBanEntry implements BanEntry {
    private final String banned;
    private final Date created;
    private final String source;
	private final Date expiry;
    private final String reason;
    private final String server;
    private final boolean ipban;

    private SimpleBanEntry(Builder builder) {
		this.banned = builder.banned;
		this.created = builder.created;
		this.source = builder.source;
		this.expiry = builder.expiry;
		this.reason = builder.reason;
		this.server = builder.server;
		this.ipban = builder.ipban;
	}

    public static class Builder {
        private String banned = null;
        private String server = null;
        private Date created = new Date();
        private String source = "(Unknown)";
    	private Date expiry = null;
        private String reason = null;
        private boolean ipban = false;
        
        public Builder() {}
        
        public Builder(String banned) {
        	this.banned = banned;
        }
        
        
        public Builder(String banned, String server) {
        	if (banned == null || banned.isEmpty()) {
        		throw new IllegalArgumentException("Banned player cannot be null");
        	}
        	this.banned = banned;
        	if (ProxyServer.getInstance().getServers().get(server) == null) {
        		throw new IllegalArgumentException("Server does not exist");
        	}
        	this.server = server;
        }
        
        public Builder banned(String banned) {
        	this.banned = banned;
        	return this;
        }
        
        public Builder server(String server) {
        	this.server = server;
        	return this;
        }
        
        public Builder global() {
        	this.server = "(GLOBAL)";
        	return this;
        }
        
    	/**
    	 * Sets the date at which this ban was made.
    	 * @param created
    	 */
    	public Builder created(Date created) {
    		this.created = created;
    		return this;
    	}
    	
    	
    	public Builder source(String source) {
    		this.source = source;
    		return this;
    	}

    	public Builder expiry() {
    		this.expiry = Utils.parseDate(MainConfig.getInstance().defaults_tempbantime);
    		return this;
    	}
    	
    	public Builder expiry(boolean def) {
    		if (def) {
    			expiry();
    		} else {
    			this.expiry = null;
    		}
    		return this;
    	}
    	
    	public Builder expiry(Date expiry) {
    		this.expiry = expiry;
    		return this;
    	}

    	public Builder reason(String reason) {
    		this.reason = reason;
    		return this;
    	}
    	
    	public Builder ipban() {
    		this.ipban = true;
    		return this;
    	}
    	
    	public Builder ipban(boolean ipban) {
    		this.ipban = ipban;
    		return this;
    	}
    	
    	public SimpleBanEntry build() {
    		return build(false);
    	}
    	
    	public SimpleBanEntry build(boolean loading) {
    		if (banned == null || banned.isEmpty()) {
    			throw new IllegalArgumentException("Username cannot be null or empty");
    		}
    		if (server == null || (!server.equalsIgnoreCase("(GLOBAL)") && ProxyServer.getInstance().getServerInfo(server) == null)) {
    			throw new IllegalArgumentException("Server " + server + " does not exist!");
    		}
    		if (created == null) {
    			throw new IllegalArgumentException("Created cannot be null");
    		}
    		if (source == null || source.isEmpty()) {
    			source = "(Unknown)";
    		}
    		if (!MainConfig.getInstance().defaults_reasonExtend && reason == null) {
    			String type = "ban";
    			type = ((expiry != null) ? "temp" : "") + type;
    			type = ((server.equalsIgnoreCase("(GLOBAL)")) ? "g" : "") + type;
    			reason = MainConfig.getInstance().getReasonByType(type);
    		} else if (MainConfig.getInstance().defaults_reasonExtend && !loading) {
    			if (reason == null) {
    				reason = "";
    			}
    			String type = "ban";
    			type = ((expiry != null) ? "temp" : "") + type;
    			type = ((server.equalsIgnoreCase("(GLOBAL)")) ? "g" : "") + type;
    			reason = MainConfig.getInstance().getReasonByType(type).replaceAll("%reason%", reason);
    		}

    		if (ipban) {
    			if (ProxyServer.getInstance().getPlayer(banned) != null) {
    				banned = ProxyServer.getInstance().getPlayer(banned).getAddress().getAddress().getHostAddress();
    			} else if (!BanManager.isIP(banned)) {
    				throw new IllegalArgumentException("Not a legal ip, and no user with this name is online");
    			}
    		} else {
    			banned = banned.toLowerCase();
    		}
    		return new SimpleBanEntry(this);
    	}
    }
 
    @Override
	public boolean equals(Object obj) {
    	boolean result = false;
    	if (obj instanceof BanEntry) {
    		BanEntry otherBan = (BanEntry) obj;
    		if (banned.equalsIgnoreCase(otherBan.getBanned()) && server.equalsIgnoreCase(otherBan.getServer())) {
    			result = true;
    		}
    	}
    	return result;
    }
    /**
     * @return The banned player representing this entry
     */
    public String getBanned() {
		return banned;
	}

    /**
     * @return When was this ban entry created
     */
    public Date getCreated() {
		return created;
	}

	/**
	 * @return The name of the player who banned the bannee in this entry.
	 */
	public String getSource() {
		return source;
	}

	public Date getExpiry() {
		return expiry;
	}
	
	public boolean hasExpired() {
		return this.expiry == null ? false : new Date().after(this.expiry);
	}
	
	public boolean isTempBan() {
		return this.expiry != null;
	}
	
	public String getReason() {
		return reason;
	}

	public String getServer() {
		return server;
	}
	
	public boolean isGlobal() {
		return server.equalsIgnoreCase("(GLOBAL)");
	}
	
	public boolean isIPBan() {
		return this.ipban;
	}
	
	@Override
	public Builder clone() {
		return new Builder(banned)
					.server(server)
					.created(created)
					.source(source)
					.expiry(expiry)
					.reason(reason)
					.ipban(ipban);
	}
}