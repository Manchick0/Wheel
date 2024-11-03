package com.manchick.wheel.client;

import com.manchick.wheel.client.screen.WheelScreen;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class WheelKeys {

    public static final KeyBinding OPEN_KEY = new KeyBinding("key.wheel.open", GLFW.GLFW_KEY_Z, KeyBinding.INVENTORY_CATEGORY);

    public static void registerBuiltin(){
        KeyBindingHelper.registerKeyBinding(OPEN_KEY);
    }

    public static void tickKeys(MinecraftClient client){
        if(!OPEN_KEY.wasPressed() || Screen.hasControlDown()) return;
        client.setScreen(new WheelScreen());
    }
}
