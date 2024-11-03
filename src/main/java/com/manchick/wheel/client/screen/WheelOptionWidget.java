package com.manchick.wheel.client.screen;

import com.manchick.wheel.Wheel;
import com.manchick.wheel.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WheelOptionWidget extends ButtonWidget {

    public static final Identifier EMPTY = Wheel.of("textures/gui/empty_widget.png");

    final MinecraftClient client;
    public final Widget widget;
    final int xOffset;
    final int yOffset;
    float animationProgress = 0;

    protected WheelOptionWidget(int x, int y, Widget widget, int xOffset, int yOffset) {
        super(x, y, 32, 32, Text.empty(), button -> {}, DEFAULT_NARRATION_SUPPLIER);
        this.widget = widget;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.setTooltip(Tooltip.of(widget.label()));
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();

        if(animationProgress < 1) animationProgress += (float) (0.25 * delta);
        matrices.translate(xOffset * animationProgress, yOffset * animationProgress, 0);
        hovered = isMouseOver(mouseX, mouseY);
        renderButton(context);
        matrices.pop();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        float xOff = xOffset * animationProgress;
        float yOff = yOffset * animationProgress;
        return this.active && this.visible && mouseX >= (double)this.getX() + xOff && mouseY >= (double)this.getY() + yOff && mouseX < (double)(this.getX() + this.width + xOff) && mouseY < (double)(this.getY() + this.height + yOff);
    }

    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return isMouseOver(mouseX, mouseY);
    }

    protected void renderButton(DrawContext context) {
        boolean displayHovered = isHovered() && !widget.actions().isEmpty();
        int color = (180 << 24) | (displayHovered ? 0x3c8527 : 0x2c2c2c);
        int x = getX();
        int y = getY();
        int contentX = x + 8;
        int contentY = y + 8;

        context.fill(x, y, x + width, y + height, color);
        ItemStack preview = widget.getStack();
        if(preview.isEmpty()) {
            context.drawTexture(EMPTY, contentX, contentY, 0, 0, 16, 16, 16, 16);
            return;
        }
        context.drawItem(preview, contentX, contentY);
    }

    @Override
    public void onPress() {
        MinecraftClient client = MinecraftClient.getInstance();
        Screen screen = client.currentScreen;
        boolean keepOpened = widget.keepOpened() || Screen.hasShiftDown();
        if(!keepOpened) {
            assert screen != null;
            screen.close();
        }
        widget.run(client);
    }
}
