package net.lmelaia.teeto;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.util.ReflectionUtil;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

public class LogManager {

    /**
     * Default logging configuration.
     */
    private static final String DEFAULT_CONFIG_LOC = "configs/log4j.xml";

    /**
     * Class that initialized the logger.
     */
    private static final String initializingClassName = null;

    /**
     * Logger for this class
     */
    private static Logger logger;

    /**
     * Initializes the logger with a specified configuration file.
     *
     * @param configFile the logger configuration file. The {@link #DEFAULT_CONFIG_LOC default} is used
     *                   if no file is specified.
     */
    public static void initialize(String configFile){
        if(initializingClassName != null)
            throw new IllegalStateException("Cannot initialize a new logger - logger already initialized by: "
                    + initializingClassName);

        try {
            configure(configFile);
            logger = org.apache.logging.log4j.LogManager.getLogger();
        } catch (FileNotFoundException e) {
            try {
                configure(DEFAULT_CONFIG_LOC);
                logger = org.apache.logging.log4j.LogManager.getLogger();
                logger.error("The configuration file: " + configFile + " could not be found");
                logger.warn("Using default log configuration");
            } catch (FileNotFoundException e1) {
                System.err.println("No default logging configuration found");
                throw new IllegalStateException("No logging configuration could be found");
            }
        }
    }

    /**
     * Gets a logger for the calling class.
     *
     * @return a logger for the calling class.
     */
    public static Logger getLogger(){
        return org.apache.logging.log4j.LogManager.getLogger(ReflectionUtil.getCallerClass(2));
    }

    /**
     * Initializes the logger.
     *
     * @param configuration configuration file.
     * @throws FileNotFoundException if the configuration file was not found.
     */
    private static void configure(String configuration) throws FileNotFoundException {
        if(!configuration.startsWith("/") && !configuration.startsWith("\\"))
            configuration = "/" + configuration;

        LoggerContext context = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        try {
            context.setConfigLocation(LogManager.class.getResource(configuration).toURI());
        } catch (URISyntaxException | NullPointerException e) {
            throw new FileNotFoundException("The relative file: " + configuration + " does not exist.");
        }
    }
}
