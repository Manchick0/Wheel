package com.manchick.wheel;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class Wheel implements ModInitializer {

    @Override
    public void onInitialize() {

    }

    public static Identifier of(String path){
        return Identifier.of("wheel", path);
    }
}
