package net.lmelaia.teeto.command.commands;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.lmelaia.teeto.LogManager;
import net.lmelaia.teeto.Teeto;
import net.lmelaia.teeto.command.CommandHandler;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * A set of invisible commands that provide
 * control over the system. Such as shutdown
 * and reboot.
 */
public final class SystemCommands {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = LogManager.getLogger();

    //Private constructor.
    private SystemCommands(){}

    /**
     * Prints a message to the user,
     * shuts down the bot and closes
     * the application.
     *
     * @param channel the message channel the command
     *                came from.
     */
    @CommandHandler(".system.shutdown")
    public static void shutdown(MessageChannel channel){
        channel.sendMessage(Teeto.getTeeto().getResponses().getResponse("system.shutdown").get()).queue();
        Teeto.shutdown();
    }

    /**
     * Reboots the application. This only works
     * in a release environment.
     *
     * @param channel the message channel the command
     *                came from.
     */
    @CommandHandler(".system.reboot")
    public static void reboot(MessageChannel channel){
        channel.sendMessage(Teeto.getTeeto().getResponses().getResponse("system.reboot").get()).queue();
        File runScript = null;

        if(Teeto.isWindows())
            runScript = new File(System.getProperty("user.dir") + "/teetobot.bat");
        else if(Teeto.isUnix())
            runScript = new File(System.getProperty("user.dir") + "/teetobot");

        if(runScript == null){
            LOG.warn("Run script not found. Unknown OS maybe?");
            channel.sendMessage(Teeto.getTeeto().getResponses().getResponse("system.cant_reboot").get())
                    .queue();
            return;
        }


        if(!runScript.exists()){
            LOG.warn("Run script: " + runScript + " does not exist. Development environment maybe?");
            channel.sendMessage(Teeto.getTeeto().getResponses().getResponse("system.cant_reboot").get())
                    .queue();
            return;
        }

        try{
            Desktop.getDesktop().open(runScript);
            Teeto.shutdown();
        } catch (IOException e){
            LOG.error("Failed to reboot", e);
            channel.sendMessage(Teeto.getTeeto().getResponses().getResponse("message.cant_reboot").get())
                    .queue();
        }
    }
}
