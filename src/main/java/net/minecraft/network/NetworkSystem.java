package net.minecraft.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.network.NetHandlerHandshakeMemory;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

public class NetworkSystem
{
	private static final Logger logger = LogManager.getLogger();
	private static final NioEventLoopGroup eventLoops = new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty IO #%d").setDaemon(true).build());
	private final MinecraftServer mcServer;
	public volatile boolean isAlive;
	private final List endpoints = Collections.synchronizedList(new ArrayList());
	private final List networkManagers = Collections.synchronizedList(new ArrayList());
	private static final String __OBFID = "CL_00001447";

	public NetworkSystem(MinecraftServer p_i45292_1_)
	{
		this.mcServer = p_i45292_1_;
		this.isAlive = true;
	}

	public void addLanEndpoint(InetAddress p_151265_1_, int p_151265_2_) throws IOException
	{
		List list = this.endpoints;

		synchronized (this.endpoints)
		{
			this.endpoints.add(((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(NioServerSocketChannel.class)).childHandler(new ChannelInitializer()
			{
				private static final String __OBFID = "CL_00001448";
				protected void initChannel(Channel p_initChannel_1_)
				{
					try
					{
						p_initChannel_1_.config().setOption(ChannelOption.IP_TOS, Integer.valueOf(24));
					}
					catch (ChannelException channelexception1)
					{
						;
					}

					try
					{
						p_initChannel_1_.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(false));
					}
					catch (ChannelException channelexception)
					{
						;
					}

					p_initChannel_1_.pipeline().addLast("timeout", new ReadTimeoutHandler(FMLNetworkHandler.READ_TIMEOUT)).addLast("legacy_query", new PingResponseHandler(NetworkSystem.this)).addLast("splitter", new MessageDeserializer2()).addLast("decoder", new MessageDeserializer(NetworkManager.field_152462_h)).addLast("prepender", new MessageSerializer2()).addLast("encoder", new MessageSerializer(NetworkManager.field_152462_h));
					NetworkManager networkmanager = new NetworkManager(false);
					NetworkSystem.this.networkManagers.add(networkmanager);
					p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
					networkmanager.setNetHandler(new NetHandlerHandshakeTCP(NetworkSystem.this.mcServer, networkmanager));
				}
			}).group(eventLoops).localAddress(p_151265_1_, p_151265_2_)).bind().syncUninterruptibly());
		}
	}

	@SideOnly(Side.CLIENT)
	public SocketAddress addLocalEndpoint()
	{
		List list = this.endpoints;
		ChannelFuture channelfuture;

		synchronized (this.endpoints)
		{
			channelfuture = ((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(LocalServerChannel.class)).childHandler(new ChannelInitializer()
			{
				private static final String __OBFID = "CL_00001449";
				protected void initChannel(Channel p_initChannel_1_)
				{
					NetworkManager networkmanager = new NetworkManager(false);
					networkmanager.setNetHandler(new NetHandlerHandshakeMemory(NetworkSystem.this.mcServer, networkmanager));
					NetworkSystem.this.networkManagers.add(networkmanager);
					p_initChannel_1_.pipeline().addLast("packet_handler", networkmanager);
				}
			}).group(eventLoops).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
			this.endpoints.add(channelfuture);
		}

		return channelfuture.channel().localAddress();
	}

	public void terminateEndpoints()
	{
		this.isAlive = false;
		Iterator iterator = this.endpoints.iterator();

		while (iterator.hasNext())
		{
			ChannelFuture channelfuture = (ChannelFuture)iterator.next();
			channelfuture.channel().close().syncUninterruptibly();
		}
	}

	public void networkTick()
	{
		List list = this.networkManagers;

		synchronized (this.networkManagers)
		{
			Iterator iterator = this.networkManagers.iterator();

			while (iterator.hasNext())
			{
				final NetworkManager networkmanager = (NetworkManager)iterator.next();

				if (!networkmanager.isChannelOpen())
				{
					iterator.remove();

					if (networkmanager.getExitMessage() != null)
					{
						networkmanager.getNetHandler().onDisconnect(networkmanager.getExitMessage());
					}
					else if (networkmanager.getNetHandler() != null)
					{
						networkmanager.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
					}
				}
				else
				{
					try
					{
						networkmanager.processReceivedPackets();
					}
					catch (Exception exception)
					{
						if (networkmanager.isLocalChannel())
						{
							CrashReport crashreport = CrashReport.makeCrashReport(exception, "Ticking memory connection");
							CrashReportCategory crashreportcategory = crashreport.makeCategory("Ticking connection");
							crashreportcategory.addCrashSectionCallable("Connection", new Callable()
							{
								private static final String __OBFID = "CL_00001450";
								public String call()
								{
									return networkmanager.toString();
								}
							});
							throw new ReportedException(crashreport);
						}

						logger.warn("Failed to handle packet for " + networkmanager.getSocketAddress(), exception);
						final ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");
						networkmanager.scheduleOutboundPacket(new S40PacketDisconnect(chatcomponenttext), new GenericFutureListener[] {new GenericFutureListener()
						{
							private static final String __OBFID = "CL_00001451";
							public void operationComplete(Future p_operationComplete_1_)
							{
								networkmanager.closeChannel(chatcomponenttext);
							}
						}
																									  });
						networkmanager.disableAutoRead();
					}
				}
			}
		}
	}

	public MinecraftServer func_151267_d()
	{
		return this.mcServer;
	}
}