package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.manchick.wheel.widget.action.ActionTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;

public class EchoAction extends Action {

    public static final MapCodec<EchoAction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(TextCodecs.CODEC.fieldOf("message").forGetter(EchoAction::getMessage),
                Codec.BOOL.fieldOf("overlay").orElse(false).forGetter(EchoAction::isOverlay))
                .apply(instance, EchoAction::new);
    });

    final Text message;
    final boolean overlay;

    public EchoAction(Text message, boolean overlay){
        this.message = message;
        this.overlay = overlay;
    }

    public Text getMessage() {
        return message;
    }

    public boolean isOverlay() {
        return overlay;
    }

    @Override
    public void run(MinecraftClient client) {
        assert client.player != null;
        client.player.sendMessage(message, overlay);
    }

    @Override
    public ActionType<?> getType() {
        return ActionTypes.ECHO;
    }
}
