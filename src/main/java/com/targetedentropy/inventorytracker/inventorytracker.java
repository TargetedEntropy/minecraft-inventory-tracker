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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Command;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

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


    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
            dispatcher.register(Commands.literal("purge")
                    .executes(context -> {
                        try {
                            return postItem("test",  context.getSource().getPlayerOrException());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
            );

    }

//    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
//        dispatcher.register(Commands.literal("sbitem")
//                .requires(source -> source.hasPermission(2))
//                .then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("item", StringArgumentType.string()).suggests(SUGGEST_SBITEM)
//                        .executes(context -> give(StringArgumentType.getString(context, "item"), EntityArgument.getPlayer(context, "targets"), new CompoundTag()))
//                        .then(Commands.argument("skyblockData", CompoundTagArgument.compoundTag())
//                                .executes(context -> give(StringArgumentType.getString(context, "item"), EntityArgument.getPlayer(context, "targets"), CompoundTagArgument.getCompoundTag(context, "skyblockData")))))));
//    }

    private static int registerAlias(CommandDispatcher<CommandSourceStack> dispatcher, String alias, String command) {
        dispatcher.register(Commands.literal(alias)
                .executes((commandContext) -> {
                    dispatcher.execute(command, commandContext.getSource());
                    return Command.SINGLE_SUCCESS;
                })
        );
        return Command.SINGLE_SUCCESS;
    }


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
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        return 1;
    }

    @SubscribeEvent
    public void placeItem(PlayerInteractEvent.RightClickBlock event) throws IOException {
        ItemStack stack = event.getItemStack();
        ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(stack.getItem());
        Player player = event.getEntity();

        LOGGER.debug("Item placed: {}", itemName);
        postItem(String.valueOf(itemName), player);
    }
}
