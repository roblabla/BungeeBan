package net.craftminecraft.bungee.bungeeban.tests;

import static org.mockito.Matchers.anyString;
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

import net.craftminecraft.bungee.bungeeban.command.BanCommand;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

@RunWith(PowerMockRunner.class)
public class CommandTest {

	@Test
	public void testBanCommand() {
		ProxiedPlayer mockedBanned = mock(ProxiedPlayer.class);
		Server mockedServer = mock(Server.class);
		ProxyServer mockedProxy = mock(ProxyServer.class);
		Collection<ProxiedPlayer> playerlist = new HashSet<ProxiedPlayer>();
		playerlist.add(mockedBanned);
		
		when(mockedProxy.getPlayers()).thenReturn(playerlist);
		ServerInfo info = new ServerInfo("testserver", new InetSocketAddress(25577)) {
			
			@Override
			public void sendData(String channel, byte[] data) {
				return;
			}
			@Override
			public void ping(Callback<ServerPing> callback) {
				return;
			}
		};
		when(mockedBanned.getName()).thenReturn("ImATroll");
		when(mockedBanned.getServer()).thenReturn(mockedServer);
		when(mockedServer.getInfo()).thenReturn(info);
		
		Command bancomm = new BanCommand();
		
	}
	
}
