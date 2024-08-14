package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.mixin.ChatScreenAccessor;
import com.manchick.wheel.mixin.MinecraftClientInvoker;
import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.dynamic.Codecs;

public class SuggestAction extends Action {

    public static final MapCodec<SuggestAction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
         return instance.group(Codec.STRING.fieldOf("content").forGetter(SuggestAction::getContent),
                Codecs.NONNEGATIVE_INT.fieldOf("cursor").orElse(-1).forGetter(SuggestAction::getCursor))
                .apply(instance, SuggestAction::new);
    });

    final String content;
    final int cursor;

    public SuggestAction(String content, int cursor){
        this.content = content;
        this.cursor = cursor;
    }

    public String getContent() {
        return content;
    }

    public int getCursor() {
        return cursor;
    }

    @Override
    public void run(MinecraftClient client) {
        ((MinecraftClientInvoker) client).invokeOpenChatScreen(content);
        if(client.currentScreen instanceof ChatScreen screen && cursor > 0) {
            TextFieldWidget chatField = ((ChatScreenAccessor) screen).getChatField();
            chatField.setCursor(cursor, false);
        }
    }

    @Override
    public ActionType<?> getType() {
        return ActionType.SUGGEST;
    }
}
