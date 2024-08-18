package com.manchick.wheel.widget.action;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.client.MinecraftClient;

import java.util.List;

public abstract class Action {

    public static final Codec<Action> CODEC = ActionType.REGISTRY.getCodec(Codec.STRING).dispatch(Action::getType, ActionType::codec);

    public static final Codec<List<Action>> ACTIONS = Codec.either(Codec.list(CODEC), CODEC).comapFlatMap(either -> either.map(DataResult::success, action -> DataResult.success(List.of(action))), Either::left);

    public abstract void run(MinecraftClient client);

    public abstract ActionType<?> getType();
}
