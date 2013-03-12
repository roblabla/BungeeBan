package net.craftminecraft.bungee.bungeeban.banstore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.craftminecraft.bungee.bungeeban.util.MainConfig;

import lib.PatPeter.SQLibrary.MySQL;

public class MySQLBanStore implements IBanStore {
	private MySQL connection;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    private Logger logger;
    
	public MySQLBanStore(Logger logger, MainConfig config) {
		this.logger = logger;
		connection = new MySQL(logger, "[BungeeBan]", 
							   config.database_address, 
							   config.database_port, 
							   config.database_name,
							   config.database_username,
							   config.database_password);
		if (!connection.open()) {
			return;
		}
		if (!(connection.isTable("bungeeban_playerbans"))) {
			try {
				connection.query("CREATE TABLE bungeeban_playerbans (id INT PRIMARY KEY AUTO_INCREMENT, banned VARCHAR(16)," +
													"source VARCHAR(50), created VARCHAR(50)," +
													"expiry VARCHAR(50), reason VARCHAR(150)," +
													"server VARCHAR(50))");
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Failed to create database table. No bans will be stored", e);
				return;
			}
		} if (!(connection.isTable("bungeeban_ipbans"))) {
			try {
				connection.query("CREATE TABLE bungeeban_ipbans (id INT PRIMARY KEY AUTO_INCREMENT, banned VARCHAR(16)," +
													"source VARCHAR(50), created VARCHAR(50)," +
													"expiry VARCHAR(50), reason VARCHAR(150)," +
													"server VARCHAR(50))");
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Failed to create database table. No bans will be stored", e);
				return;
			}
		}
	}
	
	@Override
	public boolean ban(BanEntry entry) {
		try {
			PreparedStatement stmt = connection.prepare("INSERT INTO bungeeban_" + 
														(entry.isIPBan() ? "ipbans" : "playerbans") +
														"(banned,server,created,source,reason,expiry) " +
														"VALUES (?,?,?,?,?,?)");
			stmt.setString(1,entry.getBanned());
			stmt.setString(2,entry.getServer());
			stmt.setString(3,dateFormat.format(entry.getCreated()));
			stmt.setString(4,entry.getSource());
			stmt.setString(5,entry.getReason());
			if (entry.getExpiry() == null)
				stmt.setNull(6, Types.VARCHAR);
			else
				stmt.setString(6, dateFormat.format(entry.getExpiry()));

			connection.query(stmt);
			return true;
		} catch (SQLException e) {
			logger.severe("Ban failed, " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean unban(String player, String server) {
		PreparedStatement stmt;
		try {
			stmt = connection.prepare("DELETE FROM bungeeban_playerbans WHERE banned = ? AND server = ?");
			stmt.setString(1, player);
			stmt.setString(2, server);
			connection.query(stmt);
			return true;
		} catch (SQLException e) {
			logger.severe("Unban failed, " + e.getMessage());
			return false;
		}	
	}

	@Override
	public boolean gunban(String player) {
		PreparedStatement stmt;
		try {
			stmt = connection.prepare("DELETE FROM bungeeban_playerbans WHERE banned = ? " +
																	    "AND server = '(GLOBAL)'");
			stmt.setString(1, player);
			connection.query(stmt);
			return true;
		} catch (SQLException e) {
			logger.severe("Global Unban failed, " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean unbanIP(String ip, String server) {
		PreparedStatement stmt;
		try {
			stmt = connection.prepare("DELETE FROM bungeeban_ipbans WHERE player = ? AND server = ?");
			stmt.setString(1, ip);
			stmt.setString(2, server);
			connection.query(stmt);
			return true;
		} catch (SQLException e) {
			logger.severe("UnbanIP failed, " + e.getMessage());
			return false;
		}
	}

	@Override
	public boolean gunbanIP(String ip) {
		PreparedStatement stmt;
		try {
			stmt = connection.prepare("DELETE FROM bungeeban_ipbans WHERE player = ? AND server = '(GLOBAL)'");
			stmt.setString(1, ip);
			connection.query(stmt);
			return true;
		} catch (SQLException e) {
			logger.severe("Global UnbanIP failed, " + e.getMessage());
			return false;
		}
	}

	@Override
	public List<BanEntry> getBanList() {
		try {
			ResultSet rs = connection.query("SELECT * FROM bungeeban_playerbans");
			List<BanEntry> entries = new ArrayList<BanEntry>();
			if (rs.first()) {
				do {
					try {
						BanEntry.Builder entry = new BanEntry.Builder(rs.getString("banned"))
							.created(dateFormat.parse(rs.getString("created")))
							.server(rs.getString("server"))
							.reason(rs.getString("reason"))
							.source(rs.getString("source"));
						if (rs.getNString("expiry") != null) {
							entry.expiry(dateFormat.parse(rs.getNString("expiry")));
						}
						entries.add(entry.build());
					} catch (ParseException e) {
						logger.severe("Invalid date format for entry " + rs.getString("banned") +
							":" + rs.getString("server"));
						continue;
					}
				} while (rs.next());
			}
			return entries;
		} catch (SQLException e) {
			logger.severe("getBanList failed, " + e.getMessage());
			return null;
		}
	}

	@Override
	public List<BanEntry> getIPBanList() {
		try {
			ResultSet rs = connection.query("SELECT * FROM bungeeban_ipbans");
			List<BanEntry> entries = new ArrayList<BanEntry>();
			if (rs.first()) {
				do {
					try {
						BanEntry.Builder entry = new BanEntry.Builder(rs.getString("banned"))
							.created(dateFormat.parse(rs.getString("created")))
							.server(rs.getString("server"))
							.reason(rs.getString("reason"))
							.source(rs.getString("source"));
						if (rs.getNString("expiry") != null) {
							entry.expiry(dateFormat.parse(rs.getNString("expiry")));
						}
						entry.ipban();
						entries.add(entry.build());
					} catch (ParseException e) {
						logger.severe("Invalid date format for entry " + rs.getString("banned") +
							":" + rs.getString("server"));
						continue;
					}
				} while (rs.next());
			}
			return entries;
		} catch (SQLException e) {
			logger.severe("getBanList failed, " + e.getMessage());
			return null;
		}
	}

	@Override
	public void reloadBanList() {
		return;
	}

}
