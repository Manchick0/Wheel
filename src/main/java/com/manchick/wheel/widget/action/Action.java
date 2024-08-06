package com.manchick.wheel.widget.action;

import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;

public abstract class Action {

    public static final Codec<Action> CODEC = ActionTypes.getCodec().dispatch(Action::getType, ActionType::getCodec);

    public abstract void run(MinecraftClient client);

    public abstract ActionType<?> getType();
}
