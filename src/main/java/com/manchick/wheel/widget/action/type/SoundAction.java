package com.manchick.wheel.widget.action.type;

import com.manchick.wheel.widget.action.Action;
import com.manchick.wheel.widget.action.ActionType;
import com.manchick.wheel.widget.action.ActionTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class SoundAction extends Action {

    public static final MapCodec<SoundAction> CODEC = RecordCodecBuilder.mapCodec(instance -> {
        return instance.group(Codec.optionalField("id", Identifier.CODEC, false).forGetter(SoundAction::getId),
                        Codecs.NONNEGATIVE_INT.fieldOf("volume").orElse(1).forGetter(SoundAction::getVolume),
                        Codecs.rangedInt(0, 2).fieldOf("pitch").orElse(1).forGetter(SoundAction::getPitch),
                        Codec.BOOL.fieldOf("stop").orElse(false).forGetter(SoundAction::shouldStop))
                .apply(instance, SoundAction::new);
    });

    final Optional<Identifier> id;
    final int volume;
    final int pitch;
    final boolean stop;

    public SoundAction(Optional<Identifier> id, int volume, int pitch, boolean stop){
        this.id = id;
        this.volume = volume;
        this.pitch = pitch;
        this.stop = stop;
    }

    public int getPitch(){
        return pitch;
    }

    public int getVolume(){
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
        return ActionTypes.SOUND;
    }
}
