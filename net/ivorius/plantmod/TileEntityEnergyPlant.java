package net.ivorius.plantmod;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.asm.transformers.MarkerTransformer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class TileEntityEnergyPlant extends TileEntity
{
	public static float seedEnergy = 40.0f;
	public static float transformSpeed = 0.005f;

	public float energy;

	public float nightTransform;
	public boolean transformsToNight;

	public TileEntityEnergyPlant()
	{
		this.energy = 0.0f;
	}

	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if (energy > seedEnergy)
		{
			float chance = 1.0f - 50.0f / (50.0f + this.energy - seedEnergy);
			if (worldObj.rand.nextFloat() < chance)
			{
				this.spawnSeed();
			}
		}

		if (!worldObj.isRemote)
		{
			boolean was = transformsToNight;
			transformsToNight = !worldObj.isDaytime();
			
			if (was != transformsToNight)
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		if (!transformsToNight)
		{
			if (this.nightTransform > 0.0f)
				this.nightTransform -= this.nightTransform > transformSpeed ? transformSpeed : this.nightTransform;
		}
		else
		{
			if (this.nightTransform < 1.0f)
				this.nightTransform += (1.0f - this.nightTransform) > transformSpeed ? transformSpeed : (1.0f - this.nightTransform);
		}
		
		double rangeToAffect = 4.0;
		AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(xCoord - rangeToAffect, yCoord - rangeToAffect, zCoord - rangeToAffect, xCoord + rangeToAffect + 1, yCoord + rangeToAffect + 1, zCoord + rangeToAffect + 1);
		List<EntityLivingBase> entitiesToAffect = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, bb);
		
		for (EntityLivingBase hitEntity : entitiesToAffect)
		{
			double dist = hitEntity.getDistance(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
			float value = (float)(rangeToAffect - dist) * 0.1f;
			
			if (value > 0.0f)
			{
				Vec3 srcVec = worldObj.getWorldVec3Pool().getVecFromPool(hitEntity.posX, hitEntity.posY + hitEntity.getEyeHeight(), hitEntity.posZ);
				Vec3 destVec = worldObj.getWorldVec3Pool().getVecFromPool(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
				MovingObjectPosition result = worldObj.clip(srcVec, destVec);
				
				if (result == null || worldObj.getBlockId(result.blockX, result.blockY, result.blockZ) == getBlockType().blockID)
				{
					float effect = affectEnemy(hitEntity, value);

					if (worldObj.isRemote)
					{
						Random rand = worldObj.rand;
						
						int particles = rand.nextInt(MathHelper.ceiling_float_int(effect * 5.0f) + 1);
						for (int i = 0; i < particles; i++)
						{
							if (isDark())
							{
			                    double xDir = xCoord + 0.5 - hitEntity.posX;
			                    double yDir = yCoord + 0.5 - hitEntity.posY;
			                    double zDir = zCoord + 0.5 - hitEntity.posZ;

			                    worldObj.spawnParticle("enchantmenttable", hitEntity.posX + (rand.nextDouble() - 0.5) * hitEntity.width + xDir, hitEntity.posY + hitEntity.height * rand.nextDouble() + yDir - ((hitEntity instanceof EntityPlayer) ? 1.0 : 0.0), hitEntity.posZ + (rand.nextDouble() - 0.5) * hitEntity.width + zDir, -xDir, -yDir, -zDir);	
							}
							else
							{
								double distEff = rand.nextFloat();
								double xPos = (xCoord + 0.5) * distEff + (1.0 - distEff) * hitEntity.posX;
								double yPos = (yCoord + 0.5) * distEff + (1.0 - distEff) * (hitEntity.posY + hitEntity.height * rand.nextDouble() - ((hitEntity instanceof EntityPlayer) ? 1.0 : 0.0));
								double zPos = (zCoord + 0.5) * distEff + (1.0 - distEff) * hitEntity.posZ;
								
			                    worldObj.spawnParticle("reddust", xPos, yPos, zPos, 0.3f, 1.0f, 0.5f);	
							}
						}
					}				
				}	
			}
		}
		
		float nightTransformStrength = 0.5f - Math.abs(nightTransform - 0.5f);
		int transformParticles = MathHelper.ceiling_float_int(nightTransformStrength * nightTransformStrength * 30.0f);
		transformParticles = worldObj.rand.nextInt(transformParticles + 1);
		
		for (int i = 0; i < transformParticles; i++)
		{
			worldObj.spawnParticle("cloud", xCoord + worldObj.rand.nextFloat() * 2.0 - 0.5, yCoord + worldObj.rand.nextFloat() * 0.6f, zCoord + worldObj.rand.nextFloat() * 2.0 - 0.5, 0.0f, 0.1f, 0.0f);
			worldObj.spawnParticle("largesmoke", xCoord + worldObj.rand.nextFloat() * 2.0 - 0.5, yCoord + worldObj.rand.nextFloat() * 0.6f, zCoord + worldObj.rand.nextFloat() * 2.0 - 0.5, 0.0f, 0.1f, 0.0f);
		}

		if (energy > seedEnergy * 5.0f) // Juuuust making sure exploits aren't in
			this.energy = seedEnergy * 5.0f;
	}
	
	public boolean isDark()
	{
		return nightTransform > 0.5f;
	}

	public float affectEnemy( EntityLivingBase entity, float value )
	{
		if (!isDark())
		{
			return healEntityUnloadingEnergy(entity, value * 0.2f);
		}
		else
		{
			return damageEntityStoringEnergy(entity, value * 2.0f);
		}
	}

	public float damageEntityStoringEnergy( EntityLivingBase entity, float damage )
	{
		if (!worldObj.isRemote)
		{
			if (entity.attackEntityFrom(DamageSource.magic, damage))
			{
				this.energy += damage * 0.3;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}			
		}
		
		return damage;
	}

	public float healEntityUnloadingEnergy( EntityLivingBase entity, float healing )
	{
		float heal = Math.min(Math.min(energy, healing), entity.getMaxHealth() - entity.getHealth());
		
		if (heal > 0.0f)
		{
			if (!worldObj.isRemote)
			{
				entity.heal(heal);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				this.energy -= heal;
			}
		}
		
		return heal;
	}

	public void spawnSeed()
	{
		if (!worldObj.isRemote && this.energy > seedEnergy)
		{
			this.energy -= seedEnergy;

			ItemStack seed = new ItemStack(PlantMod.instance.itemEnergyPlant);
			EntityItem entityItem = new EntityItem(worldObj, this.xCoord + 0.5f, this.yCoord + 0.5f, this.zCoord + 0.5f, seed);
			worldObj.spawnEntityInWorld(entityItem);
		}
	}

	@Override
	public void writeToNBT( NBTTagCompound par1nbtTagCompound )
	{
		super.writeToNBT(par1nbtTagCompound);

		par1nbtTagCompound.setFloat("energy", energy);
		
		par1nbtTagCompound.setFloat("nightTransform", nightTransform);
		par1nbtTagCompound.setBoolean("transformsToNight", transformsToNight);
	}

	@Override
	public void readFromNBT( NBTTagCompound par1nbtTagCompound )
	{
		super.readFromNBT(par1nbtTagCompound);

		this.energy = par1nbtTagCompound.getFloat("energy");

		this.nightTransform = par1nbtTagCompound.getFloat("nightTransform");
		this.transformsToNight = par1nbtTagCompound.getBoolean("transformsToNight");
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
