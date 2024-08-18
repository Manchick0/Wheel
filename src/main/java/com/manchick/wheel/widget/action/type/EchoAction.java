package com.manchick.wheel.widget.action.type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.manchick.wheel.value.ValueStorage;
import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EchoAction extends Action {

    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

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
        client.player.sendMessage(bakeText(message), overlay);
    }

    public Text bakeText(Text rawText){
        MutableText result = Text.empty();
        List<Text> parts = new ArrayList<>(rawText.getSiblings());
        parts.addFirst(MutableText.of(rawText.getContent()).setStyle(rawText.getStyle()));
        for(Text part : parts){
            Style style = part.getStyle();
            String string = part.getString();
            String bakedString = bakeString(string);
            result.append(Text.literal(bakedString).setStyle(style));
        }
        return result;
    }

    public String bakeString(String rawString){
        String[] parts = rawString.split(" ");
        String[] result = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if(part.isEmpty()) continue;
            if (part.startsWith("$(") && part.endsWith(")")) {
                String key = part.substring(2, part.length() - 1);
                result[i] = ValueStorage.toString(Identifier.of(key));
            } else result[i] = part;
        }
        return String.join(" ", result);
    }

    @Override
    public ActionType<?> getType() {
        return ActionType.ECHO;
    }
}
