package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.client.screen.WheelScreen;
import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.manchick.wheel.widget.action.ActionTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.MinecraftClient;

public class OpenAction extends Action {

    public static final Codec<String> PATH = Codec.STRING.validate(string -> {
        if(string.matches("[a-zA-Z0-9_/]*")){
            return DataResult.success(string);
        } else return DataResult.error(() -> "Invalid path: " + string);
    });

    public static final MapCodec<OpenAction> CODEC = PATH.fieldOf("path").xmap(OpenAction::new, OpenAction::getSubPath);

    final String subPath;

    public OpenAction(String subPath){
        this.subPath = subPath;
    }

    public String getSubPath() {
        return subPath;
    }

    @Override
    public void run(MinecraftClient client) {
        client.setScreen(new WheelScreen(subPath));
    }

    @Override
    public ActionType<?> getType() {
        return ActionTypes.OPEN;
    }
}
