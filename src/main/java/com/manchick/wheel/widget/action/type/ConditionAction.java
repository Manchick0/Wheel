package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.value.ValueStorage;
import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class ConditionAction extends Action {

    public static final Pattern PATTERN = Pattern.compile("^((\\d+(\\.\\d+)?|\\$[a-z_]+:[a-z_]+)\\s*([>=<])\\s*(\\d+(\\.\\d+)?|\\$[a-z_]:[a-z_]+))$");

    public static final Codec<String> EXPRESSION = Codec.STRING.validate(string -> {
        if(PATTERN.matcher(string).matches()){
            return DataResult.success(string);
        } else return DataResult.error(() -> "Invalid condition: " + string);
    });

    public static final MapCodec<ConditionAction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(EXPRESSION.fieldOf("expression").forGetter(ConditionAction::getExpression),
                Action.ACTIONS.fieldOf("if").forGetter(ConditionAction::listIfActions),
                Action.ACTIONS.fieldOf("else").orElse(List.of()).forGetter(ConditionAction::listElseActions))
                .apply(instance, ConditionAction::new);
    });

    final String expression;
    final List<Action> ifActions;
    final List<Action> elseActions;

    public ConditionAction(String expression, List<Action> ifActions, List<Action> elseActions) {
        this.expression = expression;
        this.ifActions = ifActions;
        this.elseActions = elseActions;
    }

    public List<Action> listIfActions() {
        return ifActions;
    }

    public List<Action> listElseActions() {
        return elseActions;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public void run(MinecraftClient client) {
        if(evaluateExpression()){
            ifActions.forEach(action -> action.run(client));
        } else elseActions.forEach(action -> action.run(client));
    }

    public boolean evaluateExpression(){
        Predicate<Character> isOperator = c -> c == '<' || c == '>' || c == '=';
        String leftPart = "";
        String rightPart = "";
        char operator = ' ';

        int cursor = 0;
        while (cursor < expression.length()){
            char current = expression.charAt(cursor);
            if(isOperator.test(current)){
                operator = current;
                leftPart = expression.substring(0, cursor);
                rightPart = expression.substring(cursor + 1);
                break;
            }
            cursor++;
        }

        double left = toDouble(leftPart);
        double right = toDouble(rightPart);
        return switch (operator){
            case '<' -> left < right;
            case '=' -> left == right;
            case '>' -> left > right;
            default -> false;
        };
    }

    public double toDouble(String string){
        string = string.trim();
        if(string.startsWith("$")){
            String dynamic = string.substring(1);
            return ValueStorage.get(Identifier.of(dynamic));
        }
        try {
            return Double.parseDouble(string);
        } catch (NumberFormatException e){
            return 0;
        }
    }

    @Override
    public ActionType<?> getType() {
        return ActionType.CONDITION;
    }
}
