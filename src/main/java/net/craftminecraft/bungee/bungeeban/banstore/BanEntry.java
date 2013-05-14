package net.craftminecraft.bungee.bungeeban.banstore;

import java.util.Date;

public interface BanEntry {
	public String getBanned();
    public Date getCreated();
	public String getSource();
	public Date getExpiry();
	public boolean hasExpired();
	public boolean isTempBan();
	public String getReason();
	public String getServer();
	public boolean isGlobal();
	public boolean isIPBan();
}
