package com.manchick.wheel.util;


import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum WidgetSlot implements StringIdentifiable {

    TOP_LEFT(-49, -49, -8, -8),
    TOP(-15, -53, 0, -8),
    TOP_RIGHT(20, -49, 8, -8),
    LEFT(-53, -15, -8, 0),
    RIGHT(24, -15, 8, 0),
    BOTTOM_LEFT(-49, 21, -8, 8),
    BOTTOM(-15, 25, 0, 8),
    BOTTOM_RIGHT(21, 22, 8, 8);

    public static final Codec<WidgetSlot> CODEC = StringIdentifiable.createBasicCodec(WidgetSlot::values);

    final int x;
    final int y;
    final int xOffset;
    final int yOffset;

    WidgetSlot(int x, int y, int xOffset, int yOffset) {
        this.x = x;
        this.y = y;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public int getX(int width) {
        return width / 2 + x;
    }

    public int getY(int height) {
        return height / 2 + y;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    @Override
    public String asString() {
        return this.toString().toLowerCase();
    }
}
