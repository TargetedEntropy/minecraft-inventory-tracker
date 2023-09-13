package com.targetedentropy.inventorytracker;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;



// The value here should match an entry in the META-INF/mods.toml file
@Mod(inventorytracker.MODID)
public class inventorytracker
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "inventorytracker";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public inventorytracker()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }



    @SubscribeEvent
    public void placeItem(PlayerInteractEvent.RightClickBlock event)
    {
        ItemStack stack = event.getItemStack();
        ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(stack.getItem());
        Player player = event.getEntity();

        LOGGER.info("Item placed: {}", itemName);

    }
}
