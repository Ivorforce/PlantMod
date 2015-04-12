package net.ivorius.plantmod;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockDetectionPlant extends BlockContainer
{
	public BlockDetectionPlant(int par1)
	{
		super(par1, Material.plants);
	}
	
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}
	
	@Override
	public int getRenderType()
	{
		return -1;
	}

    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5)
    {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        this.checkFlowerChange(par1World, par2, par3, par4);
    }

    protected final void checkFlowerChange(World par1World, int par2, int par3, int par4)
    {
        if (!this.canBlockStay(par1World, par2, par3, par4))
        {
            par1World.setBlock(par2, par3, par4, 0, 0, 2);
        }
    }
    
    @Override
    public int idDropped( int par1, Random par2Random, int par3 )
    {
    	return -1;
    }
    
	@Override
	public void breakBlock( World par1World, int par2, int par3, int par4, int par5, int par6 )
	{
		TileEntity tileEntity = par1World.getBlockTileEntity(par2, par3, par4);

		if (tileEntity != null && tileEntity instanceof TileEntityDetectionPlant)
		{
			TileEntityDetectionPlant tileEntityDetectionPlant = (TileEntityDetectionPlant)tileEntity;
			
			ItemStack droppedStack = new ItemStack(PlantMod.instance.itemDetectionPlant, 1);
			ItemSoillessPlant.setColors(droppedStack, tileEntityDetectionPlant.red, tileEntityDetectionPlant.green, tileEntityDetectionPlant.blue, tileEntityDetectionPlant.alpha);
			this.dropBlockAsItem_do(par1World, par2, par3, par4, droppedStack);
		}

		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}
	
	@Override
	public boolean canBlockStay( World par1World, int par2, int par3, int par4 )
	{
        int soilID = par1World.getBlockId(par2, par3 - 1, par4);
        return soilID == Block.grass.blockID && (par1World.getFullBlockLightValue(par2, par3, par4) >= 8 || par1World.canBlockSeeTheSky(par2, par3, par4));
	}
	
	@Override
	public TileEntity createNewTileEntity( World world )
	{
		return new TileEntityDetectionPlant();
	}
}
