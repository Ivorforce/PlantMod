package net.ivorius.plantmod;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockInstablePlant extends BlockContainer
{
	public BlockInstablePlant(int par1)
	{
		super(par1, Material.plants);

		setBlockBounds(0.05f, 0.0f, 0.05f, 0.9f, 0.95f, 0.9f);
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
	
	@Override
	public void onEntityCollidedWithBlock( World par1World, int par2, int par3, int par4, Entity par5Entity )
	{
		super.onEntityCollidedWithBlock(par1World, par2, par3, par4, par5Entity);

		if (par5Entity instanceof EntityItem)
		{
			EntityItem entityItem = (EntityItem) par5Entity;
			TileEntity tileEntity = par1World.getBlockTileEntity(par2, par3, par4);

			if (tileEntity != null && tileEntity instanceof TileEntityInstablePlant)
			{
				if (((TileEntityInstablePlant) tileEntity).tryEating(entityItem.getEntityItem()))
				{
					if (!par1World.isRemote)
						par5Entity.setDead();
				}
			}
		}
		else
		{
			TileEntity tileEntity = par1World.getBlockTileEntity(par2, par3, par4);

			if (tileEntity != null && tileEntity instanceof TileEntityInstablePlant)
			{
				((TileEntityInstablePlant) tileEntity).onCollision(par5Entity);
			}
		}
	}
	
	@Override
	public void breakBlock( World par1World, int par2, int par3, int par4, int par5, int par6 )
	{
		TileEntity tileEntity = par1World.getBlockTileEntity(par2, par3, par4);

		if (tileEntity != null && tileEntity instanceof TileEntityInstablePlant)
		{
			((TileEntityInstablePlant) tileEntity).onBlockDestroyed();
		}

		super.breakBlock(par1World, par2, par3, par4, par5, par6);
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
            this.dropBlockAsItem(par1World, par2, par3, par4, par1World.getBlockMetadata(par2, par3, par4), 0);
            par1World.setBlock(par2, par3, par4, 0, 0, 2);
        }
    }
	@Override
	public boolean canBlockStay( World par1World, int par2, int par3, int par4 )
	{
        int soilID = par1World.getBlockId(par2, par3 - 1, par4);
        return soilID == Block.tilledField.blockID;
	}
	
	@Override
	public TileEntity createNewTileEntity( World world )
	{
		return new TileEntityInstablePlant();
	}
}
