package net.ivorius.plantmod;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityDetectionPlant extends TileEntity
{
	public static ArrayList<DetectionEntry> colors;

	public int ticksAlive;
	public int ticksAliveVisual;

	public float red;
	public float green;
	public float blue;
	public float alpha;

	public DetectionEntry destEntry;

	public static void setUp()
	{
		colors = new ArrayList<TileEntityDetectionPlant.DetectionEntry>();
		colors.add(new DetectionEntry(Block.oreCoal.blockID, 0.216f, 0.216f, 0.216f));
		colors.add(new DetectionEntry(Block.oreIron.blockID, 0.831f, 0.675f, 0.565f));
		colors.add(new DetectionEntry(Block.oreRedstone.blockID, 1, 0.145f, 0));
		colors.add(new DetectionEntry(Block.oreRedstoneGlowing.blockID, 1, 0.145f, 0));
		colors.add(new DetectionEntry(Block.oreLapis.blockID, 0.11f, 0.325f, 0.659f));
		colors.add(new DetectionEntry(Block.oreNetherQuartz.blockID, 0.906f, 0.882f, 0.847f));
		colors.add(new DetectionEntry(Block.oreGold.blockID, 0.988f, 0.933f, 0.294f));
		colors.add(new DetectionEntry(Block.oreEmerald.blockID, 0.09f, 0.867f, 0.384f));
		colors.add(new DetectionEntry(Block.oreDiamond.blockID, 0.365f, 0.925f, 0.961f));
	}

	public TileEntityDetectionPlant()
	{
		ticksAlive = 0;
		ticksAliveVisual = new Random().nextInt(1000);

		red = 1.0f;
		green = 1.0f;
		blue = 1.0f;
		alpha = 0.3f;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();

		if (destEntry != null)
		{
			alpha = changeColorSafe(alpha, 1.0f, 0.00005f);
			
			red = changeColorSafe(red, destEntry.red, (1.0f - alpha * 0.8f) * 0.0001f);
			green = changeColorSafe(green, destEntry.green, (1.0f - alpha * 0.8f) * 0.0001f);
			blue = changeColorSafe(blue, destEntry.blue, (1.0f - alpha * 0.8f) * 0.0001f);
		}
		else
		{
			alpha = changeColorSafe(alpha, 0.0f, 0.00005f);
		}

		if ((ticksAlive % 40) == 0)
			updateDestColorEntry();
		
		ticksAlive ++;
		ticksAliveVisual ++;
	}

	private float changeColorSafe(float src, float dest, float speed)
	{
		return src + (dest - src) * speed * 500.0f;
	}
	
	public void updateDestColorEntry()
	{
		this.destEntry = getBestEntry(xCoord, yCoord - 1, zCoord);
	}

	public DetectionEntry getBestEntry( int x, int y, int z )
	{
		DetectionEntry bestEntry = null;
		int bestEntryIndex = -1;

		for (; y >= 0; y--)
		{
			int blockID = worldObj.getBlockId(x, y, z);

			for (int i = colors.size() - 1; i > bestEntryIndex; i--)
			{
				if (colors.get(i).blockID == blockID)
				{
					bestEntryIndex = i;
					bestEntry = colors.get(i);
				}
			}
		}

		return bestEntry;
	}

	@Override
	public void readFromNBT( NBTTagCompound par1nbtTagCompound )
	{
		super.readFromNBT(par1nbtTagCompound);

		ticksAlive = par1nbtTagCompound.getInteger("ticksAlive");

		red = par1nbtTagCompound.getFloat("flowerRed");
		green = par1nbtTagCompound.getFloat("flowerGreen");
		blue = par1nbtTagCompound.getFloat("flowerBlue");
		alpha = par1nbtTagCompound.getFloat("flowerAlpha");

		if (par1nbtTagCompound.hasKey("destEntry"))
		{
			NBTTagCompound compound = par1nbtTagCompound.getCompoundTag("destEntry");
			destEntry = new DetectionEntry(compound);
		}
	}

	@Override
	public void writeToNBT( NBTTagCompound par1nbtTagCompound )
	{
		super.writeToNBT(par1nbtTagCompound);

		par1nbtTagCompound.setInteger("ticksAlive", ticksAlive);

		par1nbtTagCompound.setFloat("flowerRed", red);
		par1nbtTagCompound.setFloat("flowerGreen", green);
		par1nbtTagCompound.setFloat("flowerBlue", blue);
		par1nbtTagCompound.setFloat("flowerAlpha", alpha);

		if (destEntry != null)
		{
			NBTTagCompound compound = new NBTTagCompound();
			destEntry.writeToNBT(compound);
			par1nbtTagCompound.setCompoundTag("destEntry", compound);
		}
	}

	@Override
	public void onDataPacket( INetworkManager net, Packet132TileEntityData pkt )
	{
		readFromNBT(pkt.data);
	}
	
	public void setPlantColor(float red, float green, float blue, float alpha)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		return new Packet132TileEntityData(xCoord, yCoord, zCoord, 200, nbttagcompound);
	}

	public static class DetectionEntry
	{
		public int blockID;

		public float red;
		public float green;
		public float blue;

		public DetectionEntry(NBTTagCompound par1nbtTagCompound)
		{
			this.readFromNBT(par1nbtTagCompound);
		}

		public DetectionEntry(int blockID, float red, float green, float blue)
		{
			this.blockID = blockID;
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		public void readFromNBT( NBTTagCompound par1nbtTagCompound )
		{
			blockID = par1nbtTagCompound.getInteger("blockID");
			red = par1nbtTagCompound.getFloat("red");
			green = par1nbtTagCompound.getFloat("green");
			blue = par1nbtTagCompound.getFloat("blue");
		}

		public void writeToNBT( NBTTagCompound par1nbtTagCompound )
		{
			par1nbtTagCompound.setInteger("blockID", blockID);
			par1nbtTagCompound.setFloat("red", red);
			par1nbtTagCompound.setFloat("green", green);
			par1nbtTagCompound.setFloat("blue", blue);
		}
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox()
	{
		return super.getRenderBoundingBox().expand(0.0, 4.0, 0.0).getOffsetBoundingBox(0.0, -4.0, 0.0);
	}
}
