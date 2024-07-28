package org.ultramine.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.server.data.ServerDataLoader;
import org.ultramine.server.util.BasicTypeFormatter;
import org.ultramine.server.util.GlobalExecutors;

import java.util.HashMap;
import java.util.Map;

public class CommandContext
{
	private static final Logger log = LogManager.getLogger();
	private ICommandSender sender;
	private String[] args;
	private IExtendedCommand command;
	private Map<String, Argument> argumentMap;
	private int lastArgumentNum;
	private String actionName;

	private CommandContext(IExtendedCommand command, ICommandSender sender, String[] args)
	{
		this.sender = sender;
		this.args = args;
		this.command = command;
		this.argumentMap = new HashMap<String, Argument>(args.length);
		this.actionName = "";
		this.lastArgumentNum = args.length - 1;
	}

	public Argument get(String key)
	{
		if (!argumentMap.containsKey(key))
			throwBadUsage();

		return argumentMap.get(key);
	}

	public Argument get(int num)
	{
		if (num < 0 || num >= args.length)
			throwBadUsage();

		return new Argument(num);
	}

	public boolean contains(String key)
	{
		return argumentMap.containsKey(key);
	}

	public Argument set(String key, String value)
	{
		Argument arg = new Argument(value);
		argumentMap.put(key, arg);
		return arg;
	}

	public String getAction()
	{
		return actionName;
	}

	public boolean actionIs(String action)
	{
		return actionName.equalsIgnoreCase(action);
	}

	public EntityPlayerMP getSenderAsPlayer()
	{
		return CommandBase.getCommandSenderAsPlayer(sender);
	}

	/* thread-safe */
	public void sendMessage(IChatComponent comp)
	{
		if(Thread.currentThread() == getServer().getServerThread())
			sender.addChatMessage(comp);
		else
			GlobalExecutors.nextTick().execute(() -> sender.addChatMessage(comp));
	}

	public void sendMessage(EnumChatFormatting tplColor, EnumChatFormatting argsColor, String msg, Object... args)
	{
		sendMessage(BasicTypeFormatter.formatMessage(tplColor, argsColor, msg, args));
	}
	
	public void sendMessage(EnumChatFormatting argsColor, String msg, Object... args)
	{
		sendMessage(EnumChatFormatting.GOLD, argsColor, msg, args);
	}
	
	public void sendMessage(String msg, Object... args)
	{
		sendMessage(EnumChatFormatting.YELLOW, msg, args);
	}

	public void throwBadUsage()
	{
		throw new WrongUsageException(command.getCommandUsage(sender));
	}
	
	public void failure(String msg)
	{
		throw new CommandException(msg);
	}
	
	public void check(boolean flag, String msg)
	{
		if(!flag)
			throw new CommandException(msg);
	}
	
	public MinecraftServer getServer()
	{
		return MinecraftServer.getServer();
	}
	
	public ServerDataLoader getServerData()
	{
		return getServer().getConfigurationManager().getDataLoader();
	}

	public class Argument
	{
		private int num;
		private boolean last;
		private String value;

		private Argument(int num)
		{
			this.value = args[num];
			this.num = num;
			this.last = num == lastArgumentNum;
		}

		private Argument(String value)
		{
			this.value = value;
			this.num = -1;
			this.last = false;
		}

		public int asInt()
		{
			return CommandBase.parseInt(sender, value);
		}

		public int asInt(int minBound)
		{
			return CommandBase.parseIntWithMin(sender, value, minBound);
		}
		
		public WorldServer asWorld()
		{
			WorldServer world = MinecraftServer.getServer().getMultiWorld().getWorldByNameOrID(value);
			if(world == null)
				throw new CommandException("command.generic.world.invalid", value);
			return world;
		}
	}
}
