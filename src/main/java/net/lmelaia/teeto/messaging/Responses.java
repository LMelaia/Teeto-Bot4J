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
