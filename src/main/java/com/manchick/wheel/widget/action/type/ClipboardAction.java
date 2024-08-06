package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.manchick.wheel.widget.action.ActionTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

public class ClipboardAction extends Action {

    public static final MapCodec<ClipboardAction> CODEC = Codec.STRING.fieldOf("content").xmap(ClipboardAction::new, ClipboardAction::getContent);

    final String content;

    public ClipboardAction(String content){
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public void run(MinecraftClient client) {
        client.keyboard.setClipboard(content);
    }

    @Override
    public ActionType<?> getType() {
        return ActionTypes.CLIPBOARD;
    }
}
