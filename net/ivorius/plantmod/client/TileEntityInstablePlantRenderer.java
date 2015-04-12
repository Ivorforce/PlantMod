package net.ivorius.plantmod.client;

import java.util.Random;

import net.ivorius.plantmod.TileEntityInstablePlant;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class TileEntityInstablePlantRenderer extends TileEntitySpecialRenderer
{
	public ModelInstablePlant model;
	public ResourceLocation texture;
	
	public TileEntityInstablePlantRenderer()
	{
		model = new ModelInstablePlant();
		texture = new ResourceLocation("plantmod:models/modelInstablePlant.png");
	}
	
	public void renderTileEntityInstablePlantAt( TileEntityInstablePlant tileentity, double d0, double d1, double d2, float f )
	{
		long i1 = (long) (tileentity.xCoord * 3129871) ^ (long) tileentity.yCoord * 116129781L ^ (long) tileentity.zCoord;
		i1 = i1 * i1 * 42317861L + i1 * 11L;
		d0 += ((double) ((float) (i1 >> 16 & 15L) / 15.0F) - 0.5D) * 0.25D;
		d1 += ((double) ((float) (i1 >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
		d2 += ((double) ((float) (i1 >> 24 & 15L) / 15.0F) - 0.5D) * 0.25D;
		// Grass translation code, borrowed from RenderBlocks

		double rotation = new Random(i1).nextDouble();
		GL11.glPushMatrix();
		GL11.glTranslated(d0 + 0.5, d1 + 0.5, d2 + 0.5);
		GL11.glRotated(rotation * 360.0, 0.0, 1.0, 0.0);
        
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 1, 0);
        GL11.glRotatef(180, 0, 0, 1);
        bindTexture(texture);
        GL11.glDisable(GL11.GL_CULL_FACE);
        model.renderCull = false;
        model.render(null, tileentity.ratioEating, tileentity.ticksAliveVisual + f, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glEnable(GL11.GL_CULL_FACE);
        model.renderCull = true;
        model.render(null, tileentity.ratioEating, tileentity.ticksAliveVisual + f, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
        
        if (tileentity.wantedItem != null)
        {
            GL11.glPushMatrix();
            GL11.glTranslated(0.0, 1.0, 0.0);
            GL11.glRotated(tileentity.ticksAliveVisual + f, 0.0, 1.0, 0.0);
			renderFloatingItem(tileentity.wantedItem, tileentity.getWorldObj());
            GL11.glPopMatrix();        	
        }
        
        GL11.glPopMatrix();
	}
	
	public static void renderFloatingItem(ItemStack stack, World world)
	{
        EntityItem var3 = new EntityItem(world, 0.0D, 0.0D, 0.0D, stack);
        var3.getEntityItem().stackSize = 1;
        var3.hoverStart = 0.0F;

        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(var3, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;
	}

	@Override
	public void renderTileEntityAt( TileEntity tileentity, double d0, double d1, double d2, float f )
	{
		this.renderTileEntityInstablePlantAt((TileEntityInstablePlant) tileentity, d0, d1, d2, f);
	}
}
