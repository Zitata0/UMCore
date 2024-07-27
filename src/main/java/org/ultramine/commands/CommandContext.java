package org.ultramine.commands;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ultramine.core.economy.service.Economy;
import org.ultramine.core.economy.account.PlayerAccount;
import org.ultramine.core.service.InjectService;
import org.ultramine.server.data.ServerDataLoader;
import org.ultramine.server.data.player.PlayerData;
import org.ultramine.server.util.BasicTypeFormatter;
import org.ultramine.server.util.BasicTypeParser;
import org.ultramine.server.util.GlobalExecutors;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionException;

public class CommandContext
{
	@InjectService private static Economy economy;
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

	/* thread-safe */
	public void handleException(@Nullable Throwable throwable)
	{
		if(throwable == null)
			return;
		if(throwable instanceof CompletionException && throwable.getCause() != null)
			throwable = throwable.getCause();
		if(throwable instanceof WrongUsageException)
		{
			CommandException cmdEx = (CommandException) throwable;
			ChatComponentTranslation msg = new ChatComponentTranslation("commands.generic.usage", new ChatComponentTranslation(cmdEx.getMessage(), cmdEx.getErrorOjbects()));
			msg.getChatStyle().setColor(EnumChatFormatting.RED);
			sendMessage(msg);
		}
		else if(throwable instanceof CommandException)
		{
			CommandException cmdEx = (CommandException) throwable;
			ChatComponentTranslation msg = new ChatComponentTranslation(cmdEx.getMessage(), cmdEx.getErrorOjbects());
			msg.getChatStyle().setColor(EnumChatFormatting.RED);
			sendMessage(msg);
		}
		else
		{
			ChatComponentTranslation msg = new ChatComponentTranslation("commands.generic.exception");
			msg.getChatStyle().setColor(EnumChatFormatting.RED);
			sendMessage(msg);
			log.error("Couldn\'t process command", throwable);
		}
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
