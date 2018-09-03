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

import net.lmelaia.teeto.Constants;
import net.lmelaia.teeto.LogManager;
import net.lmelaia.teeto.Teeto;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Provides a way to get the responses from
 * the responses.properties file and do
 * processing on them.
 */
public class Responses {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Properties from responses.properties.
     */
    private final Properties responses = new Properties();

    /**
     * Constructs a new properties instance
     * and loads the properties from file.
     */
    public Responses(){
        try {
            responses.load(new FileReader(Constants.getResponsesFile()));
        } catch (IOException e) {
            LOG.fatal("Failed to load responses file", e);
            Teeto.shutdown();
        }
    }

    /**
     * Gets the response from file as a Response object.
     *
     * @param responseID the ID (or key) of the response.
     * @return the newly constructed response object.
     */
    public Response getResponse(String responseID){
        if(responses.getProperty(responseID) == null){
            LOG.warn("Response with ID: " + responseID + " was not found.");
            return new Response(responseID);
        }

        return new Response(responses.getProperty(responseID));
    }
}
