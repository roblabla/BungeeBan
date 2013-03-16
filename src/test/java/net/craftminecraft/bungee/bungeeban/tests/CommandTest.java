package net.craftminecraft.bungee.bungeeban.tests;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import net.craftminecraft.bungee.bungeeban.BanManager;
import net.craftminecraft.bungee.bungeeban.banstore.BanEntry;
import net.craftminecraft.bungee.bungeeban.command.BanCommand;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BanManager.class) // BanManager.class contains static methods
public class CommandTest {

	@SuppressWarnings("serial")
	@Test
	public void testBanCommand() {
		PowerMockito.mockStatic(BanManager.class);
		when(BanManager.ban(any(BanEntry.class))).thenReturn(true);
		
		ProxiedPlayer mockedBanned = mock(ProxiedPlayer.class);
		when(mockedBanned.getName()).thenReturn("ImATroll");

		ProxiedPlayer mockedSender = mock(ProxiedPlayer.class);
		when(mockedSender.getName()).thenReturn("ImAnAdmin");
		when(mockedSender.hasPermission(anyString())).thenReturn(true);
		
		Collection<ProxiedPlayer> playerlist = new HashSet<ProxiedPlayer>();
		playerlist.add(mockedBanned);
		
		ServerInfo info = mock(ServerInfo.class);
		when(info.getName()).thenReturn("testserver");
		when(info.getPlayers()).thenReturn(playerlist);

		ProxyServer mockedProxy = mock(ProxyServer.class);
		when(mockedProxy.getServerInfo(anyString())).thenReturn(info);
		when(mockedProxy.getPlayers()).thenReturn(playerlist);
		ProxyServer.setInstance(mockedProxy);

		Command bancomm = new BanCommand();
		String[] args = new String[5];
		args[0] = "imatroll";
		args[1] = "testserver";
		args[2] = "You";
		args[3] = "annoy";
		args[4] = "me";
		bancomm.execute(mockedSender, args);
		verify(mockedSender).sendMessage(eq(ChatColor.RED + "imatroll has been banned."));
	}
	
}
