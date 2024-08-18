package com.manchick.wheel.client;

import com.google.gson.*;
import com.manchick.wheel.Wheel;
import com.manchick.wheel.value.ValueStorage;
import com.manchick.wheel.widget.WidgetLoader;
import com.manchick.wheel.widget.action.ActionType;
import com.manchick.wheel.widget.action.type.AwaitAction;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class WheelClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WheelClient.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Override
    public void onInitializeClient() {
        WheelKeys.registerBuiltin();
        WidgetLoader.register();
        ActionType.register();
        registerBuiltinPacks();
        loadConfig();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            WheelKeys.tickKeys(client);
            AwaitAction.tick(client);
        });
    }

    public static void saveConfig(){
        File configFile = getConfigFile();
        if(!configFile.exists()) {
            LOGGER.warn("Couldn't find the config file. Proceeding to create one automatically.");
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                LOGGER.error("An error occurred whilst trying to create the config.", e);
            }
        }
        try(FileWriter writer = new FileWriter(configFile)) {
            JsonObject root = new JsonObject();
            ValueStorage.write(root);
            writer.write(GSON.toJson(root));
        } catch (IOException e) {
            LOGGER.error("An error occurred whilst trying to save the config.", e);
        }
    }

    public void loadConfig(){
        File configFile = getConfigFile();
        if(!configFile.exists()){
            saveConfig();
            return;
        }
        try(FileReader reader = new FileReader(configFile)) {
            JsonElement element = JsonParser.parseReader(reader);
            if(element.isJsonObject()){
                JsonObject root = element.getAsJsonObject();
                ValueStorage.load(root);
            } else LOGGER.error("The config file is not a valid JSON object.");
        } catch (IOException e) {
            LOGGER.error("An error occurred whilst trying to load the config.", e);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public void registerBuiltinPacks(){
        Optional<ModContainer> optionalContainer = FabricLoader.getInstance().getModContainer("wheel");
        assert optionalContainer.isPresent();
        ModContainer container = optionalContainer.get();
        ResourceManagerHelperImpl.registerBuiltinResourcePack(Wheel.of("development"), "development", container, Text.translatable("pack.development.title"), ResourcePackActivationType.NORMAL);
        ResourceManagerHelperImpl.registerBuiltinResourcePack(Wheel.of("utility"), "utility", container, Text.translatable("pack.utility.title"), ResourcePackActivationType.DEFAULT_ENABLED);
    }

    private static File getConfigFile(){
        FabricLoader loader = FabricLoader.getInstance();
        Path configDir = loader.getConfigDir();
        return new File(configDir.toFile(), "wheel.json");
    }
}
