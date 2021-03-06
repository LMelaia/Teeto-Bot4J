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
package net.lmelaia.teeto;

import com.google.gson.Gson;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.lmelaia.teeto.aud.AudioManager;
import net.lmelaia.teeto.command.CommandManager;
import net.lmelaia.teeto.messaging.BotMessageHandler;
import net.lmelaia.teeto.messaging.Responses;
import net.lmelaia.teeto.util.FileUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

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
    public static final Gson GSON = new Gson();

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Logs events.
     */
    private static final EventLogger EVENT_LOGGER = new EventLogger();

    /**
     * The OS name the application is running on.
     */
    private static final String OS = System.getProperty("os.name").toLowerCase();

    /**
     * Singleton instance.
     */
    private static Teeto TEETO;

    /**
     * List of responses stored on file.
     */
    private final Responses responses;

    /**
     * The command manager instance for the application.
     */
    private final CommandManager commandManager;

    /**
     * The audio manager instance for the application.
     */
    private final AudioManager audioManager;

    /**
     * Configuration options for the bot. File: config/teeto.json
     */
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
        Thread.currentThread().setUncaughtExceptionHandler(new ApplicationUncaughtExceptionHandler());
        LOG.info("Set exception handler.");
        this.responses = new Responses();

        JDABuilder builder = new JDABuilder(AccountType.BOT).setToken(getToken());
        builder.addEventListener(EVENT_LOGGER);

        //noinspection ConstantConditions
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

        this.audioManager = AudioManager.init();
        this.commandManager = CommandManager.init(javaDiscordAPI, teetoConfig.getCommandPrefixes());
        BotMessageHandler.init(javaDiscordAPI);
    }

    /**
     * @return the Discord API instance.
     */
    public JDA getJavaDiscordAPI() {
        return javaDiscordAPI;
    }

    /**
     * @return the configuration settings for the bot.
     * This includes the name, version, command prefixes
     * and help command.
     */
    public TeetoConfig getTeetoConfig(){
        return this.teetoConfig;
    }

    /**
     * @return the responses stored on file.
     */
    public Responses getResponses(){
        return this.responses;
    }

    /**
     * @return The command manager instance for the application.
     */
    public CommandManager getCommandManager(){
        return this.commandManager;
    }

    /**
     * @return The audio manager instance for the application.
     */
    public AudioManager getAudioManager(){
        return this.audioManager;
    }

    /**
     * Disconnects the bot from all voice channels.
     */
    private void disconnectAllBotsFromVoice(){
        LOG.info("Disconnecting all bots.");
        for(Guild g : javaDiscordAPI.getGuilds()){
            Teeto.getTeeto().getAudioManager().getAudioPlayer(g).disconnectFromVoice();
            LOG.info("Disconnected bot from guild: " + g.getName());
        }
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
    @SuppressWarnings("UnusedReturnValue")
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
    public static void unInitTeeto() {
        if (TEETO == null)
            throw new IllegalStateException("Teeto not yet initialized.");

        if (TEETO.javaDiscordAPI == null) {
            LOG.log(Level.DEBUG, "Bot not running. Skipping disconnect");
        } else {
            LOG.log(Level.DEBUG, "Shutting down bot...");
            TEETO.disconnectAllBotsFromVoice();
            TEETO.javaDiscordAPI.shutdown();
        }

        TEETO.javaDiscordAPI = null;
        TEETO = null;
    }

    /**
     * {@link #unInitTeeto() unitializes} the current teeto bot instance
     * and exits the program.
     */
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
    public static File getRunDirectory() {
        String runDir = System.getProperty("user.dir");

        if (runDir.endsWith("bin") || runDir.endsWith("bin/")) {
            return new File(runDir.replace("bin", ""));
        }

        return new File(runDir);
    }

    /**
     * @return the runtime directory without modification.
     * This guaranteed to be the directory the application
     * is running from.
     */
    static File getAbsoluteRunDirectory(){
        return new File(System.getProperty("user.dir"));
    }

    /**
     * @return {@code true} if the application
     * is running on windows.
     */
    public static boolean isWindows() {
        return (OS.contains("win"));
    }

    /**
     * @return {@code true} if the application
     * is running on linux/unix.
     */
    public static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0 );
    }

    //##########################
    //  Private helper methods
    //##########################

    /**
     * @return the token in string form from the .TOKEN file.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static String getToken() {
        try {
            File tokenFile = Constants.getTokenFile();

            if(!tokenFile.exists()){
                tokenFile.createNewFile();

                OutputStreamWriter writer
                        = new OutputStreamWriter(new FileOutputStream(tokenFile), StandardCharsets.UTF_8);

                writer.write("Put secret bot token in this file. " +
                        "Do not include spaces or new lines!");
                writer.flush();
                writer.close();

                LOG.fatal("Token file not found at boot-up. Please enter secret bot token in the .TOKEN file.");
                Teeto.shutdown();
                return null;
            }


            return FileUtil.readFile(Constants.getTokenFile());
        } catch (IOException e) {
            LOG.log(Level.FATAL, "Failed to read token file", e);
            shutdown();
        }

        return null;
    }

    /**
     * Implements various events and logs
     * the event when fired. This makes
     * debugging network errors and
     * other serious runtime errors.
     */
    private static class EventLogger implements EventListener {

        /**
         * Logger for this class.
         */
        private static final Logger LOG = LogManager.getLogger();

        /**
         * Determines the event and calls the appropriate method.
         *
         * @param evt the event.
         */
        @Override
        public void onEvent(Event evt) {
            if(evt instanceof DisconnectEvent)
                logDisconnectEvent((DisconnectEvent) evt);
            else if(evt instanceof ShutdownEvent)
                logShutdownEvent((ShutdownEvent) evt);
            else if(evt instanceof ExceptionEvent)
                logExceptionEvent((ExceptionEvent) evt);
            else if(evt instanceof StatusChangeEvent)
                logStatusChangeEvent((StatusChangeEvent) evt);
            else if(evt instanceof ReconnectedEvent)
                logReconnectedEvent((ReconnectedEvent) evt);
            else if(evt instanceof ResumedEvent)
                logResumedEvent((ResumedEvent) evt);
            else if(evt instanceof ReadyEvent)
                logReadyEvent((ReadyEvent) evt);
            else if(evt instanceof GuildVoiceMoveEvent){
                GuildVoiceMoveEvent event = (GuildVoiceMoveEvent) evt;
                if(event.getMember().getUser().getIdLong() == Teeto.getTeeto().javaDiscordAPI.getSelfUser().getIdLong())
                    Teeto.getTeeto().commandManager.invokeCommand(".audio.play", event.getGuild());
                LOG.info("Resetting audio because bot was moved.");
            }
        }

        //###############
        //  LOG METHODS
        //###############

        private void logReadyEvent(ReadyEvent evt) {
            LOG.info("Ready");
        }

        private void logResumedEvent(ResumedEvent evt) {
            LOG.info("Resumed");
        }

        private void logReconnectedEvent(ReconnectedEvent evt) {
            LOG.info("Reconnected");
        }

        private void logStatusChangeEvent(StatusChangeEvent evt) {
            LOG.debug(String.format("Status changed from %s to %s",
                    evt.getOldStatus().name(), evt.getNewStatus().name()));
        }

        private void logExceptionEvent(ExceptionEvent evt) {
            if(!evt.isLogged())
                LOG.error("Exception went unlogged in discord api: ", evt.getCause());
        }

        private void logShutdownEvent(ShutdownEvent evt) {
            LOG.info(String.format("Shutting down. Details: [code: %s, code_reason: %s, at:%s]",
                    evt.getCloseCode().getCode(), evt.getCloseCode().getMeaning(),
                    evt.getShutdownTime().format(DateTimeFormatter.ISO_DATE_TIME)));
        }

        private void logDisconnectEvent(DisconnectEvent event){
            LOG.warn(String.format(
                    "DISCONNECTED. Details: [is_server: %s, at:%s]",
                    event.isClosedByServer(),
                    event.getDisconnectTime().format(DateTimeFormatter.ISO_DATE_TIME)));
        }
    }

    /**
     * Handles uncaught exceptions on the main thread.
     */
    private static class ApplicationUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

        /**
         * Logs the error and shuts the application down.
         *
         * @param t thread on which the error occurred.
         * @param e the cause of the error.
         */
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            LOG.fatal(String.format("Exception went uncaught and propagated up [%s]", t.getName()), e);
            shutdown();
        }
    }
}
