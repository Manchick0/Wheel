package com.manchick.wheel.client.screen;

import com.manchick.wheel.widget.Widget;
import com.manchick.wheel.widget.WidgetLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Language;

import java.util.HashMap;
import java.util.Map;

public class WheelScreen extends Screen {

    final HashMap<WidgetSlot, Widget> widgets;
    final String subPath;

    public WheelScreen(){
        this(""); // Root
    }

    public WheelScreen(String subPath){
        super(Text.empty());
        this.subPath = subPath;
        this.widgets = WidgetLoader.listEntries(subPath);
    }

    @Override
    protected void init() {
        for (Map.Entry<WidgetSlot, Widget> entry : widgets.entrySet()) {
            WidgetSlot slot = entry.getKey();
            Widget widget = entry.getValue();
            WheelOptionWidget option = new WheelOptionWidget(slot.getX(width), slot.getY(height), widget, slot.getXOffset(), slot.getYOffset());
            addDrawableChild(option);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Language language = Language.getInstance();
        String key = turnIntoTitleKey();
        if(language.hasTranslation(key) && !isRoot()){
            renderTitle(context, Text.translatable(key));
        }
    }

    public void renderTitle(DrawContext context, Text text){
        int centerY = height / 2;
        int centerX = width / 2;
        int y = centerY - 90;
        int x = centerX - (textRenderer.getWidth(text) / 2);
        context.drawTextWithShadow(textRenderer, text, x, y, 0xFFFFFF);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public boolean isRoot(){
        return subPath.isEmpty();
    }

    public String turnIntoTitleKey(){
        return "wheel.title." + subPath.replace("/", ".");
    }
}
