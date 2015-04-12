package net.ivorius.plantmod.client;

import net.ivorius.plantmod.CommonProxy;
import net.ivorius.plantmod.EventHandlerSound;
import net.ivorius.plantmod.TileEntityDetectionPlant;
import net.ivorius.plantmod.TileEntityEnergyPlant;
import net.ivorius.plantmod.TileEntityInstablePlant;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerSounds()
	{
		MinecraftForge.EVENT_BUS.register(new EventHandlerSound());		
	}

	@Override
	public void registerRenderers()
	{
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInstablePlant.class, new TileEntityInstablePlantRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDetectionPlant.class, new TileEntityDetectionPlantRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyPlant.class, new TileEntityEnergyPlantRenderer());
	}
}
