package net.craftminecraft.bungee.bungeeban.banstore;

import java.util.Date;

/**
 * Ban Representation Object
 * At it's base level, this object simply contains all the info a ban could have, and the default values.
 * @author roblabla
 */
public class BanEntry {
    private final String banned;
    private Date created = new Date();
    private String source = "(Unknown)";
	private Date expiry = null;
    private String reason = "Banned by an operator.";
    private String server = "(GLOBAL)";

    public BanEntry(String banned) {
		this.banned = banned;
	}

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
	 * Sets the date at which this ban was made.
	 * @param created
	 */
	public BanEntry setCreated(Date created) {
		this.created = created;
		return this;
	}

	/**
	 * @return The name of the player who banned the bannee in this entry.
	 */
	public String getSource() {
		return source;
	}

	public BanEntry setSource(String source) {
		this.source = source;
		return this;
	}

	public Date getExpiry() {
		return expiry;
	}

	public BanEntry setExpiry(Date expiry) {
		this.expiry = expiry;
		return this;
	}

	public boolean hasExpired() {
		return this.expiry == null ? false : new Date().after(this.expiry);
	}
	
	public String getReason() {
		return reason;
	}

	public BanEntry setReason(String reason) {
		this.reason = reason;
		return this;
	}
	
	public String getServer() {
		return server;
	}
	
	public boolean isGlobal() {
		return server.equalsIgnoreCase("(GLOBAL)");
	}
	
	public BanEntry setServer(String server) {
		this.server = server;
		return this;
	}
	
	public BanEntry setGlobal() {
		this.server = "(GLOBAL)";
		return this;
	}
}