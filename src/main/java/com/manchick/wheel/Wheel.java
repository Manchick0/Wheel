package com.manchick.wheel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.manchick.wheel.client.WheelKeys;
import com.manchick.wheel.value.ValueStorage;
import com.manchick.wheel.widget.WidgetLoader;
import com.manchick.wheel.widget.action.ActionType;
import com.manchick.wheel.widget.action.type.AwaitAction;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Wheel implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Wheel.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static void saveConfig() {
        var path = configPath();
        if (!Files.exists(path)) {
            LOGGER.warn("Couldn't find the config file. Proceeding to create one automatically.");
            try {
                Files.createFile(path);
            } catch (IOException e) {
                LOGGER.error("An error occurred whilst trying to create the config.", e);
            }
        }
        try (var writer = Files.newBufferedWriter(path)) {
            JsonObject root = new JsonObject();
            ValueStorage.write(root);
            writer.write(GSON.toJson(root));
        } catch (IOException e) {
            LOGGER.error("An error occurred whilst trying to save the config.", e);
        }
    }

    public static void loadConfig() {
        var path = configPath();
        if (!Files.exists(path)) {
            saveConfig();
            return;
        }
        try (var reader = Files.newBufferedReader(path)) {
            var element = JsonParser.parseReader(reader);
            if (element.isJsonObject()) {
                var root = element.getAsJsonObject();
                ValueStorage.load(root);
            } else LOGGER.error("The config file is not a valid JSON object.");
        } catch (IOException e) {
            LOGGER.error("An error occurred whilst trying to load the config.", e);
        }
    }

    private static Path configPath() {
        FabricLoader loader = FabricLoader.getInstance();
        Path configDir = loader.getConfigDir();
        return configDir.resolve("wheel.json");
    }

    public static Identifier of(String path) {
        return Identifier.of("wheel", path);
    }

    @Override
    public void onInitializeClient() {
        WheelKeys.registerBuiltin();
        WidgetLoader.register();
        ActionType.register();
        registerBuiltinPacks();
        registerWidgetCommand();
        loadConfig();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            WheelKeys.tickKeys(client);
            AwaitAction.tick(client);
        });
    }

    public void registerWidgetCommand() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(ClientCommandManager.literal("widget")
                    .then(ClientCommandManager.argument("path", IdentifierArgumentType.identifier())
                            .suggests((ctx, builder) -> CommandSource.suggestMatching(WidgetLoader.getInstance().listEntries().stream().map(Identifier::toString), builder))
                            .executes(context -> {
                                var path = context.getArgument("path", Identifier.class);
                                var widget = WidgetLoader.getInstance().get(path);
                                if (widget.isPresent()) {
                                    widget.get().run(context.getSource().getClient());
                                    return 1;
                                } else {
                                    context.getSource().sendError(Text.translatable("commands.widget.not_found", path));
                                    return -1;
                                }
                            }))
            );
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    public void registerBuiltinPacks() {
        Optional<ModContainer> optionalContainer = FabricLoader.getInstance().getModContainer("wheel");
        assert optionalContainer.isPresent();
        ModContainer container = optionalContainer.get();
        ResourceManagerHelperImpl.registerBuiltinResourcePack(Wheel.of("development"), "development", container, Text.translatable("pack.development.title"), ResourcePackActivationType.NORMAL);
        ResourceManagerHelperImpl.registerBuiltinResourcePack(Wheel.of("utility"), "utility", container, Text.translatable("pack.utility.title"), ResourcePackActivationType.DEFAULT_ENABLED);
    }
}
