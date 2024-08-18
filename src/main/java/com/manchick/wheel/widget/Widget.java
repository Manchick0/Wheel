package com.manchick.wheel.widget;

import com.google.gson.JsonElement;
import com.manchick.wheel.util.WidgetSlot;
import com.manchick.wheel.widget.action.Action;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record Widget(Text label, Either<RegistryEntry<Item>, ItemStack> preview, List<Action> actions, Optional<WidgetSlot> takenSlot, boolean keepOpened) {

    static final Logger LOGGER = LoggerFactory.getLogger(Widget.class);

    public static final Codec<Widget> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(TextCodecs.CODEC.fieldOf("label").forGetter(Widget::label),
                Codec.either(ItemStack.ITEM_CODEC, ItemStack.CODEC).fieldOf("preview").forGetter(Widget::preview),
                Codec.list(Action.CODEC).fieldOf("actions").forGetter(Widget::actions),
                Codec.optionalField("take_slot", WidgetSlot.CODEC, false).forGetter(Widget::takenSlot),
                Codec.BOOL.fieldOf("keep_opened").orElse(false).forGetter(Widget::keepOpened))
                .apply(instance, Widget::new);
    });

    public static Optional<Widget> deserialize(JsonElement element){
        DataResult<Widget> result = CODEC.parse(JsonOps.INSTANCE, element);
        return result.resultOrPartial(err -> LOGGER.error("An error occurred whilst trying to deserialize a widget: {}", err));
    }

    public void run(MinecraftClient client){
        actions.forEach(action -> action.run(client));
    }

    public ItemStack getStack(){
        return preview.map(ItemStack::new, Function.identity());
    }

    public boolean hasSlotTaken(){
        return takenSlot.isPresent();
    }

    public static Widget empty(){
        Text label = newLine()
                .append(space()).append(Text.translatable("widget.wheel.empty")).append(space()).append(newLine())
                .append(newLine())
                .append(space()).append(Text.translatable("widget.wheel.empty_description").formatted(Formatting.GRAY)).append(space()).append(newLine());
        return new Widget(label, Either.right(ItemStack.EMPTY), List.of(), Optional.empty(), true);
    }

    private static MutableText newLine(){
        return Text.literal("\n");
    }

    private static MutableText space(){
        return Text.literal(" ");
    }
}
