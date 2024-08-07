package net.minecraft.server.dedicated;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@SideOnly(Side.SERVER)
public class PropertyManager
{
	private static final Logger field_164440_a = LogManager.getLogger();
	private final Properties serverProperties = new Properties();
	private final File serverPropertiesFile;
	private static final String __OBFID = "CL_00001782";

	public PropertyManager(File p_i45278_1_)
	{
		this.serverPropertiesFile = p_i45278_1_;

		if (p_i45278_1_.exists())
		{
			FileInputStream fileinputstream = null;

			try
			{
				fileinputstream = new FileInputStream(p_i45278_1_);
				this.serverProperties.load(fileinputstream);
			}
			catch (Exception exception)
			{
				field_164440_a.warn("Failed to load " + p_i45278_1_, exception);
				this.generateNewProperties();
			}
			finally
			{
				if (fileinputstream != null)
				{
					try
					{
						fileinputstream.close();
					}
					catch (IOException ioexception)
					{
						;
					}
				}
			}
		}
		else
		{
			field_164440_a.warn(p_i45278_1_ + " does not exist");
			this.generateNewProperties();
		}
	}

	public void generateNewProperties()
	{
		field_164440_a.info("Generating new properties file");
		this.saveProperties();
	}

	public void saveProperties()
	{
		FileOutputStream fileoutputstream = null;

		try
		{
			fileoutputstream = new FileOutputStream(this.serverPropertiesFile);
			this.serverProperties.store(fileoutputstream, "Minecraft server properties");
		}
		catch (Exception exception)
		{
			field_164440_a.warn("Failed to save " + this.serverPropertiesFile, exception);
			this.generateNewProperties();
		}
		finally
		{
			if (fileoutputstream != null)
			{
				try
				{
					fileoutputstream.close();
				}
				catch (IOException ioexception)
				{
					;
				}
			}
		}
	}

	public File getPropertiesFile()
	{
		return this.serverPropertiesFile;
	}

	public String getStringProperty(String p_73671_1_, String p_73671_2_)
	{
		if (!this.serverProperties.containsKey(p_73671_1_))
		{
			this.serverProperties.setProperty(p_73671_1_, p_73671_2_);
			this.saveProperties();
			this.saveProperties();
		}

		return this.serverProperties.getProperty(p_73671_1_, p_73671_2_);
	}

	public int getIntProperty(String p_73669_1_, int p_73669_2_)
	{
		try
		{
			return Integer.parseInt(this.getStringProperty(p_73669_1_, "" + p_73669_2_));
		}
		catch (Exception exception)
		{
			this.serverProperties.setProperty(p_73669_1_, "" + p_73669_2_);
			this.saveProperties();
			return p_73669_2_;
		}
	}

	public boolean getBooleanProperty(String p_73670_1_, boolean p_73670_2_)
	{
		try
		{
			return Boolean.parseBoolean(this.getStringProperty(p_73670_1_, "" + p_73670_2_));
		}
		catch (Exception exception)
		{
			this.serverProperties.setProperty(p_73670_1_, "" + p_73670_2_);
			this.saveProperties();
			return p_73670_2_;
		}
	}

	public void setProperty(String p_73667_1_, Object p_73667_2_)
	{
		this.serverProperties.setProperty(p_73667_1_, "" + p_73667_2_);
	}
}