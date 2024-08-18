package com.manchick.wheel.value;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

import java.util.Random;
import java.util.function.BiFunction;

public enum Operation implements StringIdentifiable {

    ASSIGN("assign", (a, b) -> b),
    SUM("sum", Double::sum),
    DIFFERENCE("difference", (a, b) -> a - b),
    PRODUCT("product", (a, b) -> a * b),
    QUOTIENT("quotient", (a, b) -> a / b),
    REMAINDER("remainder", (a, b) -> a % b),
    POWER("power", Math::pow),
    SQRT("sqrt", (a, b) -> Math.sqrt(b)),
    SINE("sin", (a, b) -> Math.sin(b)),
    COSINE("cos", (a, b) -> Math.cos(b)),
    MAX("max", Math::max),
    MIN("min", Math::min),
    RANDOM("random", (a, b) -> new Random().nextDouble() * b),
    ROUND("round", (a, b) -> (double) Math.round(b));

    final String name;
    final BiFunction<Double, Double, Double> function;

    public static final Codec<Operation> CODEC = StringIdentifiable.createBasicCodec(Operation::values);

    Operation(String name, BiFunction<Double, Double, Double> function) {
        this.name = name;
        this.function = function;
    }

    public double apply(double a, double b){
        return function.apply(a, b);
    }

    @Override
    public String asString() {
        return name;
    }
}
