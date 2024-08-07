package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Random;

public class BlockGlass extends BlockBreakable
{
	private static final String __OBFID = "CL_00000249";

	public BlockGlass(Material p_i45408_1_, boolean p_i45408_2_)
	{
		super("glass", p_i45408_1_, p_i45408_2_);
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

	public int quantityDropped(Random p_149745_1_)
	{
		return 0;
	}

	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass()
	{
		return 0;
	}

	public boolean renderAsNormalBlock()
	{
		return false;
	}

	protected boolean canSilkHarvest()
	{
		return true;
	}
}