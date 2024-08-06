package com.manchick.wheel.widget.action;

import com.mojang.serialization.MapCodec;

public class ActionType<W extends Action> {

    final String identifier;
    final MapCodec<W> codec;

    public ActionType(MapCodec<W> codec, String identifier){
        this.codec = codec;
        this.identifier = identifier;
    }

    public MapCodec<W> getCodec(){
        return codec;
    }

    public String getIdentifier() {
        return identifier;
    }
}
