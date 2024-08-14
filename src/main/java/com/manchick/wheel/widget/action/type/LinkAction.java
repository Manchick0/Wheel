package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class LinkAction extends Action {

    static final Logger LOGGER = LoggerFactory.getLogger(LinkAction.class);

    public static final MapCodec<LinkAction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codec.STRING.fieldOf("destination").forGetter(LinkAction::getDestination),
                        Codec.BOOL.fieldOf("ignore_confirmation").orElse(false).forGetter(LinkAction::shouldIgnoreConfirmation))
                .apply(instance, LinkAction::new);
    });

    final String destination;
    final boolean ignoreConfirmation;

    public LinkAction(String destination, boolean ignoreConfirmation){
        this.destination = destination;
        this.ignoreConfirmation = ignoreConfirmation;
    }

    public String getDestination() {
        return destination;
    }

    public boolean shouldIgnoreConfirmation() {
        return ignoreConfirmation;
    }

    @Override
    public void run(MinecraftClient client) {
        URI uri;
        try {
            if(destination.equals(":run")){
                uri = client.runDirectory.toURI();
            } else uri = URI.create(destination);
        } catch (IllegalArgumentException e){
            LOGGER.error("Invalid URI: {}", destination, e);
            return;
        }
        if(ignoreConfirmation){
            Util.getOperatingSystem().open(uri);
            return;
        }
        client.setScreen(new ConfirmLinkScreen((confirmed) -> {
            if (confirmed) {
                Util.getOperatingSystem().open(uri);
            }
            client.setScreen(null);
        }, destination, true));
    }

    @Override
    public ActionType<?> getType() {
        return ActionType.LINK;
    }
}
