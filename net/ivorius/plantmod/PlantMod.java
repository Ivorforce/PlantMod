package net.ivorius.plantmod;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "MiniIvPlantMod", name = "Mini: Plant Mod", version = "1.0")
@NetworkMod(clientSideRequired = true)
public class PlantMod
{
	@Instance(value = "MiniIvPlantMod")
	public static PlantMod instance;

	public Item itemInstablePlant;
	public Block blockInstablePlant;

	public Item itemDetectionPlant;
	public Block blockDetectionPlant;

	public Item itemEnergyPlant;
	public Block blockEnergyPlant;

	public WorldGenerator detectionPlantGen;

	@SidedProxy(clientSide = "net.ivorius.plantmod.client.ClientProxy", serverSide = "net.ivorius.plantmod.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit( FMLPreInitializationEvent event )
	{
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		int itemInstablePlantID = config.getItem("itemInstablePlantID", 12200).getInt();
		int blockInstablePlantID = config.getBlock("blockInstablePlantID", 1220).getInt();
		int itemDetectionPlantID = config.getItem("itemDetectionPlantID", 12201).getInt();
		int blockDetectionPlantID = config.getBlock("blockDetectionPlantID", 1221).getInt();
		int itemEnergyPlantID = config.getItem("itemEnergyPlantID", 12202).getInt();
		int blockEnergyPlantID = config.getBlock("blockEnergyPlantID", 1222).getInt();

		config.load();

		blockInstablePlant = new BlockInstablePlant(blockInstablePlantID).setUnlocalizedName("instablePlant");
		itemInstablePlant = new ItemSeeds(itemInstablePlantID, blockInstablePlant.blockID, Block.mycelium.blockID).setUnlocalizedName("instablePlant").setTextureName("plantmod:instablePlant").setCreativeTab(CreativeTabs.tabDecorations);
		GameRegistry.registerTileEntity(TileEntityInstablePlant.class, "instablePlant");

		blockDetectionPlant = new BlockDetectionPlant(blockDetectionPlantID).setUnlocalizedName("detectionPlant").setHardness(0.2f);
		itemDetectionPlant = new ItemSoillessPlant(itemDetectionPlantID, blockDetectionPlant.blockID, Block.grass.blockID).setUnlocalizedName("detectionPlant").setTextureName("plantmod:detectionPlant").setCreativeTab(CreativeTabs.tabDecorations);
		GameRegistry.registerTileEntity(TileEntityDetectionPlant.class, "detectionPlant");

		blockEnergyPlant = new BlockEnergyPlant(blockEnergyPlantID).setUnlocalizedName("energyPlant").setHardness(0.2f);
		itemEnergyPlant = new ItemSoillessPlant(itemEnergyPlantID, blockEnergyPlant.blockID, Block.grass.blockID).setUnlocalizedName("energyPlant").setTextureName("plantmod:energyPlant").setCreativeTab(CreativeTabs.tabDecorations);
		GameRegistry.registerTileEntity(TileEntityEnergyPlant.class, "energyPlant");

		config.save();
	}

	@EventHandler
	public void load( FMLInitializationEvent event )
	{
		proxy.registerSounds();
		proxy.registerRenderers();

		LanguageRegistry.addName(blockInstablePlant, "Instable Plant");
		LanguageRegistry.addName(itemInstablePlant, "Instable Plant");

		LanguageRegistry.addName(blockDetectionPlant, "Detection Plant");
		LanguageRegistry.addName(itemDetectionPlant, "Detection Plant");

		LanguageRegistry.addName(blockEnergyPlant, "Energy Plant");
		LanguageRegistry.addName(itemEnergyPlant, "Energy Plant");

		TileEntityInstablePlant.setUp();
		TileEntityDetectionPlant.setUp();
		
		this.detectionPlantGen = new WorldGenFlowers(blockDetectionPlant.blockID);
	}
	
	@EventHandler
	public void postInit( FMLPostInitializationEvent event )
	{

	}
}
