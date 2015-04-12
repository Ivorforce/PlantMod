package net.ivorius.plantmod;

import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.event.ForgeSubscribe;

public class EventHandlerSound
{
	@ForgeSubscribe
	public void onSound( SoundLoadEvent event )
	{
		try
		{
			event.manager.soundPoolSounds.addSound("ninjamod:poof.ogg");
			event.manager.soundPoolSounds.addSound("ninjamod:swordAoE.ogg");
		}
		catch (Exception e)
		{
			System.err.println("ShinobiMod: Failed to register one or more sounds.");
		}
	}
}
