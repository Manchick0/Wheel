package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.manchick.wheel.widget.action.ActionTypes;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.Random;

public class RandomAction extends Action {

    public static final MapCodec<RandomAction> CODEC = Codec.either(Action.CODEC, Codec.list(Action.CODEC)).fieldOf("actions").xmap(RandomAction::new, RandomAction::getActions);

    final Either<Action, List<Action>> actions;

    public RandomAction(Either<Action, List<Action>> actions){
        this.actions = actions;
    }

    @Override
    public void run(MinecraftClient client) {
        Random random = new Random();
        var left = actions.left();
        var right = actions.right();
        if(right.isPresent()){
            var list = right.get();
            int index = random.nextInt(list.size());
            list.get(index).run(client);
            return;
        }
        assert left.isPresent();
        if(random.nextBoolean()) {
            left.get().run(client);
        }
    }

    public Either<Action, List<Action>> getActions() {
        return actions;
    }

    @Override
    public ActionType<?> getType() {
        return ActionTypes.RANDOM;
    }
}
