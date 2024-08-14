package com.manchick.wheel.widget.action;

import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;

public abstract class Action {

    public static final Codec<Action> CODEC = ActionType.REGISTRY.getCodec(Codec.STRING).dispatch(Action::getType, ActionType::codec);

    public abstract void run(MinecraftClient client);

    public abstract ActionType<?> getType();
}
