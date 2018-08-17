package net.lmelaia.teeto;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;


/**
 * Entry class.
 */
public class Main {

    /**
     * Logger for this class.
     */
    private static final Logger LOG;

    /*
     * Initializes the logger.
     */
    static{
        LogManager.initialize("config/log4j.xml");
        LOG = LogManager.getLogger();
    }

    /**
     * Main method.
     *
     * @param args program arguments.
     */
    public static void main(String[] args) {
        LOG.log(Level.INFO, "Starting new Teeto Bot instance.");
        LOG.log(Level.INFO, "Run directory: " + Teeto.getRunDirectory());
        try {
            Teeto.initTeeto();
        } catch (LoginException | InterruptedException e) {
            Teeto.shutdown();
        }
    }
}
