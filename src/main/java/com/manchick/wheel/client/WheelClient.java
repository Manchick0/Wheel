package com.manchick.wheel.client;

import com.manchick.wheel.Wheel;
import com.manchick.wheel.client.screen.WheelScreen;
import com.manchick.wheel.widget.WidgetLoader;
import com.manchick.wheel.widget.action.ActionTypes;
import com.manchick.wheel.widget.action.type.AwaitAction;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.session.telemetry.WorldUnloadedEvent;
import net.minecraft.text.Text;
import net.minecraft.world.WorldEvents;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

public class WheelClient implements ClientModInitializer {

    public KeyBinding OPEN_KEY = new KeyBinding("key.wheel.open", GLFW.GLFW_KEY_Z, KeyBinding.INVENTORY_CATEGORY);

    @Override
    public void onInitializeClient() {
        WidgetLoader.register();
        ActionTypes.register();
        KeyBindingHelper.registerKeyBinding(OPEN_KEY);
        registerBuiltinPacks();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            tickKeys(client);
            AwaitAction.tick(client);
        });
    }

    @SuppressWarnings("UnstableApiUsage")
    public void registerBuiltinPacks(){
        Optional<ModContainer> optionalContainer = FabricLoader.getInstance().getModContainer("wheel");
        assert optionalContainer.isPresent();
        ModContainer container = optionalContainer.get();
        ResourceManagerHelperImpl.registerBuiltinResourcePack(Wheel.of("development"), "development", container, Text.literal("Development Widgets"), ResourcePackActivationType.NORMAL);
    }

    public void tickKeys(MinecraftClient client){
        if(OPEN_KEY.wasPressed()){
            if(Screen.hasControlDown()) return;
            client.setScreen(new WheelScreen());
        }
    }
}
