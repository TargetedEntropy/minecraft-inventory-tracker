package com.targetedentropy.inventorytracker;

import com.mojang.logging.LogUtils;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(inventorytracker.MODID)
public class inventorytracker
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "inventorytracker";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Store the Http endpoint
    private static String http_url = "http://main.tovdc.com:5050/items";


    public inventorytracker()
    {

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    // Register a command to send purge to API
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
            dispatcher.register(Commands.literal("purge")
                    .executes(context -> {
                        try {
                            return postItem("purge",  context.getSource().getPlayerOrException());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
            );

    }

    // Post to the API
    // ItemName
    // PlayerUUID
    public static int postItem(String itemName, Player player) throws IOException {
        URL url = new URL(http_url);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        String json = "{\"uuid\": \""+player.getUUID()+"\", \"item_name\": \""+itemName+"\"}";
        LOGGER.debug("JsonStr: {}", json);

        // Write the body to the stream
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return 1;
    }

    // placed a block or planted a seed
    @SubscribeEvent
    public void placeItem(PlayerInteractEvent.RightClickBlock event) throws IOException {
        ItemStack stack = event.getItemStack();
        ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(stack.getItem());
        Player player = event.getEntity();

        LOGGER.debug("Item placed: {}", itemName);
        postItem(String.valueOf(itemName), player);
    }

    // used an item like a potion
    @SubscribeEvent
    public void useItem(PlayerInteractEvent.EntityInteract event) throws IOException {
        ItemStack stack = event.getItemStack();
        ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(stack.getItem());

        LOGGER.debug("Item used: {}", itemName);
        postItem(String.valueOf(itemName), event.getEntity());
    }

    // Picked up an item from the ground
    @SubscribeEvent
    public void pickupItem(PlayerEvent.ItemPickupEvent event) throws IOException {
        ItemStack stack = event.getStack();
        ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(stack.getItem());

        LOGGER.debug("Item picked up: {}", itemName);
        postItem(String.valueOf(itemName), event.getEntity());
    }
}
