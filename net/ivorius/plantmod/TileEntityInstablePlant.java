package net.ivorius.plantmod;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

public class TileEntityInstablePlant extends TileEntity
{
	public static ArrayList<ArrayList<ItemStack>> foods;

	public ItemStack wantedItem;
	public int wantedItemTime;

	public ArrayList<ItemStack> eatenItems;

	public int ticksAlive;
	public int ticksAliveVisual;

	public int timeStunnedLeft;
	public int timeAlreadyStunned;
	
	public float ratioEating;
	
	public static void setUp()
	{
		foods = new ArrayList<ArrayList<ItemStack>>();

		for (int i = 0; i < 5; i++)
			foods.add(new ArrayList<ItemStack>());

		foods.get(0).add(new ItemStack(Block.sapling));
		foods.get(0).add(new ItemStack(Block.plantRed));
		foods.get(0).add(new ItemStack(Block.plantYellow));
		foods.get(0).add(new ItemStack(Item.beefRaw));
		foods.get(0).add(new ItemStack(Block.wood));
		foods.get(1).add(new ItemStack(Item.rottenFlesh));
		foods.get(1).add(new ItemStack(Item.silk));
		foods.get(1).add(new ItemStack(Item.seeds));
		foods.get(1).add(new ItemStack(Item.wheat));
		foods.get(1).add(new ItemStack(Block.gravel));
		foods.get(1).add(new ItemStack(Item.egg));
		foods.get(1).add(new ItemStack(Block.cobblestone));
		foods.get(2).add(new ItemStack(Block.mushroomBrown));
		foods.get(2).add(new ItemStack(Block.mushroomRed));
		foods.get(2).add(new ItemStack(Item.ingotIron));
		foods.get(2).add(new ItemStack(Item.coal));
		foods.get(2).add(new ItemStack(Item.cake));
		foods.get(2).add(new ItemStack(Item.cookie));
		foods.get(2).add(new ItemStack(Item.carrot));
		foods.get(2).add(new ItemStack(Item.potato));
		foods.get(2).add(new ItemStack(Item.fishRaw));
		foods.get(3).add(new ItemStack(Item.dyePowder, 1, 4)); // Lapis
		foods.get(3).add(new ItemStack(Block.obsidian));
		foods.get(3).add(new ItemStack(Item.goldenCarrot));
		foods.get(3).add(new ItemStack(Item.ingotGold));
		foods.get(3).add(new ItemStack(Item.enderPearl));
		foods.get(3).add(new ItemStack(Item.blazeRod));
		foods.get(3).add(new ItemStack(Item.glowstone));
		foods.get(3).add(new ItemStack(Item.ghastTear));
		foods.get(3).add(new ItemStack(Item.netherStalkSeeds));
		foods.get(3).add(new ItemStack(Item.fireballCharge));
		foods.get(4).add(new ItemStack(Item.diamond));
		foods.get(4).add(new ItemStack(Item.emerald));
		foods.get(4).add(new ItemStack(Item.appleGold, 1, 0));
		foods.get(4).add(new ItemStack(Item.appleGold, 1, 1));
		foods.get(4).add(new ItemStack(Block.blockIron));
		foods.get(4).add(new ItemStack(Block.blockGold));
	}

