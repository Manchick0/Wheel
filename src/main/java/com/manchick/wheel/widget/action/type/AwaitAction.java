package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AwaitAction extends Action {

    static final Map<UUID, Entry> ENTRIES = new ConcurrentHashMap<>();

    public static final MapCodec<AwaitAction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Action.ACTIONS.fieldOf("actions").forGetter(AwaitAction::getActions),
                        Codecs.POSITIVE_INT.fieldOf("delay").forGetter(AwaitAction::getDelay))
                .apply(instance, AwaitAction::new);
    });

    final List<Action> actions;
    final int delay;

    public AwaitAction(List<Action> actions, int delay){
        this.actions = actions;
        this.delay = delay;
    }

    @Override
    public void run(MinecraftClient client) {
        ENTRIES.put(UUID.randomUUID(), new Entry(actions, delay));
    }

    public static void tick(MinecraftClient client){
        ENTRIES.replaceAll((uuid, entry) -> new Entry(entry.actions, entry.ticksLeft - 1));
        ENTRIES.values().stream()
                .filter(entry -> entry.ticksLeft <= 0)
                .forEach(entry -> entry.actions.forEach(action -> action.run(client)));
        ENTRIES.values().removeIf(entry -> entry.ticksLeft <= 0);
    }

    public static void clearCache(){
        ENTRIES.clear();
    }

    public int getDelay() {
        return delay;
    }

    public List<Action> getActions() {
        return actions;
    }

    @Override
    public ActionType<?> getType() {
        return ActionType.AWAIT;
    }

    private record Entry(List<Action> actions, int ticksLeft) {
        // Why is 6 afraid of 7? Math.sin(Math.toRadians(21));
    }
}