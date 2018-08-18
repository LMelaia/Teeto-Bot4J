package net.lmelaia.teeto;

import com.google.gson.Gson;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.lmelaia.teeto.util.FileUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

/**
 * Bot class.
 * <p>
 * Singleton class providing access to the discord bot.
 * As well as main logic for the bot instance.
 */
public class Teeto {

    /**
     * Public static final {@link Gson} instance.
     * <p>
     * Single static instance provided to
     * minimise object construction.
     */
    @SuppressWarnings("WeakerAccess")
    public static final Gson GSON = new Gson();

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Singleton instance.
     */
    private static Teeto TEETO;

    /**
     * Configuration options for the bot. File: config/teeto.json
     */
    @SuppressWarnings("unused")
    private TeetoConfig teetoConfig = TeetoConfig.getConfig();

    /**
     * Discord API instance.
     */
    private JDA javaDiscordAPI;

    /**
     * private constructor.
     * <p>
     * Builds a new Discord API instance and connects the bot
     * to discord.
     *
     * @throws InterruptedException If this thread is interrupted while waiting
     * @throws LoginException       if the provided token in the .TOKEN file is
     *                              invalid (e.i. malformed, incorrect)
     */
    private Teeto() throws InterruptedException, LoginException {
        JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(getToken());

        builder.setGame(Game.of(Game.GameType.DEFAULT,
                teetoConfig.getName() + " v" + teetoConfig.getVersion() + " | " + teetoConfig.getHelpCommand()));

        try {
            javaDiscordAPI = builder.build().awaitReady();
        } catch (InterruptedException e) {
            LOG.log(Level.FATAL, "Failed to login", e);
            throw e;
        } catch (LoginException e) {
            LOG.log(Level.FATAL, "Invalid login token.");
            throw e;
        }
    }

    /**
     * @return the Discord API instance.
     */
    @SuppressWarnings("unused")
    public JDA getJavaDiscordAPI() {
        return javaDiscordAPI;
    }

    //############################
    //   Static (de)init methods
    //############################

    /**
     * Initializes a new bot instance and logs it in.
     *
     * @return the newly constructed bot instance.
     * @throws LoginException       if the provided token in the .TOKEN file
     *                              is invalid (i.e. malformed or incorrect).
     * @throws InterruptedException If this thread is interrupted while waiting
     */
    @SuppressWarnings({"UnusedReturnValue", "WeakerAccess"})
    public static Teeto initTeeto() throws LoginException, InterruptedException {
        if (TEETO == null)
            TEETO = new Teeto();
        else
            throw new IllegalStateException("Teeto already initialized.");

        return TEETO;
    }

    /**
     * Disconnects and nullifies the current bot instance
     * and teeto instance.
     */
    @SuppressWarnings("WeakerAccess")
    public static void unInitTeeto() {
        if (TEETO == null)
            throw new IllegalStateException("Teeto not yet initialized.");

        if (TEETO.javaDiscordAPI == null) {
            LOG.log(Level.DEBUG, "Bot not running. Skipping disconnect");
        } else {
            LOG.log(Level.DEBUG, "Shutting down bot...");
            TEETO.javaDiscordAPI.shutdown();
        }

        TEETO.javaDiscordAPI = null;
        TEETO = null;
    }

    /**
     * {@link #unInitTeeto() unitializes} the current teeto bot instance
     * and exits the program.
     */
    @SuppressWarnings("WeakerAccess")
    public static void shutdown() {
        LOG.log(Level.INFO, "Shutdown requested");

        if (TEETO != null)
            unInitTeeto();

        LOG.log(Level.INFO, "Shutdown complete");
        System.exit(0);
    }

    /**
     * @return the current teeto instance.
     */
    @SuppressWarnings("unused")
    public static Teeto getTeeto() {
        if (TEETO == null)
            throw new IllegalStateException("Teeto not yet initialized.");

        return TEETO;
    }

    //##########################
    //   Static util methods
    //##########################

    /**
     * @return the home directory the programming
     * is running from.
     *
     * This will be the home project directory in a
     * development environment and the parent folder
     * to /bin in a release environment.
     */
    @SuppressWarnings("WeakerAccess")
    public static File getRunDirectory() {
        String runDir = System.getProperty("user.dir");

        if (runDir.endsWith("bin") || runDir.endsWith("bin/")) {
            return new File(runDir.replace("bin", ""));
        }

        return new File(runDir);
    }

    //##########################
    //  Private helper methods
    //##########################

    /**
     * @return the token in string form from the .TOKEN file.
     */
    private static String getToken() {
        try {
            return FileUtil.readFile(Constants.getTokenFile());
        } catch (IOException e) {
            LOG.log(Level.FATAL, "Failed to read token file", e);
            shutdown();
        }

        return null;
    }
}
