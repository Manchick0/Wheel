package com.manchick.wheel.widget;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.manchick.wheel.Wheel;
import com.manchick.wheel.client.screen.WidgetSlot;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class WidgetLoader extends JsonDataLoader {

    static final LinkedHashMap<Identifier, Widget> ENTRIES = new LinkedHashMap<>();
    static final Logger LOGGER = LoggerFactory.getLogger(WidgetLoader.class);
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public WidgetLoader() {
        super(GSON, "widgets");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        int it = 0;
        ENTRIES.clear();
        for(Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()){
            JsonElement element = entry.getValue();
            Identifier identifier = entry.getKey();
            Optional<Widget> result = Widget.deserialize(element);
            if(result.isPresent()){
                ENTRIES.put(identifier, result.get());
                it++;
            }
        }
        LOGGER.info("Loaded {} widgets", it);
        if(filterEntries("").size() > 8){
            LOGGER.warn("There are more than 8 root widgets loaded, some of them might be missing on the wheel!");
        }
    }

    public static HashMap<WidgetSlot, Widget> listEntries(String subPath){
        var map = new HashMap<WidgetSlot, Widget>();
        var filtered = new ArrayList<>(filterEntries(subPath));
        filtered.stream().filter(Widget::hasSlotTaken).forEach(widget -> {
            var slot = widget.takenSlot;
            assert slot.isPresent();
            map.put(slot.get(), widget);
        });
        filtered.removeIf(map::containsValue);
        int i = 0;
        for(WidgetSlot slot : WidgetSlot.values()){
            if(map.containsKey(slot)) continue;
            if(i >= filtered.size()) break;
            map.put(slot, filtered.get(i));
            i++;
        }
        if(map.size() < 8){
            for(WidgetSlot slot : WidgetSlot.values()){
                if(map.containsKey(slot)) continue;
                map.put(slot, Widget.empty());
                if(map.size() >= 8) break;
            }
        }
        return map;
    }

    public static List<Widget> filterEntries(String subPath){
        return ENTRIES.entrySet().stream()
                .filter(entry -> stripSubPath(entry.getKey()).equals(subPath))
                .map(Map.Entry::getValue).toList();
    }

    static String stripSubPath(Identifier identifier){
        String path = identifier.getPath();
        if(path.contains("/")){
            String[] parts = path.split("/");
            return String.join("/", Arrays.copyOfRange(parts, 0, parts.length - 1));
        }
        return ""; // Root
    }

    public static void register(){
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {

            @Override
            public Identifier getFabricId() {
                return Wheel.of("widgets");
            }

            @Override
            public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
                WidgetLoader loader = new WidgetLoader();
                return loader.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
            }
        });
    }
}
