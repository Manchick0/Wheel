package com.manchick.wheel.widget.action;

import com.manchick.wheel.util.Registry;
import com.manchick.wheel.widget.action.type.*;
import com.mojang.serialization.MapCodec;

@FunctionalInterface
public interface ActionType<T extends Action> {

    Registry<String, ActionType<?>> REGISTRY = new Registry<>();

    ActionType<EchoAction> ECHO = () -> EchoAction.CODEC;
    ActionType<CommandAction> COMMAND = () -> CommandAction.CODEC;
    ActionType<OpenAction> OPEN = () -> OpenAction.CODEC;
    ActionType<SoundAction> SOUND = () -> SoundAction.CODEC;
    ActionType<ClipboardAction> CLIPBOARD = () -> ClipboardAction.CODEC;
    ActionType<SuggestAction> SUGGEST = () -> SuggestAction.CODEC;
    ActionType<LinkAction> LINK = () -> LinkAction.CODEC;
    ActionType<AwaitAction> AWAIT = () -> AwaitAction.CODEC;
    ActionType<RandomAction> RANDOM = () -> RandomAction.CODEC;
    ActionType<ValueAction> VALUE = () -> ValueAction.CODEC;
    ActionType<ConditionAction> CONDITION = () -> ConditionAction.CODEC;

    MapCodec<T> codec();

    static void register(){
        REGISTRY.register("echo", ECHO);
        REGISTRY.register("command", COMMAND);
        REGISTRY.register("open", OPEN);
        REGISTRY.register("sound", SOUND);
        REGISTRY.register("clipboard", CLIPBOARD);
        REGISTRY.register("suggest", SUGGEST);
        REGISTRY.register("link", LINK);
        REGISTRY.register("await", AWAIT);
        REGISTRY.register("random", RANDOM);
        REGISTRY.register("value", VALUE);
        REGISTRY.register("condition", CONDITION);
    }
}
