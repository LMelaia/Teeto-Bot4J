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
package net.lmelaia.teeto.command.commands;

import net.dv8tion.jda.core.entities.Game;
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
     * in a release environment on windows.
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

        if(Teeto.isWindows()) {
            try {
                Desktop.getDesktop().open(runScript);
                Teeto.shutdown();
            } catch (IOException e) {
                LOG.error("Failed to reboot", e);
                channel.sendMessage(Teeto.getTeeto().getResponses().getResponse("message.cant_reboot").get())
                        .queue();
            }
        } else if(Teeto.isUnix()){
            try {
                Process p = Runtime.getRuntime().exec(new String[]{"bash", "" + runScript.getAbsolutePath() + ""});
                Teeto.shutdown();
            } catch (IOException e) {
                LOG.error("Failed to reboot", e);
                channel.sendMessage(Teeto.getTeeto().getResponses().getResponse("message.cant_reboot").get())
                        .queue();
            }
        }
    }

    /**
     * Updates the bots Game presence.
     *
     * @return The response to the user.
     */
    @CommandHandler(".system.update_game")
    public static String updateGame(){
        Teeto.getTeeto().getJavaDiscordAPI().getPresence().setGame(Game.of(Game.GameType.DEFAULT,
                Teeto.getTeeto().getTeetoConfig().getName()
                        + " v"
                        + Teeto.getTeeto().getTeetoConfig().getVersion()
                        + " | "
                        + Teeto.getTeeto().getTeetoConfig().getHelpCommand()));

        return Teeto.getTeeto().getResponses().getResponse("system.updated").get();
    }
}