	public TileEntityInstablePlant()
	{
		eatenItems = new ArrayList<ItemStack>();
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		ticksAlive++;
		ticksAliveVisual++;
		if (!isStunned())
			wantedItemTime++;

		if (timeStunnedLeft > 0)
			timeStunnedLeft--;

		if (wantedItem != null)
		{
			if (wantedItemTime > 20 * 60 * 15) // 15 minutes
			{
				float chance = (wantedItemTime - 20 * 60 * 10) * 0.00000005f;

				if (worldObj.rand.nextFloat() < chance)
					explode();
			}
		}

		if (!worldObj.isRemote && !isStunned() && wantedItem == null)
		{
			if (worldObj.rand.nextFloat() < 0.00000005f * wantedItemTime)
			{
				wantedItem = getRandomFood();
				wantedItemTime = 0;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}

		if (isStunned())
		{
			if (worldObj.isRemote)
			{
				if (worldObj.rand.nextInt(40) == 0)
					worldObj.spawnParticle("cloud", xCoord + worldObj.rand.nextFloat(), yCoord + worldObj.rand.nextFloat() * 0.4f + 0.8f, zCoord + worldObj.rand.nextFloat(), 0.0f, 0.1f, 0.0f);

				if (isHarvestable() && worldObj.rand.nextInt(20) == 0)
					worldObj.spawnParticle("smoke", xCoord + worldObj.rand.nextFloat(), yCoord + worldObj.rand.nextFloat() * 0.4f + 0.8f, zCoord + worldObj.rand.nextFloat(), 0.0f, 0.1f, 0.0f);
			}

			timeAlreadyStunned++;
		}
		else
			timeAlreadyStunned = 0;
		
		if (ratioEating > 0.0f)
		{
			ratioEating -= 0.01f;
			if (worldObj.isRemote)
			{
				for (int i = 0; i < MathHelper.ceiling_float_int(ratioEating * 5.0f); i++)
				{
					float xP =  + 0.5f + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2f;
					float zP =  + 0.5f + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2f;
					worldObj.spawnParticle("slime", xCoord + xP, yCoord, zCoord + zP, 0.0, 0.0, 0.0);					
				}
			}
		}
		
		if (ratioEating < 0.0f)
			ratioEating = 0.0f;

		if (!worldObj.isRemote && timeAlreadyStunned > 20 * 100)
			explode();
	}

	public boolean tryEating( ItemStack stack )
	{
		if (!worldObj.isRemote)
		{
			if (stack.itemID == Item.redstone.itemID)
			{
				timeStunnedLeft = 20 * 30;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			else if (ItemStack.areItemStacksEqual(stack, wantedItem))
			{
				eatenItems.add(wantedItem);
				wantedItem = null;
				wantedItemTime = 0;
				ratioEating = 1.0f;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			else
			{
				explode();
			}
		}

		return true;
	}

	public void onCollision( Entity entity )
	{
		if (!worldObj.isRemote)
		{
			double speedSQ = (entity.motionX * entity.motionX) + (entity.motionY * entity.motionY) + (entity.motionZ * entity.motionZ);
			float r = worldObj.rand.nextFloat();
			if (r * r < speedSQ * 10.0)
				explode();
		}
	}

	public void explode()
	{
		float strength = getRatioGrown() * 3.0f + 1.0f;
		this.worldObj.createExplosion(null, xCoord + 0.5f, yCoord + 0.5f, zCoord + 0.5f, strength, true);
	}

	public ItemStack getRandomFood()
	{
		return getRandomFood(getRatioGrown());
	}

	public ItemStack getRandomFood( float growthRatio )
	{
		int stage = MathHelper.floor_float(growthRatio * 5.0f);
		if (stage > 4)
			stage = 4;

		return getRandomFood(stage);
	}

	public ItemStack getRandomFood( int stage )
	{
		ArrayList<ItemStack> array = foods.get(stage);
		return array.get(worldObj.rand.nextInt(array.size())).copy();
	}

	public boolean isStunned()
	{
		return timeStunnedLeft > 0;
	}

	public void ejectAllEatenItems()
	{
		for (int i = 0; i < eatenItems.size(); i++)
		{
			int amount = worldObj.rand.nextInt(4);
			if (amount > 0)
			{
				EntityItem itemEntity = new EntityItem(worldObj);
				itemEntity.setEntityItemStack(eatenItems.get(i));
				itemEntity.getEntityItem().stackSize = amount;

				float seed = worldObj.rand.nextFloat();
				double xDir = MathHelper.sin(seed * 2.0f * 3.1415926f) * 0.5;
				double zDir = MathHelper.cos(seed * 2.0f * 3.1415926f) * 0.5;
				double yDir = 0.1f + worldObj.rand.nextFloat() * 0.2f;

				itemEntity.setPosition(xCoord + xDir + 0.5, yCoord + 0.1, zCoord + zDir + 0.5);
				itemEntity.motionX = xDir * 0.1;
				itemEntity.motionY = yDir;
				itemEntity.motionZ = zDir * 0.1;

				worldObj.spawnEntityInWorld(itemEntity);
			}
		}

		eatenItems.clear();
	}

	@Override
	public void writeToNBT( NBTTagCompound par1nbtTagCompound )
	{
		super.writeToNBT(par1nbtTagCompound);

		par1nbtTagCompound.setInteger("ticksAlive", ticksAlive);
		par1nbtTagCompound.setInteger("wantedItemTime", wantedItemTime);
		par1nbtTagCompound.setInteger("timeStunnedLeft", timeStunnedLeft);
		par1nbtTagCompound.setInteger("timeAlreadyStunned", timeAlreadyStunned);

		if (wantedItem != null)
		{
			NBTTagCompound itemCompound = new NBTTagCompound();
			wantedItem.writeToNBT(itemCompound);
			par1nbtTagCompound.setCompoundTag("wantedItem", itemCompound);
		}

		NBTTagList eatenItemList = new NBTTagList();
		for (int i = 0; i < eatenItems.size(); i++)
		{
			NBTTagCompound compound = new NBTTagCompound();
			eatenItems.get(i).writeToNBT(compound);
			eatenItemList.appendTag(compound);
		}
		par1nbtTagCompound.setTag("eatenItems", eatenItemList);
		
		par1nbtTagCompound.setFloat("ratioEating", ratioEating);
	}

	@Override
	public void readFromNBT( NBTTagCompound par1nbtTagCompound )
	{
		super.readFromNBT(par1nbtTagCompound);

		ticksAlive = par1nbtTagCompound.getInteger("ticksAlive");
		wantedItemTime = par1nbtTagCompound.getInteger("wantedItemTime");
		timeStunnedLeft = par1nbtTagCompound.getInteger("timeStunnedLeft");
		timeAlreadyStunned = par1nbtTagCompound.getInteger("timeAlreadyStunned");

		if (par1nbtTagCompound.hasKey("wantedItem"))
			wantedItem = ItemStack.loadItemStackFromNBT(par1nbtTagCompound.getCompoundTag("wantedItem"));
		else
			wantedItem = null;

		NBTTagList eatenItemList = par1nbtTagCompound.getTagList("eatenItems");
		eatenItems = new ArrayList<ItemStack>();
		for (int i = 0; i < eatenItemList.tagCount(); i++)
		{
			NBTTagCompound compound = (NBTTagCompound) eatenItemList.tagAt(i);
			eatenItems.add(ItemStack.loadItemStackFromNBT(compound));
		}
		
		ratioEating = par1nbtTagCompound.getFloat("ratioEating");
	}

	public float getRatioGrown()
	{
		return (float) ticksAlive / (20.0f * 60.0f * 120.0f); // 120 minutes = max
	}

	public boolean isHarvestable()
	{
		return isStunned() && timeAlreadyStunned > 20 * 60;
	}

	public void onBlockDestroyed()
	{
		if (!worldObj.isRemote)
		{
			if (isHarvestable())
			{
				ejectAllEatenItems();
			}
			else
			{
				explode();
			}
		}
	}

	@Override
	public void onDataPacket( INetworkManager net, Packet132TileEntityData pkt )
	{
		readFromNBT(pkt.data);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 200, nbttagcompound);
	}
}
