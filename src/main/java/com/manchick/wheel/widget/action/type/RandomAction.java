package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.Random;

public class RandomAction extends Action {

    public static final MapCodec<RandomAction> CODEC = Action.ACTIONS.fieldOf("actions").xmap(RandomAction::new, RandomAction::getActions);

    final List<Action> actions;

    public RandomAction(List<Action> actions){
        this.actions = actions;
    }

    @Override
    public void run(MinecraftClient client) {
        Random random = new Random();
        if(actions.size() == 1){
            if(random.nextBoolean()) {
                actions.getFirst().run(client);
            }
        } else {
            int index = random.nextInt(actions.size());
            actions.get(index).run(client);
        }
    }

    public List<Action> getActions() {
        return actions;
    }

    @Override
    public ActionType<?> getType() {
        return ActionType.RANDOM;
    }
}
