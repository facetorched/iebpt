package com.facetorched.iebpt;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import minetweaker.MineTweakerAPI;

@Mod(modid = IEBPTMod.MODID, version = IEBPTMod.VERSION, name = IEBPTMod.NAME, dependencies = "required-after:ImmersiveEngineering;")
public class IEBPTMod
{
    public static final String MODID = "iebpt";
    public static final String VERSION = "1.1.3";
    public static final String NAME = "Immersive Engineering Blueprint Tweaker";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	if (Loader.isModLoaded("MineTweaker3"))
		{
			MineTweakerAPI.registerClass(Blueprint.class);
			MineTweakerAPI.registerClass(Manual.class);
		}
    }
}
