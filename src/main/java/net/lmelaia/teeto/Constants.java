package net.lmelaia.teeto;

import java.io.File;

/**
 * Constant variables for the application as whole.
 *
 * For now this class holds paths to files,
 * both external and internal.
 */
@SuppressWarnings("WeakerAccess")
public final class Constants {

    //Private constructor.
    private Constants(){}

    /**
     * @return the path to the log4j2 config file within
     * the resources folder.
     */
    @SuppressWarnings("WeakerAccess")
    public static String getRelativeLog4jConfigFile(){
        return "config/log4j.xml";
    }

    /**
     * @return the bot config file (config/bot.config.json).
     */
    @SuppressWarnings("WeakerAccess")
    public static File getBotConfigFile(){
        return new File(Teeto.getRunDirectory() + "/config/bot.config.json");
    }

    /**
     * @return the .TOKEN file.
     */
    @SuppressWarnings("WeakerAccess")
    public static File getTokenFile(){
        return new File(Teeto.getRunDirectory() + "/.TOKEN");
    }
}
