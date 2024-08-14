package com.manchick.wheel.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Registry<T, Z> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Registry.class);
    final HashMap<T, Z> entries = new LinkedHashMap<>();

    public void register(T identifier, Z entry){
        entries.put(identifier, entry);
    }

    public boolean isRegistered(Z entry){
        return entries.containsValue(entry);
    }

    public Optional<Z> get(T identifier){
        return Optional.ofNullable(entries.get(identifier));
    }

    public T getIdentifier(Z entry){
        return entries.entrySet().stream()
                .filter(e -> e.getValue().equals(entry))
                .map(Map.Entry::getKey)
                .findFirst().orElseThrow(AssertionError::new);
    }

    public Set<Map.Entry<T, Z>> entrySet(){
        return entries.entrySet();
    }

    public void clear(){
        entries.clear();
    }

    public Codec<Z> getCodec(Codec<T> tCodec){
        return tCodec.comapFlatMap(t -> get(t).map(DataResult::success)
                .orElse(DataResult.error(() -> "Unknown type: " + t)), this::getIdentifier);
    }
}
