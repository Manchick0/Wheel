package com.manchick.wheel.value;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValueStorage {

    private static final Map<Identifier, Double> ENTRIES = new ConcurrentHashMap<>();

    public static double get(Identifier identifier){
        return ENTRIES.getOrDefault(identifier, 0.0);
    }

    public static void set(Identifier identifier, double value){
        ENTRIES.put(identifier, value);
    }

    /**
     * Loads the values from the provided {@link JsonObject} and populates the {@link ValueStorage} class.
     *
     * @param object the {@link JsonObject} containing the values to be loaded
     */
    public static void load(JsonObject object){
        JsonArray values = object.getAsJsonArray("values");
        for(int i = 0; i < values.size(); i++){
            JsonObject entryObject = values.get(i).getAsJsonObject();
            Identifier id = Identifier.of(entryObject.get("id").getAsString());
            double value = entryObject.get("value").getAsDouble();
            ENTRIES.put(id, value);
        }
    }

    /**
     * Writes the values stored in the {@link ValueStorage} class to the parsed {@link JsonObject}.
     *
     * @param object the {@link JsonObject} object to write the values to
     */
    public static void write(JsonObject object){
        JsonArray values = new JsonArray();
        for(Map.Entry<Identifier, Double> entry : ENTRIES.entrySet()){
            JsonObject entryObject = new JsonObject();
            entryObject.addProperty("id", entry.getKey().toString());
            entryObject.addProperty("value", entry.getValue());
            values.add(entryObject);
        }
        object.add("values", values);
    }

    public static String toString(Identifier identifier){
        String str = Double.toString(get(identifier));
        int pointIndex = str.indexOf('.');
        if(pointIndex != -1){
            char decimalDigit = str.charAt(++pointIndex);
            if(decimalDigit == '0' && str.length() - 1 == pointIndex){
                return str.substring(0, pointIndex - 1);
            }
            return str;
        }
        return str;
    }
}
