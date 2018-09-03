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
package net.lmelaia.teeto.messaging;

/**
 * Represents a response stored in the responses.properties
 * file. This class allows replacing of placeholders
 * before retrieving the response.
 */
public class Response {

    /**
     * The response text.
     */
    private String responseText;

    /**
     * Constructs a new response.
     *
     * @param templateResponse the response text as stored in the responses file.
     */
    Response(String templateResponse){
        this.responseText = templateResponse;
    }

    /**
     * Replaces a set placeholder in the response with the given String.
     * This works like {@link String#replace(char, char)}.
     *
     * @param placeholder the placeholder
     * @param replacement the replacement.
     * @return {@code this}.
     */
    public Response setPlaceholder(String placeholder, String replacement){
        this.responseText = responseText.replace(placeholder, replacement);
        return this;
    }

    /**
     * @return the response with all placeholders set.
     */
    public String get(){
        return responseText;
    }
}
