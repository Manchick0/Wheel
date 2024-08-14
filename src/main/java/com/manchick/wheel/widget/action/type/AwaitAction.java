package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.dynamic.Codecs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AwaitAction extends Action {

    static final HashMap<List<Action>, Integer> ENTRIES = new HashMap<>();

    public static final MapCodec<AwaitAction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codec.either(Action.CODEC, Codec.list(Action.CODEC)).fieldOf("actions").forGetter(AwaitAction::getAction),
                        Codecs.POSITIVE_INT.fieldOf("delay").forGetter(AwaitAction::getDelay))
                .apply(instance, AwaitAction::new);
    });

    final Either<Action, List<Action>> action;
    final int delay;

    public AwaitAction(Either<Action, List<Action>> action, int delay){
        this.action = action;
        this.delay = delay;
    }

    @Override
    public void run(MinecraftClient client) {
        action.ifLeft(left -> ENTRIES.put(List.of(left), delay));
        action.ifRight(right -> ENTRIES.put(right, delay));
    }

    public static void tick(MinecraftClient client){
        ENTRIES.replaceAll((task, ticksLeft) -> ticksLeft - 1);
        ENTRIES.entrySet().stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Map.Entry::getKey)
                .forEach(list -> list.forEach(action -> action.run(client)));
        ENTRIES.entrySet().removeIf(entry -> entry.getValue() == 0);
    }

    public static void clearCache(){
        ENTRIES.clear();
    }

    public int getDelay() {
        return delay;
    }

    public Either<Action, List<Action>> getAction() {
        return action;
    }

    @Override
    public ActionType<?> getType() {
        return ActionType.AWAIT;
    }
}
