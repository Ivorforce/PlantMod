package net.ivorius.plantmod;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemSoillessPlant extends Item
{
	/**
	 * The type of block this seed turns into (wheat or pumpkin stems for
	 * instance)
	 */
	private int blockType;

	/** BlockID of the block the seeds can be planted on. */
	private int soilBlockID;

	public ItemSoillessPlant(int par1, int par2, int par3)
	{
		super(par1);
		this.blockType = par2;
		this.soilBlockID = par3;
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	/**
	 * Callback for item usage. If the item does something special on right
	 * clicking, he will have one of those. Return True if something happen and
	 * false if it don't. This is for ITEMS, not BLOCKS
	 */
	public boolean onItemUse( ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10 )
	{
		if (par7 != 1)
		{
			return false;
		}
		else if (par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par2EntityPlayer.canPlayerEdit(par4, par5 + 1, par6, par7, par1ItemStack))
		{
			int i1 = par3World.getBlockId(par4, par5, par6);
			Block soil = Block.blocksList[i1];

			if (soil != null && soil.blockID == soilBlockID && par3World.isAirBlock(par4, par5 + 1, par6))
			{
				par3World.setBlock(par4, par5 + 1, par6, this.blockType);
				
				if (par1ItemStack.hasTagCompound())
				{
					TileEntity tileEntity = par3World.getBlockTileEntity(par4, par5 + 1, par6);
					
					if (tileEntity != null && tileEntity instanceof TileEntityDetectionPlant)
					{
						TileEntityDetectionPlant tileEntityDetectionPlant = (TileEntityDetectionPlant)tileEntity;
						float red = par1ItemStack.getTagCompound().getFloat("plantRed");
						float green = par1ItemStack.getTagCompound().getFloat("plantGreen");
						float blue = par1ItemStack.getTagCompound().getFloat("plantBlue");
						float alpha = par1ItemStack.getTagCompound().getFloat("plantAlpha");
						tileEntityDetectionPlant.setPlantColor(red, green, blue, alpha);
					}
				}
				
				--par1ItemStack.stackSize;
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	public static void setColors(ItemStack itemstack, float red, float green, float blue, float alpha)
	{
		itemstack.setTagInfo("plantRed", new NBTTagFloat("plantRed", red));
		itemstack.setTagInfo("plantGreen", new NBTTagFloat("plantGreen", green));
		itemstack.setTagInfo("plantBlue", new NBTTagFloat("plantBlue", blue));
		itemstack.setTagInfo("plantAlpha", new NBTTagFloat("plantAlpha", alpha));
	}
}
