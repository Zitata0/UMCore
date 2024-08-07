package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;

/**
 * This event is called when an item is rendered in an item frame.
 * 
 * You can set canceled to do no further vanilla processing.
 */
@Cancelable
public class RenderItemInFrameEvent extends Event
{
	public final ItemStack item;
	public final EntityItemFrame entityItemFrame;
	public final RenderItemFrame renderer;
	
	public RenderItemInFrameEvent(EntityItemFrame itemFrame, RenderItemFrame renderItemFrame)
	{
		item = itemFrame.getDisplayedItem();
		entityItemFrame = itemFrame;
		renderer = renderItemFrame;
	}
}
