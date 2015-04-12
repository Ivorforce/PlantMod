package net.ivorius.plantmod.client;

import java.util.Random;

import net.ivorius.plantmod.TileEntityDetectionPlant;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class TileEntityDetectionPlantRenderer extends TileEntitySpecialRenderer
{
	public ModelPlantOre modelPlantOre;
	public ResourceLocation texture;
	public ResourceLocation textureGrayscale;
	public ResourceLocation textureGrayscale2;

	public TileEntityDetectionPlantRenderer()
	{
		this.modelPlantOre = new ModelPlantOre();
		texture = new ResourceLocation("plantmod:models/modelPlantOre.png");
		textureGrayscale = new ResourceLocation("plantmod:models/modelPlantOreGrayscale.png");
		textureGrayscale2 = new ResourceLocation("plantmod:models/modelPlantOreGrayscale2.png");
	}

	public void renderTileEntityDetectionPlantAt( TileEntityDetectionPlant tileentity, double d0, double d1, double d2, float f )
	{
		long i1 = (long) (tileentity.xCoord * 3129871) ^ (long) tileentity.yCoord * 116129781L ^ (long) tileentity.zCoord;
		i1 = i1 * i1 * 42317861L + i1 * 11L;
		d0 += ((double) ((float) (i1 >> 16 & 15L) / 15.0F) - 0.5D) * 0.5D;
		d1 += ((double) ((float) (i1 >> 20 & 15L) / 15.0F) - 1.0D) * 0.2D;
		d2 += ((double) ((float) (i1 >> 24 & 15L) / 15.0F) - 0.5D) * 0.5D;
		// Grass translation code, borrowed from RenderBlocks

		double rotation = new Random(i1).nextDouble();
		GL11.glPushMatrix();
		GL11.glTranslated(d0 + 0.5, d1 + 0.75, d2 + 0.5);
		GL11.glRotated(rotation * 360.0, 0.0, 1.0, 0.0);

		GL11.glScaled(0.5, 0.5, 0.5);
		GL11.glRotated(180.0, 1.0, 0.0, 0.0);

		this.bindTexture(texture);
		this.modelPlantOre.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		char c0 = 61680;
		int j = c0 % 65536;
		int k = c0 / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j / 1.0F, (float) k / 1.0F);

		float alpha1 = Math.min((MathHelper.sin((tileentity.ticksAliveVisual + f) * 0.05f) + 1.0f) * 0.5f * Math.min(tileentity.alpha * 3.0f, 1.0f), 1.0f);
		float alpha2 = Math.min((-MathHelper.sin((tileentity.ticksAliveVisual + f) * 0.05f) + 1.0f) * 0.5f * Math.min(tileentity.alpha * 3.0f, 1.0f), 1.0f);
		GL11.glColor4f(tileentity.red, tileentity.green, tileentity.blue, alpha1);
		this.bindTexture(textureGrayscale);
		this.modelPlantOre.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glColor4f(tileentity.red, tileentity.green, tileentity.blue, alpha2);
		this.bindTexture(textureGrayscale2);
		this.modelPlantOre.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_BLEND);

		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt( TileEntity tileentity, double d0, double d1, double d2, float f )
	{
		this.renderTileEntityDetectionPlantAt((TileEntityDetectionPlant) tileentity, d0, d1, d2, f);
	}
}
