/*
 *  This file is part of TeetoBot4J.
 *
 *  TeetoBot4J is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TeetoBot4J is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TeetoBot4J.  If not, see <https://www.gnu.org/licenses/>.
 */
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
