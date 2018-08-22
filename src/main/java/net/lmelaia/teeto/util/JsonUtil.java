package net.lmelaia.teeto.util;

import com.google.gson.JsonArray;

/**
 * Gson json utilities.
 */
public final class JsonUtil {

    //Private constructor.
    private JsonUtil(){}

    /**
     * Constructs a new String array from a json array.
     *
     * @param jsonArray the json array.
     * @return the newly constructed String array.
     */
    public static String[] jsonArrayToStringArray(JsonArray jsonArray) {
        int arraySize = jsonArray.size();
        String[] stringArray = new String[arraySize];

        for(int i=0; i<arraySize; i++) {
            stringArray[i] = jsonArray.get(i).getAsString();
        }

        return stringArray;
    }
}
