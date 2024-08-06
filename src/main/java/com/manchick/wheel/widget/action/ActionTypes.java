package com.manchick.wheel.widget.action;

import com.manchick.wheel.widget.action.type.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.HashMap;
import java.util.Optional;

public class ActionTypes {

    static final HashMap<String, ActionType<?>> REGISTRY = new HashMap<>();

    public static final ActionType<EchoAction> ECHO = new ActionType<>(EchoAction.CODEC, "echo");
    public static final ActionType<CommandAction> COMMAND = new ActionType<>(CommandAction.CODEC, "command");
    public static final ActionType<OpenAction> OPEN = new ActionType<>(OpenAction.CODEC, "open");
    public static final ActionType<SoundAction> SOUND = new ActionType<>(SoundAction.CODEC, "sound");
    public static final ActionType<ClipboardAction> CLIPBOARD = new ActionType<>(ClipboardAction.CODEC, "clipboard");
    public static final ActionType<SuggestAction> SUGGEST = new ActionType<>(SuggestAction.CODEC, "suggest");
    public static final ActionType<LinkAction> LINK = new ActionType<>(LinkAction.CODEC, "link");
    public static final ActionType<AwaitAction> AWAIT = new ActionType<>(AwaitAction.CODEC, "await");

    public static void register(){
        register(ECHO);
        register(COMMAND);
        register(OPEN);
        register(SOUND);
        register(CLIPBOARD);
        register(SUGGEST);
        register(LINK);
        register(AWAIT);
    }

    static void register(ActionType<?> type){
        REGISTRY.put(type.identifier, type);
    }

    public static Codec<ActionType<?>> getCodec(){
        return Codec.STRING.comapFlatMap(str -> Optional.ofNullable(ActionTypes.REGISTRY.get(str))
                .map(DataResult::success).orElse(DataResult.error(() -> "Unknown action type: " + str)), ActionType::getIdentifier);
    }
}
