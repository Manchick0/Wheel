package com.manchick.wheel.widget;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.manchick.wheel.Wheel;
import com.manchick.wheel.util.Registry;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * The WidgetLoader class is responsible for loading and managing widgets from JSON data.
 */
public class WidgetLoader extends JsonDataLoader {

    static final Logger LOGGER = LoggerFactory.getLogger(WidgetLoader.class);
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    final Registry<Identifier, Widget> ENTRIES = new Registry<>();
    private static WidgetLoader instance;

    private WidgetLoader() {
        super(GSON, "widgets");
    }

    public static WidgetLoader getInstance() {
        return instance == null ? instance = new WidgetLoader() : instance;
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
                var widget = result.get();
                ENTRIES.register(identifier, widget);
                it++;
            }
        }
        LOGGER.info("Loaded {} widgets", it);
        if(filterEntries("").size() > 8){
            LOGGER.warn("There are more than 8 root widgets loaded, some of them might be missing on the wheel!");
        }
    }

    public Optional<Widget> get(Identifier identifier){
        return ENTRIES.get(identifier);
    }

    public Set<Identifier> listEntries(){
        return ENTRIES.keySet();
    }

    public List<Widget> filterEntries(String subPath){
        return ENTRIES.entrySet().stream()
                .filter(entry -> stripSubPath(entry.getKey()).equals(subPath))
                .map(Map.Entry::getValue).toList();
    }

    private String stripSubPath(Identifier identifier){
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
                WidgetLoader loader = WidgetLoader.getInstance();
                return loader.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
            }
        });
    }
}
