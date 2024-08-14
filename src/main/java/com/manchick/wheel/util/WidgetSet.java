package com.manchick.wheel.util;

import com.manchick.wheel.widget.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class WidgetSet {

    private final Widget[] widgets = new Widget[8];

    public static WidgetSet create(List<Widget> list){
        if(list.isEmpty()) return createEmpty();
        ArrayList<Widget> arrayList = new ArrayList<>(list);
        WidgetSet set = new WidgetSet();
        set.fillSlots(arrayList);
        set.removeIncluded(arrayList);
        set.fillEmpty(arrayList);
        set.fillEmpty(Widget.empty());
        return set;
    }

    public static WidgetSet createEmpty(){
        WidgetSet set = new WidgetSet();
        set.fillEmpty(Widget.empty());
        return set;
    }

    public void removeIncluded(List<Widget> list){
        for(int i = 0; i < 8; i++){
            Widget widget = widgets[i];
            if(widget == null) continue;
            list.remove(widget);
        }
    }

    public void fillSlots(List<Widget> list){
        for(Widget widget : list){
            if(!widget.hasSlotTaken()) continue;
            var slot = widget.takenSlot();
            assert slot.isPresent();
            fillSlot(slot.get(), widget);
        }
    }

    public void fillSlot(WidgetSlot slot, Widget widget){
        int index = slot.ordinal();
        widgets[index] = widget;
    }

    public void fillEmpty(Widget with){
        for(int i = 0; i < widgets.length; i++){
            if(widgets[i] != null) continue;
            widgets[i] = with;
        }
    }

    public void fillEmpty(List<Widget> with){
        outer: for(Widget widget : with){
            for (int i = 0; i < widgets.length; i++) {
                if (widgets[i] != null) continue;
                widgets[i] = widget;
                continue outer;
            }
        }
    }

    public void forEach(BiConsumer<WidgetSlot, Widget> action){
        for(int i = 0; i < 8; i++){
            WidgetSlot slot = WidgetSlot.values()[i];
            action.accept(slot, widgets[i]);
        }
    }
}
