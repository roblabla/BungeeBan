package fr.lambertz.robin.bungeeban.banstore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return The name of the player who banned the bannee in this entry.
	 */
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getExpiry() {
		return expiry;
	}

	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}

	public boolean hasExpired() {
		return this.expiry == null ? false : new Date().after(this.expiry);
	}
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public String getServer() {
		return server;
	}
	
	public void setServer(String server) {
		this.server = server;
	}
}