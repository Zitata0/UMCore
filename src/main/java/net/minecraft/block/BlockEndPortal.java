package net.minecraft.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;
import java.util.Random;

public class BlockEndPortal extends BlockContainer
{
	public static boolean field_149948_a;
	private static final String __OBFID = "CL_00000236";

	protected BlockEndPortal(Material p_i45404_1_)
	{
		super(p_i45404_1_);
		this.setLightLevel(1.0F);
	}

	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
	{
		return new TileEntityEndPortal();
	}

	public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_, int p_149719_4_)
	{
		float f = 0.0625F;
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
	{
		return p_149646_5_ != 0 ? false : super.shouldSideBeRendered(p_149646_1_, p_149646_2_, p_149646_3_, p_149646_4_, p_149646_5_);
	}

	public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_, AxisAlignedBB p_149743_5_, List p_149743_6_, Entity p_149743_7_) {}

	public boolean isOpaqueCube()
	{
		return false;
	}

	public boolean renderAsNormalBlock()
	{
		return false;
	}

	public int quantityDropped(Random p_149745_1_)
	{
		return 0;
	}

	public void onEntityCollidedWithBlock(World p_149670_1_, int p_149670_2_, int p_149670_3_, int p_149670_4_, Entity p_149670_5_)
	{
		if (p_149670_5_.ridingEntity == null && p_149670_5_.riddenByEntity == null && !p_149670_1_.isRemote)
		{
			int dim = ((WorldServer)p_149670_1_).getConfig().portals.enderLink;
			if(dim != Integer.MIN_VALUE)
				p_149670_5_.travelToDimension(((WorldServer)p_149670_1_).getConfig().portals.enderLink);
		}
	}

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World p_149734_1_, int p_149734_2_, int p_149734_3_, int p_149734_4_, Random p_149734_5_)
	{
		double d0 = (double)((float)p_149734_2_ + p_149734_5_.nextFloat());
		double d1 = (double)((float)p_149734_3_ + 0.8F);
		double d2 = (double)((float)p_149734_4_ + p_149734_5_.nextFloat());
		double d3 = 0.0D;
		double d4 = 0.0D;
		double d5 = 0.0D;
		p_149734_1_.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
	}

	public int getRenderType()
	{
		return -1;
	}

	public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
	{
		if (!field_149948_a && !p_149726_1_.isRemote)
		{
			int endlink = ((WorldServer)p_149726_1_).getConfig().portals.enderLink;
			if(endlink == Integer.MIN_VALUE || endlink == p_149726_1_.provider.dimensionId)
			{
				p_149726_1_.setBlockToAir(p_149726_2_, p_149726_3_, p_149726_4_);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
	{
		return Item.getItemById(0);
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister p_149651_1_)
	{
		this.blockIcon = p_149651_1_.registerIcon("portal");
	}

	public MapColor getMapColor(int p_149728_1_)
	{
		return MapColor.obsidianColor;
	}
}