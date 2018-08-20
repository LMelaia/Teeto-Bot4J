package net.lmelaia.teeto;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Represents the json file teeto.json within
 * the resources folder.
 *
 * This object will hold the configuration for the bot.
 */
public class TeetoConfig {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * The name of the bot.
     */
    private String name;

    /**
     * The version of the bot.
     */
    private String version;

    /**
     * The commands or command prefixes
     * used to message the bot.
     */
    private String[] commandPrefixes;

    /**
     * The bots help command.
     */
    private String helpCommand;

    //Private constructor.
    private TeetoConfig(){}

    /**
     * @return the name of the bot.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the version of the bot.
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return The commands or command
     * prefixes used to message the bot.
     */
    public String[] getCommandPrefixes() {
        return commandPrefixes;
    }

    /**
     * @return a new instance of this class with the
     * values initialized from the bot config file.
     */
    static TeetoConfig getConfig() {
        try {
            return Teeto.GSON.fromJson(new FileReader(
                    Constants.getBotConfigFile()), TeetoConfig.class);
        } catch (FileNotFoundException e) {
            LOG.log(Level.FATAL, "Bot config file not found", e);
            Teeto.shutdown();
        }

        return null;
    }

    /**
     * @return the help command for the bot.
     */
    public String getHelpCommand() {
        return helpCommand;
    }
}
