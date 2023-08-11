package com.example.seedhelper;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
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
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SeedHelper.MODID)
public class SeedHelper
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "seedhelper";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public SeedHelper()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
            assert itemName != null;
            if (itemName.getPath().contains("seed")) {
                LOGGER.info("Seed: {}", itemName);
            }
        }

        List<ItemStack> ALL_SEEDS = ForgeRegistries.ITEMS.getValues().stream().map(item -> {

            ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
            assert itemName != null;
            if (itemName.getPath().contains("seed")) {
                return new ItemStack(item);
            }

            return ItemStack.EMPTY;
        }).filter(stack -> !stack.isEmpty()).toList();

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
