package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class SoundAction extends Action {

    public static final MapCodec<SoundAction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codec.optionalField("id", Identifier.CODEC, false).forGetter(SoundAction::getId),
                        Codec.floatRange(0, Float.MAX_VALUE).fieldOf("volume").orElse(1f).forGetter(SoundAction::getVolume),
                        Codec.floatRange(0, 2).fieldOf("pitch").orElse(1f).forGetter(SoundAction::getPitch),
                        Codec.BOOL.fieldOf("stop").orElse(false).forGetter(SoundAction::shouldStop))
                .apply(instance, SoundAction::new);
    });

    final Optional<Identifier> id;
    final float volume;
    final float pitch;
    final boolean stop;

    public SoundAction(Optional<Identifier> id, float volume, float pitch, boolean stop){
        this.id = id;
        this.volume = volume;
        this.pitch = pitch;
        this.stop = stop;
    }

    public float getPitch(){
        return pitch;
    }

    public float getVolume(){
        return volume;
    }

    public Optional<Identifier> getId() {
        return id;
    }

    public boolean shouldStop(){
        return stop;
    }

    @Override
    public void run(MinecraftClient client) {
        SoundManager manager = client.getSoundManager();
        if(!shouldStop()){
            playSound(manager);
        } else stopSound(manager);
    }

    public void playSound(SoundManager manager){
        if(id.isEmpty()) return;
        SoundEvent event = SoundEvent.of(id.get());
        PositionedSoundInstance instance = PositionedSoundInstance.master(event, pitch, volume);
        manager.play(instance);
    }

    public void stopSound(SoundManager manager){
        if(id.isPresent()){
            manager.stopSounds(id.get(), SoundCategory.MASTER);
        } else manager.stopAll();
    }

    @Override
    public ActionType<?> getType() {
        return ActionType.SOUND;
    }
}
