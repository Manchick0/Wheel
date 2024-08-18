package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.value.Operation;
import com.manchick.wheel.value.ValueStorage;
import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueAction extends Action {

    public static final MapCodec<ValueAction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Identifier.CODEC.fieldOf("id").forGetter(ValueAction::getIdentifier),
                        Operation.CODEC.fieldOf("operation").orElse(Operation.ASSIGN).forGetter(ValueAction::getOperation),
                        Codec.either(Identifier.CODEC, Codec.DOUBLE).fieldOf("value").forGetter(ValueAction::getValue))
                .apply(instance, ValueAction::new);
    });

    final Identifier identifier;
    final Operation operation;
    final Either<Identifier, Double> value;

    public ValueAction(Identifier id, Operation operation, Either<Identifier, Double> value){
        this.identifier = id;
        this.operation = operation;
        this.value = value;
    }

    @Override
    public void run(MinecraftClient client) {
        value.ifLeft(key -> apply(ValueStorage.get(key)));
        value.ifRight(this::apply);
    }

    public void apply(double value){
        double current = ValueStorage.get(identifier);
        double result = operation.apply(current, value);
        ValueStorage.set(identifier, result);
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public Operation getOperation() {
        return operation;
    }

    public Either<Identifier, Double> getValue() {
        return value;
    }

    @Override
    public ActionType<?> getType() {
        return ActionType.AWAIT;
    }
}
