package com.manchick.wheel.client;

import com.manchick.wheel.Wheel;
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

import java.util.Optional;

public class WheelClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WheelKeys.registerBuiltin();
        WidgetLoader.register();
        ActionType.register();
        registerBuiltinPacks();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            WheelKeys.tickKeys(client);
            AwaitAction.tick(client);
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    public void registerBuiltinPacks(){
        Optional<ModContainer> optionalContainer = FabricLoader.getInstance().getModContainer("wheel");
        assert optionalContainer.isPresent();
        ModContainer container = optionalContainer.get();
        ResourceManagerHelperImpl.registerBuiltinResourcePack(Wheel.of("development"), "development", container, Text.translatable("pack.development.title"), ResourcePackActivationType.NORMAL);
        ResourceManagerHelperImpl.registerBuiltinResourcePack(Wheel.of("utility"), "utility", container, Text.translatable("pack.utility.title"), ResourcePackActivationType.DEFAULT_ENABLED);
    }
}
